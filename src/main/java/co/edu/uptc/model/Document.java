package co.edu.uptc.model;

public class Document {
    private final String id;
    private final String title;
    private final String path;
    private final int totalWords;
    private final DocumentMetadata metadata;

    public Document(String id, String title, String path, int totalWords, DocumentMetadata metadata) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.totalWords = totalWords;
        this.metadata = metadata;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getPath() { return path; }
    public int getTotalWords() { return totalWords; }
    public DocumentMetadata getMetadata() { return metadata; }
}