package co.edu.uptc.dto;

public class TrieDTO {
    private TrieNodeDTO root;

    public TrieDTO() {}

    public TrieDTO(TrieNodeDTO root) {
        this.root = root;
    }

    public TrieNodeDTO getRoot() { return root; }
    public void setRoot(TrieNodeDTO root) { this.root = root; }
}
