package co.edu.uptc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class FileTextExtractor {

    public static String extractText(File file) throws IOException {
        String name = file.getName().toLowerCase();

        if (name.endsWith(".pdf")) {
            return extractFromPdf(file);
        } else if (name.endsWith(".docx")) {
            return extractFromDocx(file);
        } else if (name.endsWith(".txt")) {
            return Files.readString(file.toPath());
        } else {
            throw new IllegalArgumentException("Formato no soportado: " + name);
        }
    }

    private static String extractFromPdf(File file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private static String extractFromDocx(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }
}