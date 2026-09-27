package co.edu.uptc.model;

public record WordStat(String word, int frequency) implements Comparable<WordStat> {
    @Override
    public int compareTo(WordStat o) {
        return Integer.compare(o.frequency, this.frequency);
    }
}