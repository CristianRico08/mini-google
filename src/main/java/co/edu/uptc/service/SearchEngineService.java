package co.edu.uptc.service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import co.edu.uptc.dto.DocumentDTO;
import co.edu.uptc.mapper.DocumentMapper;
import co.edu.uptc.mapper.TrieMapper;
import co.edu.uptc.model.AVLTree;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.DocumentMetadata;
import co.edu.uptc.model.SearchResult;
import co.edu.uptc.model.Trie;
import co.edu.uptc.model.TrieNode;
import co.edu.uptc.model.WordStat;
import co.edu.uptc.repository.JsonIndexRepository;
import co.edu.uptc.repository.XmlMetadataRepository;
import co.edu.uptc.util.TextNormalizer;

public class SearchEngineService {

    private final Trie trie;
    private final Map<String, Document> documents;
    private final JsonIndexRepository indexRepository;
    private final XmlMetadataRepository metadataRepository;

    public SearchEngineService(String jsonIndexPath, String xmlMetadataPath) {
        this.indexRepository = new JsonIndexRepository(jsonIndexPath);
        this.metadataRepository = new XmlMetadataRepository(xmlMetadataPath);

        this.trie = TrieMapper.toDomain(indexRepository.load());
        this.documents = new HashMap<>();

        List<DocumentDTO> dtoList = metadataRepository.loadAll();
        for (DocumentDTO dto : dtoList) {
            Document doc = DocumentMapper.toDomain(dto);
            this.documents.put(doc.getId(), doc);
        }
    }

    public void indexFile(File file, String author, String category) throws Exception {
        String content = co.edu.uptc.util.FileTextExtractor.extractText(file);
        String[] tokens = TextNormalizer.normalizeAndTokenize(content);

        String docId = UUID.randomUUID().toString();
        DocumentMetadata metadata = new DocumentMetadata(author, category, LocalDate.now());
        Document doc = new Document(docId, file.getName(), file.getAbsolutePath(), tokens.length, metadata);

        documents.put(docId, doc);

        for (String token : tokens) {
            if (!token.isBlank()) {
                trie.insert(token, docId);
            }
        }

        persistState();
    }

    public List<SearchResult> search(String query, String categoryFilter, LocalDate dateFilter) {
        String[] queryTokens = TextNormalizer.normalizeAndTokenize(query);
        if (queryTokens.length == 0 || documents.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Double> scoreMap = new HashMap<>();
        int totalDocs = documents.size();

        for (String token : queryTokens) {
            Map<String, Integer> docFreqs = trie.getDocumentFrequencies(token);
            if (docFreqs.isEmpty()) continue;

            int docsContainingToken = docFreqs.size();
            double idf = Math.log((double) totalDocs / docsContainingToken) + 1.0;

            for (Map.Entry<String, Integer> entry : docFreqs.entrySet()) {
                String docId = entry.getKey();
                int termFreq = entry.getValue();

                Document doc = documents.get(docId);
                if (doc == null || !applyFilters(doc, categoryFilter, dateFilter)) {
                    continue;
                }

                double tf = (double) termFreq / doc.getTotalWords();
                double tfIdf = tf * idf;

                scoreMap.merge(docId, tfIdf, Double::sum);
            }
        }

        AVLTree<SearchResult> avlTree = new AVLTree<>();
        for (Map.Entry<String, Double> entry : scoreMap.entrySet()) {
            Document doc = documents.get(entry.getKey());
            avlTree.insert(new SearchResult(doc, entry.getValue()));
        }

        return avlTree.inOrderTraversal();
    }

    private boolean applyFilters(Document doc, String categoryFilter, LocalDate dateFilter) {
        if (categoryFilter != null && !categoryFilter.isBlank() && !categoryFilter.equalsIgnoreCase("Todas")) {
            if (doc.getMetadata() == null || !categoryFilter.equalsIgnoreCase(doc.getMetadata().category())) {
                return false;
            }
        }
        if (dateFilter != null && doc.getMetadata() != null && doc.getMetadata().creationDate() != null) {
            if (doc.getMetadata().creationDate().isBefore(dateFilter)) {
                return false;
            }
        }
        return true;
    }

    public List<String> getAutocompleteSuggestions(String prefix) {
        String normalized = TextNormalizer.normalizeWord(prefix);
        if (normalized.isBlank()) return Collections.emptyList();
        return trie.autocomplete(normalized);
    }

    public List<WordStat> getTopWordFrequencies(int topN) {
        Map<String, Integer> wordCounts = new HashMap<>();
        collectFrequencies(trie.getRoot(), "", wordCounts);

        List<WordStat> stats = new ArrayList<>();
        wordCounts.forEach((word, count) -> stats.add(new WordStat(word, count)));
        stats.sort(Collections.reverseOrder());

        return stats.stream().limit(topN).toList();
    }

    private void collectFrequencies(TrieNode node, String currentWord, Map<String, Integer> counts) {
        if (node.isEndOfWord()) {
            int total = node.getDocumentFrequencies().values().stream().mapToInt(Integer::intValue).sum();
            counts.put(currentWord, total);
        }
        for (Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
            collectFrequencies(entry.getValue(), currentWord + entry.getKey(), counts);
        }
    }

    private void persistState() {
        indexRepository.save(TrieMapper.toDTO(trie));
        List<DocumentDTO> dtoList = documents.values().stream()
                .map(DocumentMapper::toDTO)
                .toList();
        metadataRepository.saveAll(dtoList);
    }
}
