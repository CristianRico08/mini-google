package co.edu.uptc.controller;

import java.util.List;

public record IndexingReport(
        int indexed,
        int selected,
        List<String> errors) {

    public IndexingReport {
        errors = errors == null
                ? List.of()
                : List.copyOf(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}