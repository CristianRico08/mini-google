package co.edu.uptc.mapper;

import co.edu.uptc.dto.DocumentDTO;
import co.edu.uptc.dto.DocumentMetadataDTO;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.DocumentMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DocumentMapper")
class DocumentMapperTest {

    // ── toDTO ──────────────────────────────────────────────────────

    @Test
    @DisplayName("toDTO con null devuelve null")
    void toDTO_null_returnsNull() {
        assertNull(DocumentMapper.toDTO(null));
    }

    @Test
    @DisplayName("toDTO mapea todos los campos correctamente")
    void toDTO_fullDocument_allFieldsMapped() {
        DocumentMetadata meta = new DocumentMetadata("Chemi", "Tecnología", LocalDate.of(2024, 1, 15));
        Document doc = new Document("id1", "Título", "/ruta/archivo.txt", 200, meta);

        DocumentDTO dto = DocumentMapper.toDTO(doc);

        assertEquals("id1", dto.getId());
        assertEquals("Título", dto.getTitle());
        assertEquals("/ruta/archivo.txt", dto.getPath());
        assertEquals(200, dto.getTotalWords());
        assertNotNull(dto.getMetadata());
        assertEquals("Chemi", dto.getMetadata().getAuthor());
        assertEquals("Tecnología", dto.getMetadata().getCategory());
        assertEquals("2024-01-15", dto.getMetadata().getCreationDate());
    }

    @Test
    @DisplayName("toDTO con metadata null no lanza excepción")
    void toDTO_nullMetadata_dtoMetadataIsNull() {
        Document doc = new Document("id1", "Título", "/ruta", 50, null);
        DocumentDTO dto = DocumentMapper.toDTO(doc);
        assertNull(dto.getMetadata());
    }

    // ── toDomain ──────────────────────────────────────────────────

    @Test
    @DisplayName("toDomain con null devuelve null")
    void toDomain_null_returnsNull() {
        assertNull(DocumentMapper.toDomain(null));
    }

    @Test
    @DisplayName("toDomain mapea todos los campos correctamente")
    void toDomain_fullDTO_allFieldsMapped() {
        DocumentMetadataDTO metaDTO = new DocumentMetadataDTO("Autor", "Ciencia", "2024-06-01");
        DocumentDTO dto = new DocumentDTO("id2", "Doc", "/doc.txt", 100, metaDTO);

        Document doc = DocumentMapper.toDomain(dto);

        assertEquals("id2", doc.getId());
        assertEquals("Doc", doc.getTitle());
        assertEquals("/doc.txt", doc.getPath());
        assertEquals(100, doc.getTotalWords());
        assertNotNull(doc.getMetadata());
        assertEquals("Autor", doc.getMetadata().author());
        assertEquals("Ciencia", doc.getMetadata().category());
        assertEquals(LocalDate.of(2024, 6, 1), doc.getMetadata().creationDate());
    }

    @Test
    @DisplayName("toDomain con metadata null no lanza excepción")
    void toDomain_nullMetadata_domainMetadataIsNull() {
        DocumentDTO dto = new DocumentDTO("id3", "Doc", "/doc.txt", 10, null);
        Document doc = DocumentMapper.toDomain(dto);
        assertNull(doc.getMetadata());
    }

    @Test
    @DisplayName("toDomain con creationDate null usa LocalDate.now()")
    void toDomain_nullCreationDate_usesToday() {
        DocumentMetadataDTO metaDTO = new DocumentMetadataDTO("A", "B", null);
        DocumentDTO dto = new DocumentDTO("id4", "Doc", "/doc.txt", 10, metaDTO);
        Document doc = DocumentMapper.toDomain(dto);
        assertEquals(LocalDate.now(), doc.getMetadata().creationDate());
    }

    @Test
    @DisplayName("toDTO y toDomain son inversos (round-trip)")
    void roundTrip_documentPreservesData() {
        DocumentMetadata meta = new DocumentMetadata("Auth", "Cat", LocalDate.of(2023, 3, 10));
        Document original = new Document("rid", "Round Trip", "/rt.txt", 500, meta);

        Document recovered = DocumentMapper.toDomain(DocumentMapper.toDTO(original));

        assertEquals(original.getId(), recovered.getId());
        assertEquals(original.getTitle(), recovered.getTitle());
        assertEquals(original.getTotalWords(), recovered.getTotalWords());
        assertEquals(original.getMetadata().author(), recovered.getMetadata().author());
        assertEquals(original.getMetadata().creationDate(), recovered.getMetadata().creationDate());
    }
}
