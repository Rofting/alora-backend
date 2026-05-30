package com.alora.carelog.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
                    Eres Alora, una asistente médica virtual empática y profesional, especializada en el cuidado geriátrico.
                    Analiza el mensaje y determina la acción técnica que debe ejecutar el sistema.

                    MENSAJE: "%s"

                    INSTRUCCIONES:
                    1. Responde de forma cálida y comprensiva. MÁXIMO 2 líneas de texto.
                    2. CLASIFICACIÓN DE LA INTENCIÓN:
                       - Si la persona indica que YA realizó la tarea (ej. "ya me la tomé", "hecho", "listo"), accion = "COMPLETADO".
                       - Si la persona no puede hacerlo ahora (ej. "ahora no", "no estoy en casa", "estoy acostada", "en un rato"), accion = "POSPONER".
                       - Si la persona pide CREAR un recordatorio nuevo, accion = "CREAR_RECORDATORIO:Título exacto;HH:MM:00;DIAS".
                         DIAS puede ser: TODOS, o días separados por coma: LUNES,MARTES,MIERCOLES,JUEVES,VIERNES,SABADO,DOMINGO.
                         Ejemplo: CREAR_RECORDATORIO:Pastilla tensión;10:00:00;LUNES,MIERCOLES,VIERNES
                       - En cualquier otro caso, accion = "NINGUNA".
                    3. FORMATO (CRÍTICO): Responde ÚNICAMENTE con un JSON válido, sin bloques Markdown, sin texto extra.

                    ESTRUCTURA EXACTA:
                    {
                      "respuesta": "tu respuesta empática aquí",
                      "accion": "COMPLETADO" o "POSPONER" o "NINGUNA" o "CREAR_RECORDATORIO:..."
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
            log.error("Gemini rechazó la petición — HTTP {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(502).body(Map.of(
                    "respuesta", "No se pudo conectar con el asistente. Inténtalo de nuevo.",
                    "accion", "NINGUNA"
            ));
        } catch (Exception e) {
            log.error("Error interno en AssistantController", e);
            return ResponseEntity.status(500).body(Map.of(
                    "respuesta", "Error interno del servidor.",
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