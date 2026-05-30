package com.alora.carelog.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/profiles/{profileId}/chat")
@RequiredArgsConstructor
public class AssistantController {

    @Value("${app.gemini-api-key}")
    private String apiKey;

    @PostMapping
    public ResponseEntity<Map<String, String>> procesarMensajeIA(
            @PathVariable Long profileId,
            @RequestBody ChatRequest request) {

        if (request == null || request.mensaje == null || request.mensaje.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "respuesta", "Mensaje vacío.",
                    "accion", "NINGUNA"
            ));
        }

        try {
            String prompt = """
                    Eres Alora, una asistente médica virtual altamente empática, paciente y profesional, especializada en el cuidado geriátrico.
                    Tu objetivo es analizar el mensaje del cuidador, darle una respuesta humana de apoyo y determinar qué acción técnica debe ejecutar el sistema.
                    
                    MENSAJE DEL CUIDADOR: "%s"
                    
                    INSTRUCCIONES ESTRICTAS:
                    1. ACTITUD: Responde de forma muy cálida y comprensiva. Si reportan una tarea médica, felicítalos o dales ánimos. MÁXIMO 2 líneas de texto.
                    2. ANÁLISIS DE INTENCIÓN (Clasificación):
                       - Si el cuidador indica que YA SE HA REALIZADO una tarea médica, comida o rutina (ej. "ya comió", "se tomó la pastilla", "desayunó", "listo"), la acción debe ser "COMPLETADO".
                       - Si el cuidador pide RETRASAR una tarea, poner una alarma para más tarde o aplazar algo (ej. "recuérdamelo en un rato", "pospón la alarma", "ahora no"), la acción debe ser "POSPONER".
                       - Si el usuario solo saluda, hace una pregunta general, o el texto no encaja claramente en las anteriores, la acción debe ser "NINGUNA".
                    3. REGLA DE FORMATO (CRÍTICA): Es OBLIGATORIO que tu única salida sea un objeto JSON válido. NO incluyas bloques de código Markdown (como ```json). NO añadas ningún texto antes ni después de las llaves {}.
                    
                    ESTRUCTURA EXACTA DEL JSON REQUERIDA:
                    {
                      "respuesta": "tu respuesta empática aquí",
                      "accion": "COMPLETADO" o "POSPONER" o "NINGUNA"
                    }
                    """.formatted(request.mensaje);

            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);

            JSONArray partsArray = new JSONArray();
            partsArray.put(textPart);

            JSONObject contentObj = new JSONObject();
            contentObj.put("parts", partsArray);

            JSONArray contentsArray = new JSONArray();
            contentsArray.put(contentObj);

            JSONObject requestBodyObj = new JSONObject();
            requestBodyObj.put("contents", contentsArray);

            // 🌟 CORREGIDO: URL completamente limpia sin residuos de formato Markdown
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);

            HttpEntity<String> entity = new HttpEntity<>(requestBodyObj.toString(), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject jsonObject = new JSONObject(response.getBody());
                String textoCrudoIA = jsonObject.getJSONArray("candidates")
                        .getJSONObject(0).getJSONObject("content")
                        .getJSONArray("parts").getJSONObject(0).getString("text");

                textoCrudoIA = textoCrudoIA.replace("```json", "").replace("```", "").trim();

                JSONObject jsonIA = new JSONObject(textoCrudoIA);
                String respuestaHablada = jsonIA.getString("respuesta");
                String accion = jsonIA.optString("accion", "NINGUNA");

                return ResponseEntity.ok(Map.of(
                        "respuesta", respuestaHablada,
                        "accion", accion
                ));
            }

            return ResponseEntity.status(500).body(Map.of(
                    "respuesta", "Lo siento, ha habido un problema de comunicación con mi servidor central.",
                    "accion", "NINGUNA"));

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // 🌟 ESTO IMPRIMIRÁ EL MENSAJE DE ERROR EXACTO QUE ENVÍA GOOGLE
            System.err.println("=== GOOGLE HA RECHAZADO LA PETICIÓN ===");
            System.err.println("Código HTTP de Google: " + e.getStatusCode());
            System.err.println("Motivo exacto: " + e.getResponseBodyAsString());
            System.err.println("=======================================");

            return ResponseEntity.ok(Map.of(
                    "respuesta", "Google ha denegado el acceso. Revisa la consola de IntelliJ.",
                    "accion", "NINGUNA"
            ));
        } catch (Exception e) {
            System.err.println("=== ERROR INTERNO ===");
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                    "respuesta", "Fallo de conexión.",
                    "accion", "NINGUNA"
            ));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRequest {
        public String mensaje;
    }
}