package co.edu.uptc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileTextExtractorTest {

    @Test
    void testExtractTextFromTxt(@TempDir Path tempDir) throws IOException {
        File txtFile = tempDir.resolve("test.txt").toFile();
        String expectedContent = "Contenido de prueba para archivo plano TXT.";
        Files.writeString(txtFile.toPath(), expectedContent);

        String extractedText = FileTextExtractor.extractText(txtFile);

        assertEquals(expectedContent, extractedText.trim());
    }

    @Test
    void testExtractTextFromPdf(@TempDir Path tempDir) throws IOException {
        File pdfFile = tempDir.resolve("test.pdf").toFile();
        String expectedText = "Texto de prueba dentro del archivo PDF";

        // Generar un PDF básico en memoria y guardarlo en el directorio temporal
        try (PDDocument pdfDoc = new PDDocument()) {
            PDPage page = new PDPage();
            pdfDoc.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(pdfDoc, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText(expectedText);
                contentStream.endText();
            }

            pdfDoc.save(pdfFile);
        }

        String extractedText = FileTextExtractor.extractText(pdfFile);

        assertTrue(extractedText.contains(expectedText));
    }

    @Test
    void testExtractTextFromDocx(@TempDir Path tempDir) throws IOException {
        File docxFile = tempDir.resolve("test.docx").toFile();
        String expectedText = "Texto de prueba dentro del archivo Word DOCX";

        // Generar un documento DOCX básico en memoria y guardarlo
        try (XWPFDocument docxDoc = new XWPFDocument()) {
            XWPFParagraph paragraph = docxDoc.createParagraph();
            paragraph.createRun().setText(expectedText);

            try (FileOutputStream out = new FileOutputStream(docxFile)) {
                docxDoc.write(out);
            }
        }

        String extractedText = FileTextExtractor.extractText(docxFile);

        assertTrue(extractedText.contains(expectedText));
    }

    @Test
    void testUnsupportedFileExtension(@TempDir Path tempDir) throws IOException {
        File unsupportedFile = tempDir.resolve("test.png").toFile();
        Files.writeString(unsupportedFile.toPath(), "fake image content");

        assertThrows(IllegalArgumentException.class, () -> {
            FileTextExtractor.extractText(unsupportedFile);
        });
    }
}
