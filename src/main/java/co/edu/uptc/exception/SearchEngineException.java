package co.edu.uptc.exception;

/**
 * Excepción de dominio para errores ocurridos durante la indexación o búsqueda.
 */
public class SearchEngineException extends RuntimeException {

    public SearchEngineException(String message) {
        super(message);
    }

    public SearchEngineException(String message, Throwable cause) {
        super(message, cause);
    }
}
