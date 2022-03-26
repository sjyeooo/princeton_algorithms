/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import java.util.Iterator;

public class Deque<Item> implements Iterable<Item> {

    private Node first = null;
    private Node last = null;
    private int length;

    private class Node {
        public Item value;
        public Node next;
        public Node past;

        // Constructor for node
        private Node(Item val, Node next, Node past) {
            this.value = val;
            this.next = next;
            this.past = past;
        }
    }

    // construct an empty deque
    public Deque() {
        this.length = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return this.length == 0;
    }

    // return the number of items on the deque
    public int size() {
        return this.length;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        Node newNode = new Node(item, null, null);

        if (this.length == 0) {
            this.last = newNode;
            this.first = newNode;
        }
        else {
            newNode.next = this.first;
            this.first.past = newNode;
            this.first = newNode;
        }
        this.length++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        Node newNode = new Node(item, null, null);

        if (this.length == 0) {
            this.last = newNode;
            this.first = newNode;
        }
        else {
            newNode.past = this.last;
            this.last.next = newNode;
            this.last = newNode;
        }
        this.length++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (this.length == 0) {
            throw new java.util.NoSuchElementException();
        }

        Item retVal = this.first.value;

        if (this.length == 1) {
            this.first = null;
            this.last = null;
        }
        else {
            this.first = this.first.next;
            this.first.past = null;
        }

        this.length--;

        return retVal;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (this.length == 0) {
            throw new java.util.NoSuchElementException();
        }

        Item retVal = this.last.value;

        if (this.length == 1) {
            this.last = null;
            this.first = null;
        }
        else {
            this.last = this.last.past;
            this.last.next = null;
        }

        this.length--;

        return retVal;
    }

    @Override
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        // Initialise current node as first
        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public Item next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException("the naxt() is overflow");
            }
            else {
                Item item = current.value;
                current = current.next;
                return item;
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("remove method is unsupported");
        }
    }


    // unit testing (required)
    public static void main(String[] args) {

        Deque<Integer> deqTest = new Deque<Integer>();
        deqTest.addFirst(2);
        deqTest.addFirst(3);
        deqTest.addLast(10);
        deqTest.addFirst(7);

        for (Integer curr : deqTest)
            System.out.println(curr);

        System.out.println("removeFirst = " + deqTest.removeFirst());
        System.out.println("removeLast = " + deqTest.removeLast());
    }

}
