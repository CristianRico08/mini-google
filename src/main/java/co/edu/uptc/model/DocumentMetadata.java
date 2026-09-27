package co.edu.uptc.model;

import java.time.LocalDate;

public record DocumentMetadata(
    String author,
    String category,
    LocalDate creationDate
) {}