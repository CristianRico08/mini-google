package co.edu.uptc.service;

import co.edu.uptc.dto.DocumentDTO;
import co.edu.uptc.exception.SearchEngineException;
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
import co.edu.uptc.util.FileTextExtractor;
import co.edu.uptc.util.StopWordFilter;
import co.edu.uptc.util.TextNormalizer;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Servicio principal del motor de búsqueda.
 */
public class SearchEngineService {

    private final Trie trie;

    private final Map<String, Document> documents;

    private final JsonIndexRepository indexRepository;

    private final XmlMetadataRepository metadataRepository;

    public SearchEngineService(
            String jsonIndexPath,
            String xmlMetadataPath) {

        this.indexRepository =
                new JsonIndexRepository(
                        jsonIndexPath
                );

        this.metadataRepository =
                new XmlMetadataRepository(
                        xmlMetadataPath
                );

        this.trie =
                TrieMapper.toDomain(
                        indexRepository.load()
                );

        this.documents =
                new HashMap<>();

        List<DocumentDTO> dtoList =
                metadataRepository.loadAll();

        for (DocumentDTO dto : dtoList) {

            Document document =
                    DocumentMapper.toDomain(dto);

            if (document != null
                    && document.getId() != null) {

                documents.put(
                        document.getId(),
                        document
                );
            }
        }
    }

    /**
     * Indexa un archivo.
     */
    public void indexFile(
            File file,
            String author,
            String category) {

        if (file == null) {

            throw new SearchEngineException(
                    "El archivo no puede ser nulo."
            );
        }

        if (!file.isFile()) {

            throw new SearchEngineException(
                    "El archivo seleccionado no es válido."
            );
        }

        try {

            String content =
                    FileTextExtractor.extractText(file);

            String[] tokens =
                    TextNormalizer
                            .normalizeAndTokenize(
                                    content
                            );

            String documentId =
                    UUID.randomUUID().toString();

            DocumentMetadata metadata =
                    new DocumentMetadata(

                            author == null
                                    || author.isBlank()
                                    ? "Anónimo"
                                    : author,

                            category == null
                                    || category.isBlank()
                                    ? "General"
                                    : category,

                            LocalDate.now()
                    );

            Document document =
                    new Document(

                            documentId,

                            file.getName(),

                            file.getAbsolutePath(),

                            tokens.length,

                            metadata
                    );

            documents.put(
                    documentId,
                    document
            );

            for (String token : tokens) {

                if (!token.isBlank()) {

                    trie.insert(
                            token,
                            documentId
                    );
                }
            }

            persistState();

        } catch (SearchEngineException e) {

            throw e;

        } catch (Exception e) {

            throw new SearchEngineException(
                    "No fue posible indexar el archivo: "
                            + file.getName(),
                    e
            );
        }
    }

    /**
     * Busca utilizando TF-IDF.
     */
    public List<SearchResult> search(
            String query,
            String categoryFilter,
            LocalDate dateFilter) {

        String[] queryTokens =
                TextNormalizer
                        .normalizeAndTokenize(query);

        if (queryTokens.length == 0
                || documents.isEmpty()) {

            return Collections.emptyList();
        }

        Map<String, Double> scoreMap =
                new HashMap<>();

        int totalDocuments =
                documents.size();

        for (String token : queryTokens) {

            /*
             * No procesar stopwords como
             * términos de búsqueda.
             */
            if (StopWordFilter.isStopword(token)) {
                continue;
            }

            Map<String, Integer> documentFrequencies =
                    trie.getDocumentFrequencies(token);

            if (documentFrequencies.isEmpty()) {
                continue;
            }

            int documentsWithToken =
                    documentFrequencies.size();

            double idf =
                    Math.log(
                            (double) totalDocuments
                                    / documentsWithToken
                    ) + 1.0;

            for (
                    Map.Entry<String, Integer> entry
                    : documentFrequencies.entrySet()) {

                String documentId =
                        entry.getKey();

                Document document =
                        documents.get(documentId);

                if (document == null) {
                    continue;
                }

                if (!applyFilters(
                        document,
                        categoryFilter,
                        dateFilter)) {

                    continue;
                }

                int termFrequency =
                        entry.getValue();

                double tf =
                        (double) termFrequency
                                / Math.max(
                                        1,
                                        document.getTotalWords()
                                );

                double tfIdf =
                        tf * idf;

                scoreMap.merge(
                        documentId,
                        tfIdf,
                        Double::sum
                );
            }
        }

        /*
         * Ordenamiento utilizando AVL.
         */
        AVLTree<SearchResult> tree =
                new AVLTree<>();

        for (
                Map.Entry<String, Double> entry
                : scoreMap.entrySet()) {

            Document document =
                    documents.get(
                            entry.getKey()
                    );

            if (document != null) {

                tree.insert(
                        new SearchResult(
                                document,
                                entry.getValue()
                        )
                );
            }
        }

        return tree.inOrderTraversal();
    }

    private boolean applyFilters(
            Document document,
            String categoryFilter,
            LocalDate dateFilter) {

        DocumentMetadata metadata =
                document.getMetadata();

        if (categoryFilter != null
                && !categoryFilter.isBlank()
                && !categoryFilter.equalsIgnoreCase(
                        "Todas"
        )) {

            if (metadata == null
                    || metadata.category() == null
                    || !categoryFilter.equalsIgnoreCase(
                            metadata.category()
                    )) {

                return false;
            }
        }

        if (dateFilter != null
                && metadata != null
                && metadata.creationDate() != null
                && metadata.creationDate()
                        .isBefore(dateFilter)) {

            return false;
        }

        return true;
    }

    /**
     * Autocompletado.
     */
    public List<String> getAutocompleteSuggestions(
            String prefix) {

        String normalized =
                TextNormalizer.normalizeWord(
                        prefix
                );

        if (normalized.isBlank()) {
            return Collections.emptyList();
        }

        return trie
                .autocomplete(normalized)
                .stream()
                .filter(
                        word ->
                                !StopWordFilter
                                        .isStopword(word)
                )
                .limit(8)
                .toList();
    }

    /**
     * Obtiene las palabras más frecuentes del corpus,
     * ignorando stopwords y números.
     */
    public List<WordStat> getTopWordFrequencies(
            int topN) {

        if (topN <= 0) {
            return Collections.emptyList();
        }

        Map<String, Integer> counts =
                new HashMap<>();

        collectFrequencies(
                trie.getRoot(),
                "",
                counts
        );

        List<WordStat> stats =
                counts.entrySet()
                        .stream()
                        .map(
                                entry ->
                                        new WordStat(
                                                entry.getKey(),
                                                entry.getValue()
                                        )
                        )
                        .sorted()
                        .toList();

        return stats
                .stream()
                .limit(topN)
                .toList();
    }

    private void collectFrequencies(
            TrieNode node,
            String currentWord,
            Map<String, Integer> counts) {

        if (node.isEndOfWord()
                && !StopWordFilter.isStopword(
                        currentWord
                )) {

            int total =
                    node.getDocumentFrequencies()
                            .values()
                            .stream()
                            .mapToInt(Integer::intValue)
                            .sum();

            if (total > 0) {

                counts.put(
                        currentWord,
                        total
                );
            }
        }

        for (
                Map.Entry<Character, TrieNode> entry
                : node.getChildren().entrySet()) {

            collectFrequencies(
                    entry.getValue(),
                    currentWord + entry.getKey(),
                    counts
            );
        }
    }

    /**
     * Obtiene categorías existentes.
     */
    public List<String> getCategories() {

        return documents.values()
                .stream()
                .map(Document::getMetadata)
                .filter(Objects::nonNull)
                .map(DocumentMetadata::category)
                .filter(Objects::nonNull)
                .filter(category ->
                        !category.isBlank())
                .distinct()
                .sorted(
                        String.CASE_INSENSITIVE_ORDER
                )
                .toList();
    }

    /**
     * Devuelve documentos indexados.
     */
    public List<Document> getIndexedDocuments() {

        return documents.values()
                .stream()
                .sorted(
                        Comparator.comparing(
                                Document::getTitle,
                                String.CASE_INSENSITIVE_ORDER
                        )
                )
                .toList();
    }

    /**
     * Desindexa un documento.
     */
    public boolean removeDocument(
            String documentId) {

        if (documentId == null
                || documentId.isBlank()) {

            return false;
        }

        Document removed =
                documents.remove(
                        documentId
                );

        if (removed == null) {
            return false;
        }

        trie.removeDocument(
                documentId
        );

        persistState();

        return true;
    }

    public int getDocumentCount() {
        return documents.size();
    }

    public int getTotalIndexedWords() {

        return documents.values()
                .stream()
                .mapToInt(
                        Document::getTotalWords
                )
                .sum();
    }

    private void persistState() {

        indexRepository.save(
                TrieMapper.toDTO(trie)
        );

        List<DocumentDTO> documentsDTO =
                documents.values()
                        .stream()
                        .map(
                                DocumentMapper::toDTO
                        )
                        .toList();

        metadataRepository.saveAll(
                new ArrayList<>(documentsDTO)
        );
    }
}