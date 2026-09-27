package co.edu.uptc.mapper;

import co.edu.uptc.dto.DocumentDTO;
import co.edu.uptc.dto.DocumentMetadataDTO;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.DocumentMetadata;

import java.time.LocalDate;

public class DocumentMapper {

    public static DocumentDTO toDTO(Document document) {
        if (document == null) return null;
        DocumentMetadataDTO metaDTO = null;
        if (document.getMetadata() != null) {
            metaDTO = new DocumentMetadataDTO(
                document.getMetadata().author(),
                document.getMetadata().category(),
                document.getMetadata().creationDate() != null ? document.getMetadata().creationDate().toString() : null
            );
        }
        return new DocumentDTO(document.getId(), document.getTitle(), document.getPath(), document.getTotalWords(), metaDTO);
    }

    public static Document toDomain(DocumentDTO dto) {
        if (dto == null) return null;
        DocumentMetadata meta = null;
        if (dto.getMetadata() != null) {
            LocalDate date = dto.getMetadata().getCreationDate() != null ? LocalDate.parse(dto.getMetadata().getCreationDate()) : LocalDate.now();
            meta = new DocumentMetadata(dto.getMetadata().getAuthor(), dto.getMetadata().getCategory(), date);
        }
        return new Document(dto.getId(), dto.getTitle(), dto.getPath(), dto.getTotalWords(), meta);
    }
}