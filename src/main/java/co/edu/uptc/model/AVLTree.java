package co.edu.uptc.model;

import java.util.ArrayList;
import java.util.List;

public class AVLTree<T extends Comparable<T>> {
    private AVLNode<T> root;

    public void insert(T data) {
        root = insert(root, data);
    }

    private AVLNode<T> insert(AVLNode<T> node, T data) {
        if (node == null) return new AVLNode<>(data);

        if (data.compareTo(node.getData()) < 0) {
            node.setLeft(insert(node.getLeft(), data));
        } else {
            node.setRight(insert(node.getRight(), data));
        }

        updateHeight(node);
        return balance(node);
    }

    public List<T> inOrderTraversal() {
        List<T> list = new ArrayList<>();
        inOrder(root, list);
        return list;
    }

    private void inOrder(AVLNode<T> node, List<T> list) {
        if (node != null) {
            inOrder(node.getLeft(), list);
            list.add(node.getData());
            inOrder(node.getRight(), list);
        }
    }

    private int height(AVLNode<T> n) {
        return n == null ? 0 : n.getHeight();
    }

    private void updateHeight(AVLNode<T> n) {
        n.setHeight(1 + Math.max(height(n.getLeft()), height(n.getRight())));
    }

    private int getBalance(AVLNode<T> n) {
        return n == null ? 0 : height(n.getLeft()) - height(n.getRight());
    }

    private AVLNode<T> balance(AVLNode<T> node) {
        int balance = getBalance(node);

        if (balance > 1) {
            if (getBalance(node.getLeft()) < 0) {
                node.setLeft(rotateLeft(node.getLeft()));
            }
            return rotateRight(node);
        }
        if (balance < -1) {
            if (getBalance(node.getRight()) > 0) {
                node.setRight(rotateRight(node.getRight()));
            }
            return rotateLeft(node);
        }
        return node;
    }

    private AVLNode<T> rotateRight(AVLNode<T> y) {
        AVLNode<T> x = y.getLeft();
        AVLNode<T> T2 = x.getRight();
        x.setRight(y);
        y.setLeft(T2);
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private AVLNode<T> rotateLeft(AVLNode<T> x) {
        AVLNode<T> y = x.getRight();
        AVLNode<T> T2 = y.getLeft();
        y.setLeft(x);
        x.setRight(T2);
        updateHeight(x);
        updateHeight(y);
        return y;
    }
}