package com.bts.flashcards.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TTSService {

    @Value("${google.tts.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Supported languages
    private static final Map<String, String> LANGUAGE_CODES =
            Map.of(
                    "english", "en-US",
                    "spanish", "es-ES",
                    "french",  "fr-FR",
                    "german",  "de-DE",
                    "italian", "it-IT",
                    "japanese","ja-JP",
                    "korean",  "ko-KR",
                    "chinese", "zh-CN",
                    "hindi",   "hi-IN",
                    "arabic",  "ar-XA"
            );

    public byte[] synthesize(String text, String language) {
        try {
            String langCode = LANGUAGE_CODES
                    .getOrDefault(
                            language.toLowerCase(), "en-US");

            String url = "https://texttospeech.googleapis.com"
                    + "/v1/text:synthesize?key=" + apiKey;

            Map<String, Object> body = new HashMap<>();
            body.put("input", Map.of("text", text));
            body.put("voice", Map.of(
                    "languageCode", langCode,
                    "ssmlGender", "NEUTRAL"
            ));
            body.put("audioConfig", Map.of(
                    "audioEncoding", "MP3"
            ));

            Map response = restTemplate
                    .postForObject(url, body, Map.class);

            if (response == null
                    || !response.containsKey("audioContent")) {
                throw new RuntimeException(
                        "TTS API returned empty response");
            }

            String audioContent =
                    (String) response.get("audioContent");
            return Base64.getDecoder().decode(audioContent);

        } catch (Exception e) {
            throw new RuntimeException(
                    "TTS failed: " + e.getMessage());
        }
    }

    public List<String> getSupportedLanguages() {
        return new ArrayList<>(LANGUAGE_CODES.keySet());
    }
}