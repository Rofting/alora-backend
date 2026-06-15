package com.alora.carelog.controller;

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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/profiles/{profileId}/chat")
@RequiredArgsConstructor
public class AssistantController {

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

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
            String fechaServidor = ZonedDateTime.now(ZoneId.of("Europe/Madrid"))
                    .format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy, HH:mm",
                            Locale.forLanguageTag("es-ES")));

            String contexto = (request.contexto != null && !request.contexto.isBlank())
                    ? request.contexto
                    : "Fecha y hora actual: " + fechaServidor;

            String historial = (request.historial != null && !request.historial.isBlank())
                    ? request.historial
                    : "(sin conversación previa)";

            String prompt = """
                    Eres Alora, una asistente de voz empática para personas mayores que les ayuda con su medicación
                    y recordatorios. Hablas en español, con frases cortas, claras y cálidas (la respuesta se lee en voz alta).

                    CONTEXTO DEL SISTEMA:
                    %s

                    CONVERSACIÓN PREVIA:
                    %s

                    MENSAJE ACTUAL DEL PACIENTE:
                    "%s"

                    TU TAREA: responde al paciente y clasifica la intención en el campo "accion".

                    REGLAS DE CLASIFICACIÓN (en este orden):
                    1. "COMPLETADO" → el paciente confirma que YA hizo la tarea o tomó la medicación
                       (ej.: "ya me la tomé", "hecho", "listo", "sí, ya").
                    2. "POSPONER:MINUTOS" → no puede hacerlo ahora (ej.: "ahora no", "no estoy en casa", "más tarde", "en un rato").
                       - Si el paciente indica cuánto tiempo ("en diez minutos", "en una hora"), conviértelo a minutos: POSPONER:10, POSPONER:60.
                       - Si no indica tiempo, usa 30 minutos por defecto: POSPONER:30.
                       - En la respuesta hablada, confirma el aplazamiento mencionando los minutos.
                    3. "CREAR_RECORDATORIO:Título;HH:MM:00;DIAS" → pide crear o programar un recordatorio nuevo.
                       - Usa la fecha y hora actual del contexto para resolver expresiones relativas:
                         "mañana" = el día siguiente de la semana, "en una hora" = hora actual + 1.
                       - DIAS: "TODOS" o días separados por coma (LUNES,MARTES,MIERCOLES,JUEVES,VIERNES,SABADO,DOMINGO).
                       - Si dice solo una hora sin días, usa TODOS. Usa formato 24h.
                       - Si falta la HORA, NO crees el recordatorio: pregunta la hora y usa accion "NINGUNA".
                    4. "NINGUNA" → conversación general, preguntas, o falta información para actuar.

                    EJEMPLOS:
                    - "ponme la pastilla de la tensión a las 10 de la mañana" → accion: "CREAR_RECORDATORIO:Pastilla de la tensión;10:00:00;TODOS"
                    - "recuérdame el jarabe lunes y jueves a las 9 y media de la noche" → accion: "CREAR_RECORDATORIO:Jarabe;21:30:00;LUNES,JUEVES"
                    - "quiero un recordatorio para el paracetamol" → accion: "NINGUNA" y respuesta: "Claro, ¿a qué hora quieres que te lo recuerde?"
                    - "ya me tomé la pastilla" → accion: "COMPLETADO"
                    - "ahora no puedo, estoy en el médico" → accion: "POSPONER:30"
                    - "recuérdamelo en diez minutos" → accion: "POSPONER:10"
                    - "no me la voy a tomar todavía, en una hora" → accion: "POSPONER:60"

                    RESPONDE SOLO con JSON válido con esta estructura exacta:
                    {"respuesta": "texto cálido de máximo 2 frases", "accion": "COMPLETADO" | "POSPONER:MINUTOS" | "NINGUNA" | "CREAR_RECORDATORIO:..."}
                    """.formatted(contexto, historial, request.mensaje.trim());

            JSONObject requestBodyObj = new JSONObject()
                    .put("contents", new JSONArray()
                            .put(new JSONObject()
                                    .put("parts", new JSONArray()
                                            .put(new JSONObject().put("text", prompt)))))
                    .put("generationConfig", new JSONObject()
                            .put("responseMimeType", "application/json")
                            .put("temperature", 0.2)
                            // gemini-2.5-flash es un modelo "pensante": sin esto, consume el
                            // presupuesto de tokens en razonamiento y devuelve la respuesta vacía
                            .put("thinkingConfig", new JSONObject().put("thinkingBudget", 0))
                            .put("maxOutputTokens", 1024));

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);

            HttpEntity<String> entity = new HttpEntity<>(requestBodyObj.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_URL, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Parseo defensivo: si Gemini agota tokens o bloquea la respuesta,
                // "content/parts" puede no existir. Logueamos el cuerpo para diagnóstico.
                JSONObject cuerpo = new JSONObject(response.getBody());
                JSONObject candidato = cuerpo.optJSONArray("candidates") != null && cuerpo.getJSONArray("candidates").length() > 0
                        ? cuerpo.getJSONArray("candidates").getJSONObject(0) : null;
                JSONObject contenido = candidato != null ? candidato.optJSONObject("content") : null;
                String textoCrudoIA = null;
                if (contenido != null && contenido.optJSONArray("parts") != null
                        && contenido.getJSONArray("parts").length() > 0) {
                    textoCrudoIA = contenido.getJSONArray("parts").getJSONObject(0).optString("text", null);
                }

                if (textoCrudoIA == null || textoCrudoIA.isBlank()) {
                    log.error("Gemini devolvió una respuesta sin texto (finishReason={}): {}",
                            candidato != null ? candidato.optString("finishReason") : "sin candidato",
                            response.getBody());
                    return ResponseEntity.ok(Map.of(
                            "respuesta", "Perdona, no te he entendido bien. ¿Puedes repetirlo?",
                            "accion", "NINGUNA"));
                }

                textoCrudoIA = textoCrudoIA.replace("```json", "").replace("```", "").trim();

                JSONObject jsonIA = new JSONObject(textoCrudoIA);
                return ResponseEntity.ok(Map.of(
                        "respuesta", jsonIA.optString("respuesta", "Perdona, no te he entendido bien."),
                        "accion", jsonIA.optString("accion", "NINGUNA")
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "respuesta", "Lo siento, ha habido un problema de comunicación con mi servidor central.",
                    "accion", "NINGUNA"));

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("Gemini rechazó la petición — HTTP {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.ok(Map.of(
                    "respuesta", "En este momento no puedo procesar tu mensaje. Inténtalo de nuevo en unos segundos.",
                    "accion", "NINGUNA"
            ));
        } catch (Exception e) {
            log.error("Error interno en AssistantController", e);
            return ResponseEntity.ok(Map.of(
                    "respuesta", "Ha ocurrido un error inesperado. Por favor, inténtalo de nuevo.",
                    "accion", "NINGUNA"
            ));
        }
    }

    @Data
    @NoArgsConstructor
    public static class ChatRequest {
        public String mensaje;
        public String contexto;
        public String historial;

        public ChatRequest(String mensaje) { this.mensaje = mensaje; }
    }
}
