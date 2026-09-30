package co.edu.uptc.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TextNormalizer")
class TextNormalizerTest {

    // ── normalizeAndTokenize ───────────────────────────────────────

    @Test
    @DisplayName("texto null devuelve arreglo vacío")
    void normalizeAndTokenize_null_returnsEmpty() {
        assertEquals(0, TextNormalizer.normalizeAndTokenize(null).length);
    }

    @Test
    @DisplayName("texto en blanco devuelve arreglo vacío")
    void normalizeAndTokenize_blank_returnsEmpty() {
        assertEquals(0, TextNormalizer.normalizeAndTokenize("   ").length);
    }

    @Test
    @DisplayName("convierte a minúsculas")
    void normalizeAndTokenize_uppercase_lowercased() {
        String[] tokens = TextNormalizer.normalizeAndTokenize("JAVA Python");
        assertEquals("java", tokens[0]);
        assertEquals("python", tokens[1]);
    }

    @Test
    @DisplayName("elimina tildes y caracteres especiales")
    void normalizeAndTokenize_accentedChars_removed() {
        String[] tokens = TextNormalizer.normalizeAndTokenize("árbol café");
        assertEquals("arbol", tokens[0]);
        assertEquals("cafe", tokens[1]);
    }

    @Test
    @DisplayName("elimina signos de puntuación")
    void normalizeAndTokenize_punctuation_removed() {
        String[] tokens = TextNormalizer.normalizeAndTokenize("hola, mundo!");
        assertEquals("hola", tokens[0]);
        assertEquals("mundo", tokens[1]);
    }

    @Test
    @DisplayName("mantiene números")
    void normalizeAndTokenize_numbers_kept() {
        String[] tokens = TextNormalizer.normalizeAndTokenize("java 25");
        assertEquals("java", tokens[0]);
        assertEquals("25", tokens[1]);
    }

    @Test
    @DisplayName("texto con múltiples espacios tokeniza correctamente")
    void normalizeAndTokenize_multipleSpaces_handled() {
        String[] tokens = TextNormalizer.normalizeAndTokenize("hola   mundo");
        assertEquals(2, tokens.length);
    }

    // ── normalizeWord ──────────────────────────────────────────────

    @Test
    @DisplayName("normalizeWord con null devuelve cadena vacía")
    void normalizeWord_null_returnsEmpty() {
        assertEquals("", TextNormalizer.normalizeWord(null));
    }

    @Test
    @DisplayName("normalizeWord elimina tilde y convierte a minúscula")
    void normalizeWord_accentedUppercase_normalized() {
        assertEquals("arbol", TextNormalizer.normalizeWord("Árbol"));
    }

    @Test
    @DisplayName("normalizeWord elimina caracteres no alfanuméricos")
    void normalizeWord_specialChars_removed() {
        assertEquals("hola", TextNormalizer.normalizeWord("¡Hola!"));
    }
}
