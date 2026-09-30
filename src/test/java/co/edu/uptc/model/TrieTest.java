package co.edu.uptc.model;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        trie = new Trie();
    }

    @Test
    void testInsertAndGetFrequencies() {
        trie.insert("java", "doc1");
        trie.insert("java", "doc1");
        trie.insert("java", "doc2");

        Map<String, Integer> freqs = trie.getDocumentFrequencies("java");

        assertEquals(2, freqs.get("doc1"));
        assertEquals(1, freqs.get("doc2"));
    }

    @Test
    void testAutocomplete() {
        trie.insert("algoritmo", "doc1");
        trie.insert("alberto", "doc1");
        trie.insert("arbol", "doc1");

        List<String> suggestions = trie.autocomplete("al");

        assertEquals(2, suggestions.size());
        assertTrue(suggestions.contains("algoritmo"));
        assertTrue(suggestions.contains("alberto"));
        assertFalse(suggestions.contains("arbol"));
    }

    @Test
    void testWordNotFound() {
        Map<String, Integer> freqs = trie.getDocumentFrequencies("inexistente");
        assertTrue(freqs.isEmpty());
    }
}