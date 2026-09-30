package co.edu.uptc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Trie {

    private final TrieNode root = new TrieNode();

    public void insert(String word, String docId) {

        TrieNode current = root;

        for (char ch : word.toLowerCase().toCharArray()) {

            current = current
                    .getChildren()
                    .computeIfAbsent(
                            ch,
                            c -> new TrieNode()
                    );
        }

        current.setEndOfWord(true);

        current
                .getDocumentFrequencies()
                .merge(
                        docId,
                        1,
                        Integer::sum
                );
    }

    public Map<String, Integer> getDocumentFrequencies(
            String word) {

        TrieNode node =
                searchNode(word.toLowerCase());

        return (
                node != null &&
                node.isEndOfWord()
        )
                ? node.getDocumentFrequencies()
                : Collections.emptyMap();
    }

    public List<String> autocomplete(String prefix) {

        List<String> results =
                new ArrayList<>();

        String normalizedPrefix =
                prefix.toLowerCase();

        TrieNode prefixNode =
                searchNode(normalizedPrefix);

        if (prefixNode != null) {

            collectWords(
                    prefixNode,
                    normalizedPrefix,
                    results
            );
        }

        Collections.sort(results);

        return results;
    }

    /**
     * Elimina todas las referencias de un documento.
     */
    public void removeDocument(String docId) {

        if (docId == null || docId.isBlank()) {
            return;
        }

        removeDocumentRecursive(
                root,
                docId
        );
    }

    private boolean removeDocumentRecursive(
            TrieNode node,
            String docId) {

        var iterator =
                node.getChildren()
                        .entrySet()
                        .iterator();

        while (iterator.hasNext()) {

            Map.Entry<Character, TrieNode> entry =
                    iterator.next();

            TrieNode child =
                    entry.getValue();

            boolean deleteChild =
                    removeDocumentRecursive(
                            child,
                            docId
                    );

            if (deleteChild) {
                iterator.remove();
            }
        }

        node.getDocumentFrequencies()
                .remove(docId);

        /*
         * Si ya no existen documentos asociados
         * a esta palabra, deja de ser final de palabra.
         */
        if (node.isEndOfWord()
                && node.getDocumentFrequencies().isEmpty()) {

            node.setEndOfWord(false);
        }

        return !node.isEndOfWord()
                && node.getChildren().isEmpty()
                && node.getDocumentFrequencies().isEmpty();
    }

    private TrieNode searchNode(String text) {

        TrieNode current = root;

        for (char ch : text.toCharArray()) {

            current =
                    current.getChildren().get(ch);

            if (current == null) {
                return null;
            }
        }

        return current;
    }

    private void collectWords(
            TrieNode node,
            String prefix,
            List<String> results) {

        if (node.isEndOfWord()) {
            results.add(prefix);
        }

        for (var entry :
                node.getChildren().entrySet()) {

            collectWords(
                    entry.getValue(),
                    prefix + entry.getKey(),
                    results
            );
        }
    }

    public TrieNode getRoot() {
        return root;
    }
}