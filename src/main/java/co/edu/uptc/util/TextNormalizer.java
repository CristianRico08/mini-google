package co.edu.uptc.util;

import java.text.Normalizer;

public class TextNormalizer {

    public static String[] normalizeAndTokenize(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return new String[0];
        }

        String normalized = Normalizer.normalize(rawText, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");

        return normalized.trim().split("\\s+");
    }

    public static String normalizeWord(String word) {
        if (word == null) return "";
        String normalized = Normalizer.normalize(word, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase().replaceAll("[^a-z0-9]", "");
    }
}