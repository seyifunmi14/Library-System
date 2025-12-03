package ca.umanitoba.cs.oluwasef.domain;

/**
 * Simple stack interface required by the path-finding algorithm.
 */
public interface Stack<T> {
    void push(T item);
    T pop();
    T peek();
    boolean isEmpty();
    int size();
}
