package co.edu.uptc.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Gestor sencillo de internacionalización para la aplicación.
 */
public final class I18n {

    private static final String BASE_NAME = "co.edu.uptc.i18n.messages";
    private static Locale currentLocale = Locale.forLanguageTag("es");

    private I18n() {
    }

    public static String get(String key) {
        return getBundle().getString(key);
    }

    public static String get(String key, Object... args) {
        return MessageFormat.format(getBundle().getString(key), args);
    }

    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle(BASE_NAME, currentLocale);
    }

    public static Locale getLocale() {
        return currentLocale;
    }

    public static void setLanguage(String language) {
        if (language == null || language.isBlank()) {
            return;
        }
        currentLocale = Locale.forLanguageTag(language);
    }

    public static void toggleLanguage() {
        setLanguage(isEnglish() ? "es" : "en");
    }

    public static boolean isEnglish() {
        return "en".equalsIgnoreCase(currentLocale.getLanguage());
    }
}
