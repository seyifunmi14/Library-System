package ca.umanitoba.cs.oluwasef.domain;

import com.google.common.base.Preconditions;

/**
 * a simple Linked-list implementation of the {@link Stack} interface.
 *
 * @param <T> the type of elements stored on the stack
 */
public final class LinkedStack<T> implements Stack<T> {

    /**
     * a singly-linked node class.
     */
    private static class Node<E> {
        private final E value;
        private final Node<E> next;

        private Node(E value, Node<E> next) {
            this.value = value;
            this.next = next;
        }
    }

    private Node<T> top;
    private int size;

    /**
     * create an empty stack.
     */
    public LinkedStack() {
        this.top = null;
        this.size = 0;
    }

    /**
     * Pushes an item onto the top of this stack.
     *
     * @param item the item to be pushed onto this stack. Must not be {@code null}.
     */
    @Override
    public void push(T item) {
        Preconditions.checkNotNull(item, "item must not be null");
        top = new Node<>(item, top);
        size++;
    }

    /**
     * Removes the object at the top of this stack and returns that object as the value of this function.
     *
     * @return the object at the top of this stack.
     * @throws IllegalStateException if the stack is empty.
     */
    @Override
    public T pop() {
        Preconditions.checkState(!isEmpty(), "stack must not be empty to pop");
        T value = top.value;
        top = top.next;
        size--;
        return value;
    }

    /**
     * Looks at the object at the top of this stack without removing it from the stack.
     *
     * @return the object at the top of this stack.
     * @throws IllegalStateException if the stack is empty.
     */
    @Override
    public T peek() {
        Preconditions.checkState(!isEmpty(), "stack must not be empty to peek");
        return top.value;
    }

    /**
     * Tests if this stack is empty.
     *
     * @return {@code true} if the stack contains no items; {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of items in this stack.
     *
     * @return the number of elements in the stack.
     */
    @Override
    public int size() {
        return size;
    }
}