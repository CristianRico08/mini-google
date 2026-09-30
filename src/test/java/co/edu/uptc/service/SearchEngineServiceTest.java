package co.edu.uptc.service;

import co.edu.uptc.exception.SearchEngineException;
import co.edu.uptc.model.SearchResult;
import co.edu.uptc.model.WordStat;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SearchEngineService")
class SearchEngineServiceTest {

    private Path tempDir;
    private SearchEngineService service;

    // Archivos .txt de prueba creados en disco
    private File fileA;
    private File fileB;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("service-test");

        // Crear archivos de texto de prueba
        fileA = tempDir.resolve("docA.txt").toFile();
        Files.writeString(fileA.toPath(), "java es un lenguaje java programacion java");

        fileB = tempDir.resolve("docB.txt").toFile();
        Files.writeString(fileB.toPath(), "python es un lenguaje de programacion sencillo");

        String jsonPath = tempDir.resolve("index.json").toString();
        String xmlPath  = tempDir.resolve("metadata.xml").toString();

        service = new SearchEngineService(jsonPath, xmlPath);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
             .map(Path::toFile)
             .sorted((a, b) -> -a.compareTo(b))
             .forEach(File::delete);
    }

    // ── indexFile ──────────────────────────────────────────────────

    @Test
    @DisplayName("indexar un archivo incrementa el conteo de documentos")
    void indexFile_validFile_documentCountIncreases() {
        service.indexFile(fileA, "Chemi", "Tech");
        assertEquals(1, service.getDocumentCount());
    }

    @Test
    @DisplayName("indexar dos archivos distintos registra ambos")
    void indexFile_twoFiles_bothIndexed() {
        service.indexFile(fileA, "Chemi", "Tech");
        service.indexFile(fileB, "Ana", "Ciencia");
        assertEquals(2, service.getDocumentCount());
    }

    @Test
    @DisplayName("indexar archivo null lanza SearchEngineException")
    void indexFile_null_throwsException() {
        assertThrows(SearchEngineException.class, () -> service.indexFile(null, "A", "B"));
    }

    @Test
    @DisplayName("indexar directorio (no archivo) lanza SearchEngineException")
    void indexFile_directory_throwsException() {
        assertThrows(SearchEngineException.class,
                () -> service.indexFile(tempDir.toFile(), "A", "B"));
    }

    @Test
    @DisplayName("indexar con author vacío usa 'Anónimo'")
    void indexFile_blankAuthor_usesAnonimo() {
        service.indexFile(fileA, "", "Tech");
        assertEquals("Anónimo",
                service.getIndexedDocuments().get(0).getMetadata().author());
    }

    @Test
    @DisplayName("indexar con category vacía usa 'General'")
    void indexFile_blankCategory_usesGeneral() {
        service.indexFile(fileA, "Chemi", "");
        assertEquals("General",
                service.getIndexedDocuments().get(0).getMetadata().category());
    }

    // ── search ─────────────────────────────────────────────────────

    @Test
    @DisplayName("búsqueda de palabra presente devuelve resultados")
    void search_existingWord_returnsResults() {
        service.indexFile(fileA, "Chemi", "Tech");
        List<SearchResult> results = service.search("java", null, null);
        assertFalse(results.isEmpty());
    }

    @Test
    @DisplayName("búsqueda de palabra ausente devuelve lista vacía")
    void search_absentWord_returnsEmpty() {
        service.indexFile(fileA, "Chemi", "Tech");
        List<SearchResult> results = service.search("haskell", null, null);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("búsqueda sin documentos indexados devuelve lista vacía")
    void search_noDocuments_returnsEmpty() {
        assertTrue(service.search("java", null, null).isEmpty());
    }

    @Test
    @DisplayName("búsqueda con stopword no devuelve resultados")
    void search_stopword_returnsEmpty() {
        service.indexFile(fileA, "Chemi", "Tech");
        // "de" es stopword, se ignora en búsqueda
        assertTrue(service.search("de", null, null).isEmpty());
    }

    @Test
    @DisplayName("documento con mayor TF-IDF aparece primero")
    void search_twoDocuments_higherScoreFirst() {
        service.indexFile(fileA, "Chemi", "Tech");   // java aparece 3 veces
        service.indexFile(fileB, "Ana", "Ciencia");  // java no aparece

        List<SearchResult> results = service.search("java", null, null);
        assertEquals(1, results.size());
        assertEquals("docA.txt", results.get(0).getDocument().getTitle());
    }

    @Test
    @DisplayName("filtro por categoría excluye documentos de otra categoría")
    void search_categoryFilter_excludesOtherCategory() throws IOException {
        File fileC = tempDir.resolve("docC.txt").toFile();
        Files.writeString(fileC.toPath(), "java java java");

        service.indexFile(fileA, "Chemi", "Tech");
        service.indexFile(fileC, "Luis", "Otro");

        List<SearchResult> results = service.search("java", "Otro", null);
        assertEquals(1, results.size());
        assertEquals("Otro", results.get(0).getDocument().getMetadata().category());
    }

    @Test
    @DisplayName("filtro por fecha excluye documentos anteriores")
    void search_dateFilter_excludesOldDocuments() {
        service.indexFile(fileA, "Chemi", "Tech");
        // Filtro de fecha futura: debería excluir documentos de hoy
        List<SearchResult> results = service.search("java", null, LocalDate.now().plusDays(1));
        assertTrue(results.isEmpty());
    }

    // ── autocomplete ───────────────────────────────────────────────

    @Test
    @DisplayName("autocompletar devuelve sugerencias para prefijo válido")
    void autocomplete_validPrefix_returnsSuggestions() {
        service.indexFile(fileA, "Chemi", "Tech");
        List<String> suggestions = service.getAutocompleteSuggestions("jav");
        assertTrue(suggestions.contains("java"));
    }

    @Test
    @DisplayName("autocompletar con prefijo en blanco devuelve lista vacía")
    void autocomplete_blankPrefix_returnsEmpty() {
        service.indexFile(fileA, "Chemi", "Tech");
        assertTrue(service.getAutocompleteSuggestions("   ").isEmpty());
    }

    @Test
    @DisplayName("autocompletar no devuelve stopwords")
    void autocomplete_stopwordMatch_notReturned() {
        service.indexFile(fileA, "Chemi", "Tech");
        // "es" es stopword, no debe aparecer en sugerencias
        List<String> suggestions = service.getAutocompleteSuggestions("e");
        assertFalse(suggestions.contains("es"));
    }

    @Test
    @DisplayName("autocompletar devuelve máximo 8 resultados")
    void autocomplete_manyMatches_maxEight() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (char c = 'a'; c <= 'z'; c++) {
            sb.append("test").append(c).append(" ");
        }
        File bigFile = tempDir.resolve("big.txt").toFile();
        Files.writeString(bigFile.toPath(), sb.toString());
        service.indexFile(bigFile, "X", "Y");

        List<String> suggestions = service.getAutocompleteSuggestions("test");
        assertTrue(suggestions.size() <= 8);
    }

    // ── removeDocument ─────────────────────────────────────────────

    @Test
    @DisplayName("eliminar documento existente devuelve true y reduce conteo")
    void removeDocument_existing_returnsTrueAndReducesCount() {
        service.indexFile(fileA, "Chemi", "Tech");
        String docId = service.getIndexedDocuments().get(0).getId();

        assertTrue(service.removeDocument(docId));
        assertEquals(0, service.getDocumentCount());
    }

    @Test
    @DisplayName("eliminar documento inexistente devuelve false")
    void removeDocument_nonExisting_returnsFalse() {
        assertFalse(service.removeDocument("id-que-no-existe"));
    }

    @Test
    @DisplayName("eliminar documento null devuelve false")
    void removeDocument_null_returnsFalse() {
        assertFalse(service.removeDocument(null));
    }

    @Test
    @DisplayName("eliminar documento lo quita de los resultados de búsqueda")
    void removeDocument_afterRemoval_notFoundInSearch() {
        service.indexFile(fileA, "Chemi", "Tech");
        String docId = service.getIndexedDocuments().get(0).getId();
        service.removeDocument(docId);

        assertTrue(service.search("java", null, null).isEmpty());
    }

    // ── getTopWordFrequencies ──────────────────────────────────────

    @Test
    @DisplayName("getTopWordFrequencies con topN=0 devuelve lista vacía")
    void getTopWordFrequencies_zeroN_returnsEmpty() {
        service.indexFile(fileA, "Chemi", "Tech");
        assertTrue(service.getTopWordFrequencies(0).isEmpty());
    }

    @Test
    @DisplayName("getTopWordFrequencies devuelve máximo N elementos")
    void getTopWordFrequencies_limitRespected() {
        service.indexFile(fileA, "Chemi", "Tech");
        List<WordStat> stats = service.getTopWordFrequencies(2);
        assertTrue(stats.size() <= 2);
    }

    @Test
    @DisplayName("getTopWordFrequencies no incluye stopwords")
    void getTopWordFrequencies_noStopwords() {
        service.indexFile(fileA, "Chemi", "Tech");
        List<WordStat> stats = service.getTopWordFrequencies(10);
        boolean hasStopword = stats.stream()
                .anyMatch(ws -> ws.word().equals("es") || ws.word().equals("de"));
        assertFalse(hasStopword);
    }

    @Test
    @DisplayName("palabra más frecuente aparece primera")
    void getTopWordFrequencies_mostFrequentFirst() {
        service.indexFile(fileA, "Chemi", "Tech"); // java aparece 3 veces
        List<WordStat> stats = service.getTopWordFrequencies(5);
        assertFalse(stats.isEmpty());
        assertEquals("java", stats.get(0).word());
    }

    // ── getCategories ──────────────────────────────────────────────

    @Test
    @DisplayName("getCategories devuelve categorías únicas y ordenadas")
    void getCategories_multipleDocuments_uniqueSorted() {
        service.indexFile(fileA, "Chemi", "Tech");
        service.indexFile(fileB, "Ana", "Ciencia");
        List<String> cats = service.getCategories();
        assertEquals(List.of("Ciencia", "Tech"), cats);
    }

    @Test
    @DisplayName("getCategories sin documentos devuelve lista vacía")
    void getCategories_empty_returnsEmpty() {
        assertTrue(service.getCategories().isEmpty());
    }

    // ── getIndexedDocuments ────────────────────────────────────────

    @Test
    @DisplayName("getIndexedDocuments devuelve documentos ordenados por título")
    void getIndexedDocuments_sorted() {
        service.indexFile(fileB, "Ana", "Ciencia");
        service.indexFile(fileA, "Chemi", "Tech");
        List<String> titles = service.getIndexedDocuments().stream()
                .map(d -> d.getTitle()).toList();
        assertEquals(List.of("docA.txt", "docB.txt"), titles);
    }

    // ── getTotalIndexedWords ───────────────────────────────────────

    @Test
    @DisplayName("getTotalIndexedWords suma palabras de todos los documentos")
    void getTotalIndexedWords_twoDocuments_sumsCorrectly() {
        service.indexFile(fileA, "Chemi", "Tech");
        service.indexFile(fileB, "Ana", "Ciencia");
        int total = service.getTotalIndexedWords();
        assertTrue(total > 0);
        assertEquals(
            service.getIndexedDocuments().stream().mapToInt(d -> d.getTotalWords()).sum(),
            total
        );
    }
}
