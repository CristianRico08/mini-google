package co.edu.uptc.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import co.edu.uptc.model.SearchResult;
import co.edu.uptc.model.WordStat;

class SearchEngineServiceTest {

    private SearchEngineService service;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        Path jsonPath = tempDir.resolve("index.json");
        Path xmlPath = tempDir.resolve("metadata.xml");
        service = new SearchEngineService(jsonPath.toString(), xmlPath.toString());
    }

    @Test
    void testSearchWithDateFilter(@TempDir Path tempDir) throws Exception {
        Path txtPath = tempDir.resolve("doc_fecha.txt");
        Files.writeString(txtPath, "Documento de prueba con fechas.");
        service.indexFile(txtPath.toFile(), "Autor", "General");

        // Búsqueda con fecha mínima posterior a hoy (no debe retornar nada)
        List<SearchResult> futureResults = service.search("prueba", "General", LocalDate.now().plusDays(1));
        assertTrue(futureResults.isEmpty());

        // Búsqueda con fecha mínima anterior o igual a hoy
        List<SearchResult> validResults = service.search("prueba", "General", LocalDate.now());
        assertEquals(1, validResults.size());
    }

    @Test
    void testSearchMultipleTermsRanking(@TempDir Path tempDir) throws Exception {
        // Documento A: contiene la palabra "java" 3 veces
        Path pathA = tempDir.resolve("docA.txt");
        Files.writeString(pathA, "java java java lenguaje");
        service.indexFile(pathA.toFile(), "Autor A", "General");

        // Documento B: contiene la palabra "java" 1 vez
        Path pathB = tempDir.resolve("docB.txt");
        Files.writeString(pathB, "java python programacion");
        service.indexFile(pathB.toFile(), "Autor B", "General");

        List<SearchResult> results = service.search("java", "Todas", null);

        assertEquals(2, results.size());
        // El Documento A debe tener un puntaje TF-IDF mayor y aparecer primero
        assertEquals("docA.txt", results.get(0).getDocument().getTitle());
        assertTrue(results.get(0).getScore() > results.get(1).getScore());
    }

    @Test
    void testSearchEmptyOrNullQuery() {
        List<SearchResult> nullResults = service.search(null, null, null);
        List<SearchResult> emptyResults = service.search("   ", null, null);

        assertTrue(nullResults.isEmpty());
        assertTrue(emptyResults.isEmpty());
    }

    @Test
    void testAutocompleteServiceIntegration(@TempDir Path tempDir) throws Exception {
        Path txtPath = tempDir.resolve("auto.txt");
        Files.writeString(txtPath, "algoritmos alimentacion infraestructura");
        service.indexFile(txtPath.toFile(), "Autor", "General");

        List<String> suggestions = service.getAutocompleteSuggestions("al");

        assertEquals(2, suggestions.size());
        assertTrue(suggestions.contains("algoritmos"));
        assertTrue(suggestions.contains("alimentacion"));
    }

    @Test
    void testTopWordFrequencies(@TempDir Path tempDir) throws Exception {
        Path txtPath = tempDir.resolve("freq.txt");
        Files.writeString(txtPath, "codigo codigo codigo datos datos red");
        service.indexFile(txtPath.toFile(), "Autor", "General");

        List<WordStat> topWords = service.getTopWordFrequencies(2);

        assertEquals(2, topWords.size());
        assertEquals("codigo", topWords.get(0).word());
        assertEquals(3, topWords.get(0).frequency());
        assertEquals("datos", topWords.get(1).word());
        assertEquals(2, topWords.get(1).frequency());
    }
}