package co.edu.uptc.mapper;

import co.edu.uptc.dto.TrieDTO;
import co.edu.uptc.model.Trie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TrieMapper")
class TrieMapperTest {

    @Test
    @DisplayName("toDTO con null devuelve null")
    void toDTO_null_returnsNull() {
        assertNull(TrieMapper.toDTO(null));
    }

    @Test
    @DisplayName("toDTO de trie vacío produce DTO con root no null")
    void toDTO_emptyTrie_rootNotNull() {
        Trie trie = new Trie();
        TrieDTO dto = TrieMapper.toDTO(trie);
        assertNotNull(dto);
        assertNotNull(dto.getRoot());
    }

    @Test
    @DisplayName("toDTO conserva palabras y frecuencias")
    void toDTO_trieWithWords_frequenciesPreserved() {
        Trie trie = new Trie();
        trie.insert("java", "doc1");
        trie.insert("java", "doc1");
        trie.insert("python", "doc2");

        TrieDTO dto = TrieMapper.toDTO(trie);
        Trie recovered = TrieMapper.toDomain(dto);

        Map<String, Integer> javaFreqs = recovered.getDocumentFrequencies("java");
        assertEquals(2, javaFreqs.get("doc1"));

        Map<String, Integer> pythonFreqs = recovered.getDocumentFrequencies("python");
        assertEquals(1, pythonFreqs.get("doc2"));
    }

    @Test
    @DisplayName("toDomain con DTO vacío devuelve trie funcional")
    void toDomain_emptyDTO_returnsFunctionalTrie() {
        TrieDTO dto = new TrieDTO();
        Trie trie = TrieMapper.toDomain(dto);
        assertNotNull(trie);
        assertTrue(trie.getDocumentFrequencies("cualquier").isEmpty());
    }

    @Test
    @DisplayName("round-trip conserva autocompletado")
    void roundTrip_autocompleteWorks() {
        Trie original = new Trie();
        original.insert("banana", "doc1");
        original.insert("ball", "doc1");
        original.insert("bat", "doc1");

        Trie recovered = TrieMapper.toDomain(TrieMapper.toDTO(original));

        assertTrue(recovered.autocomplete("ba").contains("banana"));
        assertTrue(recovered.autocomplete("ba").contains("ball"));
        assertTrue(recovered.autocomplete("ba").contains("bat"));
    }

    @Test
    @DisplayName("round-trip con múltiples documentos por palabra")
    void roundTrip_multipleDocsPerWord_preserved() {
        Trie original = new Trie();
        original.insert("java", "doc1");
        original.insert("java", "doc2");
        original.insert("java", "doc2"); // freq 2 en doc2

        Trie recovered = TrieMapper.toDomain(TrieMapper.toDTO(original));

        Map<String, Integer> freqs = recovered.getDocumentFrequencies("java");
        assertEquals(1, freqs.get("doc1"));
        assertEquals(2, freqs.get("doc2"));
    }
}
