package co.edu.uptc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DocumentDTO y DocumentMetadataDTO")
class DocumentDTOTest {

    // ── DocumentDTO ────────────────────────────────────────────────

    @Test
    @DisplayName("constructor vacío crea objeto sin lanzar excepción")
    void documentDTO_defaultConstructor_noException() {
        assertNotNull(new DocumentDTO());
    }

    @Test
    @DisplayName("constructor completo asigna todos los campos")
    void documentDTO_fullConstructor_allFieldsSet() {
        DocumentMetadataDTO meta = new DocumentMetadataDTO("Chemi", "Tech", "2024-01-01");
        DocumentDTO dto = new DocumentDTO("id1", "Título", "/ruta.txt", 300, meta);

        assertEquals("id1", dto.getId());
        assertEquals("Título", dto.getTitle());
        assertEquals("/ruta.txt", dto.getPath());
        assertEquals(300, dto.getTotalWords());
        assertNotNull(dto.getMetadata());
    }

    @Test
    @DisplayName("setters modifican los campos correctamente")
    void documentDTO_setters_updateFields() {
        DocumentDTO dto = new DocumentDTO();
        dto.setId("id2");
        dto.setTitle("Nuevo título");
        dto.setPath("/nueva/ruta.txt");
        dto.setTotalWords(500);
        dto.setMetadata(new DocumentMetadataDTO("Ana", "Ciencia", "2024-06-01"));

        assertEquals("id2", dto.getId());
        assertEquals("Nuevo título", dto.getTitle());
        assertEquals("/nueva/ruta.txt", dto.getPath());
        assertEquals(500, dto.getTotalWords());
        assertEquals("Ana", dto.getMetadata().getAuthor());
    }

    @Test
    @DisplayName("metadata puede ser null")
    void documentDTO_nullMetadata_allowed() {
        DocumentDTO dto = new DocumentDTO("id3", "Doc", "/doc.txt", 10, null);
        assertNull(dto.getMetadata());
    }

    // ── DocumentMetadataDTO ────────────────────────────────────────

    @Test
    @DisplayName("constructor vacío de metadata no lanza excepción")
    void metadataDTO_defaultConstructor_noException() {
        assertNotNull(new DocumentMetadataDTO());
    }

    @Test
    @DisplayName("constructor completo de metadata asigna todos los campos")
    void metadataDTO_fullConstructor_allFieldsSet() {
        DocumentMetadataDTO meta = new DocumentMetadataDTO("Autor", "Categoría", "2023-12-31");

        assertEquals("Autor", meta.getAuthor());
        assertEquals("Categoría", meta.getCategory());
        assertEquals("2023-12-31", meta.getCreationDate());
    }

    @Test
    @DisplayName("setters de metadata modifican los campos correctamente")
    void metadataDTO_setters_updateFields() {
        DocumentMetadataDTO meta = new DocumentMetadataDTO();
        meta.setAuthor("Luis");
        meta.setCategory("Historia");
        meta.setCreationDate("2020-05-10");

        assertEquals("Luis", meta.getAuthor());
        assertEquals("Historia", meta.getCategory());
        assertEquals("2020-05-10", meta.getCreationDate());
    }

    @Test
    @DisplayName("campos pueden ser null sin lanzar excepción")
    void metadataDTO_nullFields_allowed() {
        DocumentMetadataDTO meta = new DocumentMetadataDTO(null, null, null);
        assertNull(meta.getAuthor());
        assertNull(meta.getCategory());
        assertNull(meta.getCreationDate());
    }
}
