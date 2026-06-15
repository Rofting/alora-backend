package com.alora.config;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class GeminiService {

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    @Value("${app.gemini-api-key}")
    private String apiKey;

    /**
     * Llama a Gemini y devuelve el texto generado.
     * Lanza {@link GeminiUnavailableException} si la cuota está agotada o hay error de red,
     * para que el caller pueda usar un fallback sin romper el flujo.
     */
    public String generateText(String prompt) {
        JSONObject requestBody = new JSONObject()
                .put("contents", new JSONArray()
                        .put(new JSONObject()
                                .put("parts", new JSONArray()
                                        .put(new JSONObject().put("text", prompt)))))
                .put("generationConfig", new JSONObject()
                        // Desactiva el razonamiento interno de gemini-2.5-flash: respuesta más rápida
                        // y evita que el presupuesto de tokens se agote antes de generar texto
                        .put("thinkingConfig", new JSONObject().put("thinkingBudget", 0))
                        .put("maxOutputTokens", 512));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    GEMINI_URL, new HttpEntity<>(requestBody.toString(), headers), String.class);

            String raw = new JSONObject(response.getBody())
                    .getJSONArray("candidates")
                    .getJSONObject(0).getJSONObject("content")
                    .getJSONArray("parts").getJSONObject(0).getString("text");

            return raw.replace("```json", "").replace("```", "").trim();

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                log.warn("Gemini: cuota agotada (429). Usando fallback.");
            } else {
                log.error("Gemini: error HTTP {} — {}", e.getStatusCode(), e.getResponseBodyAsString());
            }
            throw new GeminiUnavailableException(e);
        } catch (Exception e) {
            log.error("Gemini: error inesperado", e);
            throw new GeminiUnavailableException(e);
        }
    }

    public static class GeminiUnavailableException extends RuntimeException {
        public GeminiUnavailableException(Throwable cause) { super(cause); }
    }
}
