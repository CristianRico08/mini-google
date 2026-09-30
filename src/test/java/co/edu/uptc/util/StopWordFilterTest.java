package co.edu.uptc.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StopWordFilter")
class StopWordFilterTest {

    @Test
    @DisplayName("null es stopword")
    void isStopword_null_returnsTrue() {
        assertTrue(StopWordFilter.isStopword(null));
    }

    @Test
    @DisplayName("cadena en blanco es stopword")
    void isStopword_blank_returnsTrue() {
        assertTrue(StopWordFilter.isStopword("   "));
    }

    @Test
    @DisplayName("número puro es stopword")
    void isStopword_number_returnsTrue() {
        assertTrue(StopWordFilter.isStopword("123"));
    }

    @Test
    @DisplayName("artículos en español son stopwords")
    void isStopword_spanishArticles_returnsTrue() {
        assertTrue(StopWordFilter.isStopword("el"));
        assertTrue(StopWordFilter.isStopword("la"));
        assertTrue(StopWordFilter.isStopword("los"));
        assertTrue(StopWordFilter.isStopword("las"));
        assertTrue(StopWordFilter.isStopword("un"));
        assertTrue(StopWordFilter.isStopword("una"));
    }

    @Test
    @DisplayName("preposiciones en español son stopwords")
    void isStopword_spanishPrepositions_returnsTrue() {
        assertTrue(StopWordFilter.isStopword("de"));
        assertTrue(StopWordFilter.isStopword("en"));
        assertTrue(StopWordFilter.isStopword("con"));
        assertTrue(StopWordFilter.isStopword("para"));
        assertTrue(StopWordFilter.isStopword("por"));
    }

    @Test
    @DisplayName("stopwords en inglés son reconocidas")
    void isStopword_english_returnsTrue() {
        assertTrue(StopWordFilter.isStopword("the"));
        assertTrue(StopWordFilter.isStopword("and"));
        assertTrue(StopWordFilter.isStopword("of"));
    }

    @Test
    @DisplayName("isStopword es case-insensitive")
    void isStopword_uppercase_returnsTrue() {
        assertTrue(StopWordFilter.isStopword("EL"));
        assertTrue(StopWordFilter.isStopword("THE"));
    }

    @Test
    @DisplayName("palabra de contenido no es stopword")
    void isStopword_contentWord_returnsFalse() {
        assertFalse(StopWordFilter.isStopword("java"));
        assertFalse(StopWordFilter.isStopword("algoritmo"));
        assertFalse(StopWordFilter.isStopword("búsqueda"));
    }
}
