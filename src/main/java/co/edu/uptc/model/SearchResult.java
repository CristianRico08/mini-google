package co.edu.uptc.model;

public class SearchResult implements Comparable<SearchResult> {
    private final Document document;
    private final double score; // Puntaje TF-IDF

    public SearchResult(Document document, double score) {
        this.document = document;
        this.score = score;
    }

    public Document getDocument() { return document; }
    public double getScore() { return score; }

    @Override
    public int compareTo(SearchResult o) {
        // Orden descendente (los de mayor score quedan al frente)
        return Double.compare(o.score, this.score);
    }
}