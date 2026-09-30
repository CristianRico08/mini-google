package co.edu.uptc.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class TextNormalizerTest {

    @Test
    void testNormalizeAndTokenize() {
        String text = "¡Hola, Mundo! Programación en Java 100%.";
        String[] tokens = TextNormalizer.normalizeAndTokenize(text);

        assertArrayEquals(new String[]{"hola", "mundo", "programacion", "en", "java", "100"}, tokens);
    }

    @Test
    void testNormalizeWord() {
        String word = "Canción!!!";
        String normalized = TextNormalizer.normalizeWord(word);

        assertEquals("cancion", normalized);
    }

    @Test
    void testEmptyText() {
        String[] tokens = TextNormalizer.normalizeAndTokenize("   ");
        assertEquals(0, tokens.length);
    }
}