package co.edu.uptc.repository;

import co.edu.uptc.dto.TrieDTO;
import co.edu.uptc.mapper.TrieMapper;
import co.edu.uptc.model.Trie;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonIndexRepository")
class JsonIndexRepositoryTest {

    private Path tempDir;
    private JsonIndexRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Files.createTempDirectory("json-test");
        repository = new JsonIndexRepository(tempDir.resolve("index.json").toString());
    }

    @AfterEach
    void tearDown() throws Exception {
        // Limpiar archivos temporales
        Files.walk(tempDir)
             .map(Path::toFile)
             .sorted((a, b) -> -a.compareTo(b))
             .forEach(File::delete);
    }

    @Test
    @DisplayName("load sin archivo existente devuelve TrieDTO vacío")
    void load_noFile_returnsEmptyTrieDTO() {
        TrieDTO dto = repository.load();
        assertNotNull(dto);
        assertNull(dto.getRoot()); // TrieDTO vacío
    }

    @Test
    @DisplayName("save y load round-trip conserva los datos")
    void saveAndLoad_roundTrip_dataPreserved() {
        Trie trie = new Trie();
        trie.insert("java", "doc1");
        trie.insert("java", "doc1");
        trie.insert("python", "doc2");

        repository.save(TrieMapper.toDTO(trie));
        TrieDTO loaded = repository.load();
        Trie recovered = TrieMapper.toDomain(loaded);

        assertEquals(2, recovered.getDocumentFrequencies("java").get("doc1"));
        assertEquals(1, recovered.getDocumentFrequencies("python").get("doc2"));
    }

    @Test
    @DisplayName("save crea el archivo en disco")
    void save_createsFile() {
        repository.save(TrieMapper.toDTO(new Trie()));
        assertTrue(tempDir.resolve("index.json").toFile().exists());
    }

    @Test
    @DisplayName("save sobrescribe correctamente en llamadas sucesivas")
    void save_twice_lastValueWins() {
        Trie trie1 = new Trie();
        trie1.insert("java", "doc1");
        repository.save(TrieMapper.toDTO(trie1));

        Trie trie2 = new Trie();
        trie2.insert("python", "doc2");
        repository.save(TrieMapper.toDTO(trie2));

        Trie recovered = TrieMapper.toDomain(repository.load());
        assertTrue(recovered.getDocumentFrequencies("java").isEmpty());
        assertFalse(recovered.getDocumentFrequencies("python").isEmpty());
    }

    @Test
    @DisplayName("constructor crea directorio si no existe")
    void constructor_missingDir_dirCreated() {
        Path nested = tempDir.resolve("a/b/c/index.json");
        new JsonIndexRepository(nested.toString());
        assertTrue(nested.getParent().toFile().exists());
    }
}
