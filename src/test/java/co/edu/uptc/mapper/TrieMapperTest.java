package co.edu.uptc.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import co.edu.uptc.dto.TrieDTO;
import co.edu.uptc.model.Trie;

class TrieMapperTest {

    @Test
    void testToDTOAndToDomain() {
        Trie originalTrie = new Trie();
        originalTrie.insert("prueba", "doc1");

        // Dominio -> DTO
        TrieDTO dto = TrieMapper.toDTO(originalTrie);
        assertNotNull(dto);
        assertNotNull(dto.getRoot());

        // DTO -> Dominio
        Trie reconstructedTrie = TrieMapper.toDomain(dto);
        assertEquals(1, reconstructedTrie.getDocumentFrequencies("prueba").get("doc1"));
    }
}