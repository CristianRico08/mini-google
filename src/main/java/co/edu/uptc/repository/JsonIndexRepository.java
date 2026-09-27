package co.edu.uptc.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import co.edu.uptc.dto.TrieDTO;
import co.edu.uptc.exception.RepositoryException;

import java.io.File;
import java.io.IOException;

public class JsonIndexRepository {

    private final ObjectMapper objectMapper;
    private final File storageFile;

    public JsonIndexRepository(String filePath) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.storageFile = new File(filePath);
        ensureDirectoryExists();
    }

    private void ensureDirectoryExists() {
        File parent = storageFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    public void save(TrieDTO trieDTO) {
        try {
            objectMapper.writeValue(storageFile, trieDTO);
        } catch (IOException e) {
            throw new RepositoryException("Error guardando el índice JSON en: " + storageFile.getPath(), e);
        }
    }

    public TrieDTO load() {
        if (!storageFile.exists()) {
            return new TrieDTO();
        }
        try {
            return objectMapper.readValue(storageFile, TrieDTO.class);
        } catch (IOException e) {
            throw new RepositoryException("Error leyendo el índice JSON desde: " + storageFile.getPath(), e);
        }
    }
}