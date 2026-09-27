package co.edu.uptc.model;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    private final Map<Character, TrieNode> children = new HashMap<>();
    private final Map<String, Integer> documentFrequencies = new HashMap<>(); // docId -> cantidad
    private boolean isEndOfWord = false;

    public Map<Character, TrieNode> getChildren() { return children; }
    public Map<String, Integer> getDocumentFrequencies() { return documentFrequencies; }
    public boolean isEndOfWord() { return isEndOfWord; }
    public void setEndOfWord(boolean endOfWord) { isEndOfWord = endOfWord; }
}