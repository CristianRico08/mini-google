package co.edu.uptc.controller;

import co.edu.uptc.exception.SearchEngineException;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.SearchResult;
import co.edu.uptc.model.WordStat;
import co.edu.uptc.service.SearchEngineService;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controlador de aplicación.
 *
 * No contiene componentes JavaFX.
 * Coordina las operaciones entre la vista y el servicio.
 */
public class MainController {

    private final SearchEngineService searchService;

    public MainController(String jsonIndexPath, String xmlMetadataPath) {
        this(new SearchEngineService(jsonIndexPath, xmlMetadataPath));
    }

    public MainController(SearchEngineService searchService) {
        if (searchService == null) {
            throw new IllegalArgumentException(
                    "El servicio de búsqueda no puede ser nulo."
            );
        }

        this.searchService = searchService;
    }

    /**
     * Ejecuta una búsqueda.
     */
    public List<SearchResult> search(
            String query,
            String category,
            LocalDate minDate) {

        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        return searchService.search(query, category, minDate);
    }

    /**
     * Obtiene sugerencias para autocompletar.
     */
    public List<String> getAutocompleteSuggestions(String prefix) {

        if (prefix == null || prefix.isBlank()) {
            return Collections.emptyList();
        }

        return searchService.getAutocompleteSuggestions(prefix);
    }

    /**
     * Indexa varios archivos.
     */
    public IndexingReport indexFiles(
            List<File> files,
            String author,
            String category) {

        if (files == null || files.isEmpty()) {
            return new IndexingReport(0, 0, List.of());
        }

        int indexed = 0;
        List<String> errors = new ArrayList<>();

        for (File file : files) {
            try {

                searchService.indexFile(
                        file,
                        author,
                        category
                );

                indexed++;

            } catch (SearchEngineException | IllegalArgumentException e) {

                String name =
                        file == null
                                ? "Archivo"
                                : file.getName();

                errors.add(
                        name + ": " + e.getMessage()
                );
            }
        }

        return new IndexingReport(
                indexed,
                files.size(),
                errors
        );
    }

    /**
     * Obtiene los documentos actualmente indexados.
     */
    public List<Document> getIndexedDocuments() {
        return searchService.getIndexedDocuments();
    }

    /**
     * Elimina un documento del índice.
     */
    public boolean removeDocument(String documentId) {
        return searchService.removeDocument(documentId);
    }

    /**
     * Obtiene las categorías existentes.
     */
    public List<String> getCategories() {
        return searchService.getCategories();
    }

    /**
     * Obtiene las palabras más frecuentes.
     */
    public List<WordStat> getTopWordFrequencies(int topN) {
        return searchService.getTopWordFrequencies(topN);
    }

    /**
     * Cantidad de documentos indexados.
     */
    public int getDocumentCount() {
        return searchService.getDocumentCount();
    }

    /**
     * Cantidad total de palabras indexadas.
     */
    public int getTotalIndexedWords() {
        return searchService.getTotalIndexedWords();
    }
}