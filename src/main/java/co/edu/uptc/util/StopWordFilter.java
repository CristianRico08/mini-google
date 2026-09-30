package co.edu.uptc.util;

import java.util.Set;

/**
 * Filtro de palabras vacías para las estadísticas del corpus.
 */
public final class StopWordFilter {

    private static final Set<String> STOPWORDS = Set.of(

            // Español
            "a",
            "al",
            "algo",
            "ante",
            "bajo",
            "con",
            "como",
            "contra",
            "de",
            "del",
            "desde",
            "e",
            "el",
            "en",
            "entre",
            "era",
            "es",
            "esta",
            "este",
            "y",
            "o",
            "para",
            "por",
            "que",
            "se",
            "sin",
            "sobre",
            "su",
            "sus",
            "un",
            "una",
            "uno",
            "unos",
            "unas",
            "la",
            "las",
            "los",

            // Inglés
            "the",
            "and",
            "of",
            "to",
            "in",
            "on",
            "for"
    );

    private StopWordFilter() {
    }

    public static boolean isStopword(String word) {

        if (word == null || word.isBlank()) {
            return true;
        }

        // Ignorar únicamente números
        if (word.matches("\\d+")) {
            return true;
        }

        return STOPWORDS.contains(
                word.toLowerCase()
        );
    }
}