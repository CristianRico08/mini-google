package co.edu.uptc.repository;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import co.edu.uptc.dto.TrieDTO;
import co.edu.uptc.dto.TrieNodeDTO;

class JsonIndexRepositoryTest {

    @Test
    void testSaveAndLoad(@TempDir Path tempDir) {
        Path jsonPath = tempDir.resolve("index_test.json");
        JsonIndexRepository repository = new JsonIndexRepository(jsonPath.toString());

        TrieDTO dto = new TrieDTO(new TrieNodeDTO());
        dto.getRoot().setEndOfWord(true);

        repository.save(dto);

        TrieDTO loadedDto = repository.load();
        assertNotNull(loadedDto);
        assertTrue(loadedDto.getRoot().isEndOfWord());
    }
}