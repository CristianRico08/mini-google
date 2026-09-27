package co.edu.uptc.dto;

import java.util.HashMap;
import java.util.Map;

public class TrieNodeDTO {
    private Map<String, TrieNodeDTO> children = new HashMap<>();
    private Map<String, Integer> documentFrequencies = new HashMap<>();
    private boolean endOfWord;

    public TrieNodeDTO() {}

    public Map<String, TrieNodeDTO> getChildren() { return children; }
    public void setChildren(Map<String, TrieNodeDTO> children) { this.children = children; }

    public Map<String, Integer> getDocumentFrequencies() { return documentFrequencies; }
    public void setDocumentFrequencies(Map<String, Integer> documentFrequencies) { this.documentFrequencies = documentFrequencies; }

    public boolean isEndOfWord() { return endOfWord; }
    public void setEndOfWord(boolean endOfWord) { this.endOfWord = endOfWord; }
}