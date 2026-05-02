package com.bts.flashcards.controller;

import com.bts.flashcards.service.CSVImportService;
import com.bts.flashcards.service.TTSService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImportController {

    private final CSVImportService csvImportService;
    private final TTSService ttsService;

    // POST /api/decks/{deckId}/import
    // Import flashcards from CSV
    @PostMapping("/decks/{deckId}/import")
    public ResponseEntity<Map<String, Object>> importCSV(
            @PathVariable Long deckId,
            @RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result =
                    csvImportService.importFromCSV(file, deckId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity
                    .badRequest().body(error);
        }
    }

    // GET /api/decks/sample-csv
    // Download sample CSV file
    @GetMapping("/decks/sample-csv")
    public ResponseEntity<byte[]> getSampleCSV() {
        String content = csvImportService.generateSampleCSV();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData(
                "attachment", "sample-flashcards.csv");
        return ResponseEntity.ok()
                .headers(headers)
                .body(content.getBytes());
    }

    // GET /api/tts?text=hello&language=spanish
    // Text to Speech
    // GET /api/tts?text=hello&language=spanish
    @GetMapping("/tts")
    public ResponseEntity<byte[]> textToSpeech(
            @RequestParam String text,
            @RequestParam(defaultValue = "english")
            String language) {
        try {
            byte[] audio = ttsService.synthesize(text, language);

            // If empty audio return message instead
            if (audio.length == 0) {
                return ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.parseMediaType("audio/mpeg"));
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(audio);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/tts/languages
    // Get supported languages
    @GetMapping("/tts/languages")
    public ResponseEntity<List<String>> getLanguages() {
        return ResponseEntity.ok(
                ttsService.getSupportedLanguages());
    }
}