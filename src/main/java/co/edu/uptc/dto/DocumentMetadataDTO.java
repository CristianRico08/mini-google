package co.edu.uptc.dto;

public class DocumentMetadataDTO {
    private String author;
    private String category;
    private String creationDate;

    public DocumentMetadataDTO() {}

    public DocumentMetadataDTO(String author, String category, String creationDate) {
        this.author = author;
        this.category = category;
        this.creationDate = creationDate;
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }
}