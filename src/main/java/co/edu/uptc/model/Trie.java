package co.edu.uptc.model;

import java.util.*;

public class Trie {
    private final TrieNode root = new TrieNode();

    public void insert(String word, String docId) {
        TrieNode current = root;
        for (char ch : word.toLowerCase().toCharArray()) {
            current = current.getChildren().computeIfAbsent(ch, c -> new TrieNode());
        }
        current.setEndOfWord(true);
        current.getDocumentFrequencies().merge(docId, 1, Integer::sum);
    }

    public Map<String, Integer> getDocumentFrequencies(String word) {
        TrieNode node = searchNode(word.toLowerCase());
        return (node != null && node.isEndOfWord()) ? node.getDocumentFrequencies() : Collections.emptyMap();
    }

    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode prefixNode = searchNode(prefix.toLowerCase());
        if (prefixNode != null) {
            collectWords(prefixNode, prefix.toLowerCase(), results);
        }
        return results;
    }

    private TrieNode searchNode(String text) {
        TrieNode current = root;
        for (char ch : text.toCharArray()) {
            current = current.getChildren().get(ch);
            if (current == null) return null;
        }
        return current;
    }

    private void collectWords(TrieNode node, String prefix, List<String> results) {
        if (node.isEndOfWord()) results.add(prefix);
        for (var entry : node.getChildren().entrySet()) {
            collectWords(entry.getValue(), prefix + entry.getKey(), results);
        }
    }

    public TrieNode getRoot() { return root; }
}