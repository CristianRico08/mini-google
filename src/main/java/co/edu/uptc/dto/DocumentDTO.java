package co.edu.uptc.dto;

public class DocumentDTO {
    private String id;
    private String title;
    private String path;
    private int totalWords;
    private DocumentMetadataDTO metadata;

    public DocumentDTO() {}

    public DocumentDTO(String id, String title, String path, int totalWords, DocumentMetadataDTO metadata) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.totalWords = totalWords;
        this.metadata = metadata;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public int getTotalWords() { return totalWords; }
    public void setTotalWords(int totalWords) { this.totalWords = totalWords; }

    public DocumentMetadataDTO getMetadata() { return metadata; }
    public void setMetadata(DocumentMetadataDTO metadata) { this.metadata = metadata; }
}