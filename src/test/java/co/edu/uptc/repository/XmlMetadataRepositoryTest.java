package co.edu.uptc.repository;

import co.edu.uptc.dto.DocumentDTO;
import co.edu.uptc.dto.DocumentMetadataDTO;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("XmlMetadataRepository")
class XmlMetadataRepositoryTest {

    private Path tempDir;
    private XmlMetadataRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Files.createTempDirectory("xml-test");
        repository = new XmlMetadataRepository(tempDir.resolve("metadata.xml").toString());
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.walk(tempDir)
             .map(Path::toFile)
             .sorted((a, b) -> -a.compareTo(b))
             .forEach(File::delete);
    }

    @Test
    @DisplayName("loadAll sin archivo devuelve lista vacía")
    void loadAll_noFile_returnsEmptyList() {
        assertTrue(repository.loadAll().isEmpty());
    }

    @Test
    @DisplayName("saveAll y loadAll round-trip conserva los datos")
    void saveAndLoadAll_roundTrip_dataPreserved() {
        DocumentMetadataDTO meta = new DocumentMetadataDTO("Chemi", "Tech", "2024-01-10");
        DocumentDTO doc = new DocumentDTO("id1", "Documento 1", "/ruta/doc1.txt", 150, meta);

        repository.saveAll(List.of(doc));
        List<DocumentDTO> loaded = repository.loadAll();

        assertEquals(1, loaded.size());
        assertEquals("id1", loaded.get(0).getId());
        assertEquals("Documento 1", loaded.get(0).getTitle());
        assertEquals(150, loaded.get(0).getTotalWords());
        assertEquals("Chemi", loaded.get(0).getMetadata().getAuthor());
        assertEquals("Tech", loaded.get(0).getMetadata().getCategory());
        assertEquals("2024-01-10", loaded.get(0).getMetadata().getCreationDate());
    }

    @Test
    @DisplayName("saveAll con múltiples documentos los persiste todos")
    void saveAll_multipleDocuments_allPersisted() {
        DocumentDTO doc1 = new DocumentDTO("id1", "Doc1", "/d1.txt", 100, null);
        DocumentDTO doc2 = new DocumentDTO("id2", "Doc2", "/d2.txt", 200, null);
        DocumentDTO doc3 = new DocumentDTO("id3", "Doc3", "/d3.txt", 300, null);

        repository.saveAll(List.of(doc1, doc2, doc3));
        List<DocumentDTO> loaded = repository.loadAll();

        assertEquals(3, loaded.size());
    }

    @Test
    @DisplayName("saveAll sobrescribe correctamente")
    void saveAll_twice_lastValueWins() {
        DocumentDTO doc1 = new DocumentDTO("id1", "Doc1", "/d1.txt", 100, null);
        repository.saveAll(List.of(doc1));

        DocumentDTO doc2 = new DocumentDTO("id2", "Doc2", "/d2.txt", 200, null);
        repository.saveAll(List.of(doc2));

        List<DocumentDTO> loaded = repository.loadAll();
        assertEquals(1, loaded.size());
        assertEquals("id2", loaded.get(0).getId());
    }

    @Test
    @DisplayName("saveAll lista vacía borra el contenido anterior")
    void saveAll_emptyList_clearsFile() {
        DocumentDTO doc = new DocumentDTO("id1", "Doc", "/d.txt", 10, null);
        repository.saveAll(List.of(doc));
        repository.saveAll(List.of());

        assertTrue(repository.loadAll().isEmpty());
    }

    @Test
    @DisplayName("constructor crea directorio si no existe")
    void constructor_missingDir_dirCreated() {
        Path nested = tempDir.resolve("x/y/z/metadata.xml");
        new XmlMetadataRepository(nested.toString());
        assertTrue(nested.getParent().toFile().exists());
    }
}
