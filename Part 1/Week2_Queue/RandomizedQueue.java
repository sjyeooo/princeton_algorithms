/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;

public class RandomizedQueue<Item> implements Iterable<Item> {
    /**
     * Constant holding initial array size
     */
    private static final int MIN_ARRAY_SIZE = 2;

    /**
     * Number of elements in {@code RandomizedQueue}
     */
    private int size;

    /**
     * Array structure holding all elements
     */
    private Item[] items;

    // construct an empty randomized queue
    public RandomizedQueue() {
        size = 0;
        items = (Item[]) new Object[MIN_ARRAY_SIZE];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return (this.size == 0);
    }

    // return the number of items on the randomized queue
    public int size() {
        return this.size;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        // Double array size if current array is already full
        if (this.size == this.items.length) {
            resize(size * 2);
        }

        // Add new item to array
        this.items[size++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (this.isEmpty()) {
            throw new java.util.NoSuchElementException();
        }

        // Randomly choose item to remove
        int index = StdRandom.uniform(0, size);
        Item retVal = this.items[index];
        // Take last element in array and place it at where the item was removed.
        this.items[index] = this.items[size - 1];
        this.items[size - 1] = null;

        this.size--;

        // Reduce array size by half if current array is only quarter full
        if (this.size == (this.items.length / 4)) {
            resize(this.items.length / 2);
        }

        return retVal;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (this.isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        // Choose element at random and return that item
        int index = StdRandom.uniform(0, size);
        return (this.items[index]);
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private final Item[] array;
        private int index;

        public RandomizedQueueIterator() {
            array = (Item[]) new Object[size];
            for (int i = 0; i < size; i++) {
                array[i] = items[i];
            }
            StdRandom.shuffle(array);
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException("no more elements");
            }
            return array[index++];
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove operation not supported");
        }
    }

    private void resize(int newSize) {
        // Only resize if it's larger than minimum array size
        if (newSize > MIN_ARRAY_SIZE) {
            Item[] newArray = (Item[]) new Object[newSize];
            // Copy data from class items array to new array
            for (int i = 0; i < this.size; i++) {
                newArray[i] = this.items[i];
            }
            // Replace class items array to new array
            this.items = newArray;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> testRanQueue = new RandomizedQueue<>();

        testRanQueue.enqueue(1);
        testRanQueue.enqueue(2);
        testRanQueue.enqueue(3);
        testRanQueue.enqueue(4);
        testRanQueue.enqueue(5);

        // Test iterator
        for (Integer curr : testRanQueue)
            System.out.println(curr);

        System.out.println("dequeue = " + testRanQueue.dequeue());
        System.out.println("sample = " + testRanQueue.sample());
        System.out.println("sample = " + testRanQueue.sample());
        System.out.println("sample = " + testRanQueue.sample());
        System.out.println("sample = " + testRanQueue.sample());

    }

}
