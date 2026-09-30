package co.edu.uptc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Trie")
class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        trie = new Trie();
    }

    // ── insert ─────────────────────────────────────────────────────

    @Test
    @DisplayName("insertar palabra y verificar que existe")
    void insert_word_foundInFrequencies() {
        trie.insert("java", "doc1");
        assertFalse(trie.getDocumentFrequencies("java").isEmpty());
    }

    @Test
    @DisplayName("insertar misma palabra dos veces en el mismo doc suma frecuencia")
    void insert_sameWordTwice_frequencyIsTwo() {
        trie.insert("java", "doc1");
        trie.insert("java", "doc1");
        assertEquals(2, trie.getDocumentFrequencies("java").get("doc1"));
    }

    @Test
    @DisplayName("insertar misma palabra en dos documentos distintos")
    void insert_sameWordTwoDocs_bothRegistered() {
        trie.insert("java", "doc1");
        trie.insert("java", "doc2");
        Map<String, Integer> freqs = trie.getDocumentFrequencies("java");
        assertEquals(1, freqs.get("doc1"));
        assertEquals(1, freqs.get("doc2"));
    }

    @Test
    @DisplayName("insert convierte a minúsculas")
    void insert_uppercaseWord_normalizedToLowercase() {
        trie.insert("JAVA", "doc1");
        assertFalse(trie.getDocumentFrequencies("java").isEmpty());
    }

    // ── getDocumentFrequencies ─────────────────────────────────────

    @Test
    @DisplayName("palabra no insertada devuelve mapa vacío")
    void getDocumentFrequencies_unknownWord_returnsEmpty() {
        assertTrue(trie.getDocumentFrequencies("xyz").isEmpty());
    }

    @Test
    @DisplayName("prefijo existente sin ser fin de palabra devuelve vacío")
    void getDocumentFrequencies_prefix_returnsEmpty() {
        trie.insert("java", "doc1");
        assertTrue(trie.getDocumentFrequencies("jav").isEmpty());
    }

    // ── autocomplete ───────────────────────────────────────────────

    @Test
    @DisplayName("autocompletar con prefijo válido devuelve palabras correctas")
    void autocomplete_validPrefix_returnsMatchingWords() {
        trie.insert("java", "doc1");
        trie.insert("javascript", "doc1");
        trie.insert("python", "doc1");
        List<String> suggestions = trie.autocomplete("jav");
        assertTrue(suggestions.contains("java"));
        assertTrue(suggestions.contains("javascript"));
        assertFalse(suggestions.contains("python"));
    }

    @Test
    @DisplayName("autocompletar con prefijo sin coincidencias devuelve lista vacía")
    void autocomplete_noMatch_returnsEmpty() {
        trie.insert("java", "doc1");
        assertTrue(trie.autocomplete("xyz").isEmpty());
    }

    @Test
    @DisplayName("autocompletar devuelve resultados ordenados alfabéticamente")
    void autocomplete_results_areSorted() {
        trie.insert("banana", "doc1");
        trie.insert("ball", "doc1");
        trie.insert("bat", "doc1");
        List<String> results = trie.autocomplete("ba");
        assertEquals(List.of("ball", "banana", "bat"), results);
    }

    @Test
    @DisplayName("autocompletar con prefijo vacío no lanza excepción")
    void autocomplete_emptyPrefix_returnsAll() {
        trie.insert("java", "doc1");
        trie.insert("python", "doc1");
        // prefijo vacío navega desde la raíz → devuelve todas
        List<String> results = trie.autocomplete("");
        assertTrue(results.contains("java"));
        assertTrue(results.contains("python"));
    }

    // ── removeDocument ─────────────────────────────────────────────

    @Test
    @DisplayName("eliminar documento borra sus frecuencias")
    void removeDocument_existing_frequenciesRemoved() {
        trie.insert("java", "doc1");
        trie.removeDocument("doc1");
        assertTrue(trie.getDocumentFrequencies("java").isEmpty());
    }

    @Test
    @DisplayName("eliminar doc que no existe no lanza excepción")
    void removeDocument_nonExisting_noException() {
        trie.insert("java", "doc1");
        assertDoesNotThrow(() -> trie.removeDocument("docX"));
        assertFalse(trie.getDocumentFrequencies("java").isEmpty());
    }

    @Test
    @DisplayName("eliminar doc null no lanza excepción")
    void removeDocument_null_noException() {
        assertDoesNotThrow(() -> trie.removeDocument(null));
    }

    @Test
    @DisplayName("eliminar un doc de varios mantiene los demás")
    void removeDocument_oneOfTwo_otherRemains() {
        trie.insert("java", "doc1");
        trie.insert("java", "doc2");
        trie.removeDocument("doc1");
        Map<String, Integer> freqs = trie.getDocumentFrequencies("java");
        assertFalse(freqs.containsKey("doc1"));
        assertTrue(freqs.containsKey("doc2"));
    }
}
