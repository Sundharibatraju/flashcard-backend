package com.bts.flashcards.service;

import com.bts.flashcards.model.Deck;
import com.bts.flashcards.model.Flashcard;
import com.bts.flashcards.repository.DeckRepository;
import com.bts.flashcards.repository.FlashcardRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CSVImportService {

    private final FlashcardRepository flashcardRepository;
    private final DeckRepository deckRepository;

    public Map<String, Object> importFromCSV(
            MultipartFile file, Long deckId) throws Exception {

        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null
                || !filename.endsWith(".csv")) {
            throw new RuntimeException(
                    "Only CSV files are allowed");
        }

        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() ->
                        new RuntimeException("Deck not found"));

        List<Flashcard> cards = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int rowNumber = 1;

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(
                        file.getInputStream()))) {

            // Skip header row
            String[] header = reader.readNext();
            if (header == null) {
                throw new RuntimeException(
                        "CSV file is empty");
            }

            String[] line;
            while ((line = reader.readNext()) != null) {
                rowNumber++;
                try {
                    // Validate row
                    if (line.length < 2) {
                        errors.add("Row " + rowNumber
                                + ": Missing front or back text");
                        continue;
                    }

                    String frontText = line[0].trim();
                    String backText = line[1].trim();

                    if (frontText.isEmpty()
                            || backText.isEmpty()) {
                        errors.add("Row " + rowNumber
                                + ": Front or back text is empty");
                        continue;
                    }

                    Flashcard card = Flashcard.builder()
                            .deck(deck)
                            .frontText(frontText)
                            .backText(backText)
                            .exampleSentence(line.length > 2
                                    ? line[2].trim() : null)
                            .build();

                    cards.add(card);

                } catch (Exception e) {
                    errors.add("Row " + rowNumber
                            + ": " + e.getMessage());
                }
            }
        }

        // Save valid cards
        if (!cards.isEmpty()) {
            flashcardRepository.saveAll(cards);
            deck.setCardCount(
                    deck.getCardCount() + cards.size());
            deckRepository.save(deck);
        }

        // Return result
        Map<String, Object> result = new HashMap<>();
        result.put("imported", cards.size());
        result.put("failed", errors.size());
        result.put("errors", errors);
        result.put("message", "Successfully imported "
                + cards.size() + " cards");
        return result;
    }

    // Generate sample CSV content
    public String generateSampleCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append("front,back,example\n");
        sb.append("Hello,Hola,Hello how are you?\n");
        sb.append("Goodbye,Adios,Goodbye see you tomorrow\n");
        sb.append("Thank you,Gracias,Thank you very much\n");
        sb.append("Please,Por favor,Please help me\n");
        sb.append("Yes,Si,Yes I understand\n");
        return sb.toString();
    }
}