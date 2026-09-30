package co.edu.uptc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AVLTree")
class AVLTreeTest {

    private AVLTree<Integer> tree;

    @BeforeEach
    void setUp() {
        tree = new AVLTree<>();
    }

    @Test
    @DisplayName("inOrderTraversal en árbol vacío devuelve lista vacía")
    void inOrder_emptyTree_returnsEmptyList() {
        assertTrue(tree.inOrderTraversal().isEmpty());
    }

    @Test
    @DisplayName("insertar un solo elemento y recuperarlo")
    void insert_singleElement_returnedInOrder() {
        tree.insert(42);
        assertEquals(List.of(42), tree.inOrderTraversal());
    }

    @Test
    @DisplayName("inOrder devuelve elementos en orden ascendente")
    void insert_multipleElements_sortedAscending() {
        tree.insert(5);
        tree.insert(3);
        tree.insert(7);
        tree.insert(1);
        tree.insert(4);
        assertEquals(List.of(1, 3, 4, 5, 7), tree.inOrderTraversal());
    }

    @Test
    @DisplayName("inserción en orden ascendente no rompe el balance (rotación izquierda)")
    void insert_ascendingOrder_remainsBalanced() {
        // Sin balance esto sería O(n), con AVL queda balanceado
        tree.insert(1);
        tree.insert(2);
        tree.insert(3);
        assertEquals(List.of(1, 2, 3), tree.inOrderTraversal());
    }

    @Test
    @DisplayName("inserción en orden descendente no rompe el balance (rotación derecha)")
    void insert_descendingOrder_remainsBalanced() {
        tree.insert(3);
        tree.insert(2);
        tree.insert(1);
        assertEquals(List.of(1, 2, 3), tree.inOrderTraversal());
    }

    @Test
    @DisplayName("inserción con rotación doble izquierda-derecha")
    void insert_leftRightRotation_remainsBalanced() {
        tree.insert(3);
        tree.insert(1);
        tree.insert(2);
        assertEquals(List.of(1, 2, 3), tree.inOrderTraversal());
    }

    @Test
    @DisplayName("inserción con rotación doble derecha-izquierda")
    void insert_rightLeftRotation_remainsBalanced() {
        tree.insert(1);
        tree.insert(3);
        tree.insert(2);
        assertEquals(List.of(1, 2, 3), tree.inOrderTraversal());
    }

    @Test
    @DisplayName("AVLTree con SearchResult ordena por score descendente")
    void insert_searchResults_sortedByScore() {
        AVLTree<SearchResult> resultTree = new AVLTree<>();
        Document d1 = new Document("1", "Doc1", "/d1", 100, null);
        Document d2 = new Document("2", "Doc2", "/d2", 100, null);
        Document d3 = new Document("3", "Doc3", "/d3", 100, null);

        // SearchResult.compareTo ordena descendente (mayor score primero)
        resultTree.insert(new SearchResult(d1, 0.5));
        resultTree.insert(new SearchResult(d2, 0.9));
        resultTree.insert(new SearchResult(d3, 0.1));

        List<SearchResult> results = resultTree.inOrderTraversal();
        assertEquals(3, results.size());
        // inOrder del AVL con compareTo descendente: 0.9, 0.5, 0.1
        assertEquals(0.9, results.get(0).getScore(), 0.0001);
        assertEquals(0.5, results.get(1).getScore(), 0.0001);
        assertEquals(0.1, results.get(2).getScore(), 0.0001);
    }
}
