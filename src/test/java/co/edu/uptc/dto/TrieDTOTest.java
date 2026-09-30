package co.edu.uptc.dto;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TrieDTO y TrieNodeDTO")
class TrieDTOTest {

    // ── TrieDTO ────────────────────────────────────────────────────

    @Test
    @DisplayName("constructor vacío no lanza excepción")
    void trieDTO_defaultConstructor_noException() {
        assertNotNull(new TrieDTO());
    }

    @Test
    @DisplayName("constructor vacío tiene root null")
    void trieDTO_defaultConstructor_rootIsNull() {
        assertNull(new TrieDTO().getRoot());
    }

    @Test
    @DisplayName("constructor con root asigna correctamente")
    void trieDTO_constructorWithRoot_rootSet() {
        TrieNodeDTO node = new TrieNodeDTO();
        TrieDTO dto = new TrieDTO(node);
        assertNotNull(dto.getRoot());
    }

    @Test
    @DisplayName("setter de root funciona correctamente")
    void trieDTO_setRoot_updatesRoot() {
        TrieDTO dto = new TrieDTO();
        TrieNodeDTO node = new TrieNodeDTO();
        dto.setRoot(node);
        assertSame(node, dto.getRoot());
    }

    // ── TrieNodeDTO ────────────────────────────────────────────────

    @Test
    @DisplayName("constructor vacío inicializa colecciones no nulas")
    void trieNodeDTO_defaultConstructor_collectionsNotNull() {
        TrieNodeDTO node = new TrieNodeDTO();
        assertNotNull(node.getChildren());
        assertNotNull(node.getDocumentFrequencies());
    }

    @Test
    @DisplayName("endOfWord por defecto es false")
    void trieNodeDTO_defaultEndOfWord_isFalse() {
        assertFalse(new TrieNodeDTO().isEndOfWord());
    }

    @Test
    @DisplayName("setEndOfWord cambia el valor correctamente")
    void trieNodeDTO_setEndOfWord_updatesValue() {
        TrieNodeDTO node = new TrieNodeDTO();
        node.setEndOfWord(true);
        assertTrue(node.isEndOfWord());
    }

    @Test
    @DisplayName("se pueden agregar hijos al mapa")
    void trieNodeDTO_addChild_childPresent() {
        TrieNodeDTO parent = new TrieNodeDTO();
        TrieNodeDTO child = new TrieNodeDTO();
        parent.getChildren().put("a", child);
        assertTrue(parent.getChildren().containsKey("a"));
        assertSame(child, parent.getChildren().get("a"));
    }

    @Test
    @DisplayName("se pueden agregar frecuencias de documentos")
    void trieNodeDTO_addDocumentFrequency_frequencyPresent() {
        TrieNodeDTO node = new TrieNodeDTO();
        node.getDocumentFrequencies().put("doc1", 3);
        assertEquals(3, node.getDocumentFrequencies().get("doc1"));
    }

    @Test
    @DisplayName("setChildren reemplaza el mapa de hijos")
    void trieNodeDTO_setChildren_replacesMap() {
        TrieNodeDTO node = new TrieNodeDTO();
        Map<String, TrieNodeDTO> newChildren = Map.of("b", new TrieNodeDTO());
        node.setChildren(new java.util.HashMap<>(newChildren));
        assertTrue(node.getChildren().containsKey("b"));
    }

    @Test
    @DisplayName("setDocumentFrequencies reemplaza el mapa de frecuencias")
    void trieNodeDTO_setDocumentFrequencies_replacesMap() {
        TrieNodeDTO node = new TrieNodeDTO();
        node.setDocumentFrequencies(new java.util.HashMap<>(Map.of("doc2", 5)));
        assertEquals(5, node.getDocumentFrequencies().get("doc2"));
    }
}
