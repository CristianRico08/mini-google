package co.edu.uptc.model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AVLTreeTest {

    @Test
    void testAVLInsertionAndOrder() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(50);
        tree.insert(20);
        tree.insert(70);
        tree.insert(10);
        tree.insert(30);

        List<Integer> inOrder = tree.inOrderTraversal();

        assertEquals(List.of(10, 20, 30, 50, 70), inOrder);
    }

    @Test
    void testSearchResultOrdering() {
        AVLTree<SearchResult> tree = new AVLTree<>();
        Document doc1 = new Document("1", "DocA", "/path/a", 100, null);
        Document doc2 = new Document("2", "DocB", "/path/b", 100, null);

        SearchResult r1 = new SearchResult(doc1, 0.25);
        SearchResult r2 = new SearchResult(doc2, 0.85);

        tree.insert(r1);
        tree.insert(r2);

        List<SearchResult> results = tree.inOrderTraversal();

        // SearchResult implementa orden descendente por puntaje TF-IDF
        assertEquals(0.85, results.get(0).getScore());
        assertEquals(0.25, results.get(1).getScore());
    }
}