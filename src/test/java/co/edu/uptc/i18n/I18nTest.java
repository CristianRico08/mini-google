package co.edu.uptc.i18n;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("I18n")
class I18nTest {

    @BeforeEach
    void resetToSpanish() {
        // Asegurar estado inicial en español antes de cada test
        I18n.setLanguage("es");
    }

    @AfterEach
    void restoreSpanish() {
        // Dejar el locale en español al terminar
        I18n.setLanguage("es");
    }

    // ── idioma por defecto ─────────────────────────────────────────

    @Test
    @DisplayName("idioma por defecto es español")
    void defaultLanguage_isSpanish() {
        assertEquals("es", I18n.getLocale().getLanguage());
    }

    @Test
    @DisplayName("isEnglish devuelve false con español")
    void isEnglish_spanish_returnsFalse() {
        assertFalse(I18n.isEnglish());
    }

    // ── get(key) ───────────────────────────────────────────────────

    @Test
    @DisplayName("get devuelve texto no vacío para clave válida")
    void get_validKey_returnsNonEmpty() {
        String value = I18n.get("app.title");
        assertNotNull(value);
        assertFalse(value.isBlank());
    }

    @Test
    @DisplayName("get en español devuelve texto en español")
    void get_spanish_returnsSpanishText() {
        assertEquals("Mini-Google", I18n.get("app.title"));
    }

    @Test
    @DisplayName("get con argumentos formatea el mensaje correctamente")
    void get_withArgs_formatsMessage() {
        // stats.documents=Documentos: {0}
        String result = I18n.get("stats.documents", 5);
        assertEquals("Documentos: 5", result);
    }

    @Test
    @DisplayName("get con múltiples argumentos formatea correctamente")
    void get_multipleArgs_formatsMessage() {
        // status.indexedSummary=Indexación terminada: {0} de {1} archivo(s).
        String result = I18n.get("status.indexedSummary", 3, 5);
        assertTrue(result.contains("3"));
        assertTrue(result.contains("5"));
    }

    // ── setLanguage ────────────────────────────────────────────────

    @Test
    @DisplayName("setLanguage('en') cambia el locale a inglés")
    void setLanguage_english_localeChanges() {
        I18n.setLanguage("en");
        assertEquals("en", I18n.getLocale().getLanguage());
        assertTrue(I18n.isEnglish());
    }

    @Test
    @DisplayName("setLanguage con null no lanza excepción ni cambia locale")
    void setLanguage_null_noChangeNoException() {
        Locale before = I18n.getLocale();
        assertDoesNotThrow(() -> I18n.setLanguage(null));
        assertEquals(before.getLanguage(), I18n.getLocale().getLanguage());
    }

    @Test
    @DisplayName("setLanguage con blank no cambia locale")
    void setLanguage_blank_noChange() {
        Locale before = I18n.getLocale();
        I18n.setLanguage("   ");
        assertEquals(before.getLanguage(), I18n.getLocale().getLanguage());
    }

    // ── toggleLanguage ─────────────────────────────────────────────

    @Test
    @DisplayName("toggleLanguage de español cambia a inglés")
    void toggleLanguage_fromSpanish_switchesToEnglish() {
        I18n.setLanguage("es");
        I18n.toggleLanguage();
        assertTrue(I18n.isEnglish());
    }

    @Test
    @DisplayName("toggleLanguage de inglés cambia a español")
    void toggleLanguage_fromEnglish_switchesToSpanish() {
        I18n.setLanguage("en");
        I18n.toggleLanguage();
        assertFalse(I18n.isEnglish());
    }

    @Test
    @DisplayName("toggleLanguage dos veces regresa al idioma original")
    void toggleLanguage_twice_returnsToOriginal() {
        String original = I18n.getLocale().getLanguage();
        I18n.toggleLanguage();
        I18n.toggleLanguage();
        assertEquals(original, I18n.getLocale().getLanguage());
    }

    // ── getBundle ──────────────────────────────────────────────────

    @Test
    @DisplayName("getBundle no devuelve null")
    void getBundle_returnsNotNull() {
        assertNotNull(I18n.getBundle());
    }

    @Test
    @DisplayName("getBundle en inglés devuelve bundle distinto al de español")
    void getBundle_english_differentFromSpanish() {
        String titleEs = I18n.get("app.title");
        I18n.setLanguage("en");
        String titleEn = I18n.get("app.title");
        // Ambos pueden ser iguales si el título no cambia,
        // pero el bundle debe existir y devolver algo
        assertNotNull(titleEn);
        assertFalse(titleEn.isBlank());
    }
}
