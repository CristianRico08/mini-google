package co.edu.uptc.mapper;

import co.edu.uptc.dto.TrieDTO;
import co.edu.uptc.dto.TrieNodeDTO;
import co.edu.uptc.model.Trie;
import co.edu.uptc.model.TrieNode;

import java.util.Map;

public class TrieMapper {

    public static TrieDTO toDTO(Trie trie) {
        if (trie == null) return null;
        return new TrieDTO(toNodeDTO(trie.getRoot()));
    }

    private static TrieNodeDTO toNodeDTO(TrieNode node) {
        if (node == null) return null;
        TrieNodeDTO dto = new TrieNodeDTO();
        dto.setEndOfWord(node.isEndOfWord());
        dto.setDocumentFrequencies(node.getDocumentFrequencies());

        for (Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
            dto.getChildren().put(String.valueOf(entry.getKey()), toNodeDTO(entry.getValue()));
        }
        return dto;
    }

    public static Trie toDomain(TrieDTO dto) {
        Trie trie = new Trie();
        if (dto != null && dto.getRoot() != null) {
            populateNodeDomain(trie.getRoot(), dto.getRoot());
        }
        return trie;
    }

    private static void populateNodeDomain(TrieNode target, TrieNodeDTO source) {
        target.setEndOfWord(source.isEndOfWord());
        target.getDocumentFrequencies().putAll(source.getDocumentFrequencies());

        for (Map.Entry<String, TrieNodeDTO> entry : source.getChildren().entrySet()) {
            char ch = entry.getKey().charAt(0);
            TrieNode childNode = new TrieNode();
            target.getChildren().put(ch, childNode);
            populateNodeDomain(childNode, entry.getValue());
        }
    }
}