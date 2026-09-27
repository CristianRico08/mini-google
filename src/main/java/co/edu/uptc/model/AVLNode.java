package co.edu.uptc.model;

public class AVLNode<T extends Comparable<T>> {
    private T data;
    private int height;
    private AVLNode<T> left;
    private AVLNode<T> right;

    public AVLNode(T data) {
        this.data = data;
        this.height = 1;
    }

    // Getters y Setters
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public AVLNode<T> getLeft() { return left; }
    public void setLeft(AVLNode<T> left) { this.left = left; }
    public AVLNode<T> getRight() { return right; }
    public void setRight(AVLNode<T> right) { this.right = right; }
}