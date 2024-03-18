package edu.uwm.cs351.util.util;

import java.util.*;
import java.util.function.Consumer;

public class RobustQueue<E> extends AbstractQueue<E> {
	
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}
    
    // Node class for doubly linked list
    private static class Node<E> {
        E data;
        Node<E> next;
        Node<E> prev;
        
        Node(E element) {
            data = element;
        }

    }
    
    // Fields
    private Node<E> dummy;
    private int size;
    
    private boolean wellFormed() {
    	return true;
    }
    
	private RobustQueue(boolean ignored) {} // do not change this constructor

    // Constructor
    public RobustQueue() {
        dummy = new Node<>(null);
        dummy.next = dummy;
        dummy.prev = dummy;
        size = 0;
    }
    
    // Queue methods
    @Override
    public boolean add(E e) {
        offer(e);
        return true;
    }
    
    @Override
    public boolean offer(E e) {
        Node<E> newNode = new Node<>(e);
        newNode.next = dummy;
        newNode.prev = dummy.prev;
        dummy.prev.next = newNode;
        dummy.prev = newNode;
        size++;
        return true;
    }
    
    @Override
    public E remove() {
        if (size == 0)
            throw new NoSuchElementException();
        return poll();
    }
    
    @Override
    public E poll() {
        if (size == 0)
            return null;
        E element = dummy.next.data;
        dummy.next.data = null;
        dummy.next = dummy.next.next;
        dummy.next.prev = dummy;
        size--;
        return element;
    }
    
    @Override
    public E element() {
        if (size == 0)
            throw new NoSuchElementException();
        return peek();
    }
    
    @Override
    public E peek() {
        if (size == 0)
            return null;
        return dummy.next.data;
    }
    
    @Override
    public void clear() {
        while (size > 0)
            poll();
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }
    
    // Iterator class
    private class MyIterator implements Iterator<E> {
        private Node<E> current;
        
		private boolean wellFormed() {
			return true;
		}
		
        MyIterator() {
            current = dummy.next;
        }
        
		MyIterator(boolean ignored) {} // do not change this constructor

        @Override
        public boolean hasNext() {
            return current != dummy;
        }
        
        @Override
        public E next() {
            if (!hasNext())
                throw new NoSuchElementException();
            E element = current.data;
            current = current.next;
            return element;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }


    }
    
    // Spy class
    public static class Spy<T> {
		/**
		 * A public version of the data structure's internal node class.
		 * This class is only used for testing.
		 */
		public static class Node<U> extends RobustQueue.Node<U> {
			/**
			 * Create a node with null data, tag, prev, and next fields.
			 */
			public Node() {
				this(null, null, null);
			}
			/**
			 * Create a node with the given values
			 * @param d data for new node, may be null
			 * @param t tag for new node,may be null
			 * @param p prev for new node, may be null
			 * @param n next for new node, may be null
			 */
			public Node(U d, Node<U> p, Node<U> n) {
				super(null);
				this.data = d;
				this.prev = p;
				this.next = n;
			}
		}
		
		/**
		 * Create a node for testing.
		 * @param d data for new node, may be null
		 * @param t tag for new node,may be null
		 * @param p prev for new node, may be null
		 * @param n next for new node, may be null
		 * @return newly created test node
		 */
		public Node<T> newNode(T t, Node<T> p, Node<T> n) {
			return new Node<T>(t, p, n);
		}
		
		/**
		 * Create a node with all null fields for testing.
		 * @return newly created test node
		 */
		public Node<T> newNode() {
			return new Node<T>();
		}
		
		/**
		 * Change a node's next field
		 * @param n1 node to change, must not be null
		 * @param n2 node to point to, may be null
		 */
		public void setNext(Node<T> n1, Node<T> n2) {
			n1.next = n2;
		}
		
		/**
		 * Change a node's prev field
		 * @param n1 node to change, must not be null
		 * @param n2 node to point to, may be null
		 */
		public void setPrev(Node<T> n1, Node<T> n2) {
			n1.prev = n2;
		}

		/**
		 * Return the sink for invariant error messages
		 * @return current reporter
		 */
		public Consumer<String> getReporter() {
			return reporter;
		}

		/**
		 * Change the sink for invariant error messages.
		 * @param r where to send invariant error messages.
		 */
		public void setReporter(Consumer<String> r) {
			reporter = r;
		}

		/**
		 * Create a testing instance of a linked tag collection with the given
		 * data structure.
		 * @param d the dummy node
		 * @param s the size
		 * @param v the version
		 * @return a new testing linked tag collection with this data structure.
		 */
		public <U> RobustQueue<U> newInstance(Node<U> d, int s) {
			RobustQueue<U> result = new RobustQueue<U>(false);
			result.dummy = d;
			result.size = s;
			return result;
		}
			
		/**
		 * Creates a testing instance of an iterator
		 * @param outer the collection attached to the iterator
		 * @param t the tag
		 * @param c the current node
		 * @param n the next node
		 * @param cv the colVersion
		 */
		public <E> Iterator<E> newIterator(RobustQueue<E> outer, Node<E> c) {
			RobustQueue<E>.MyIterator result = outer.new MyIterator(false);
			result.current = c;
			return result;
		}
			
		/**
		 * Check the invariant on the given dynamic array robot.
		 * @param r robot to check, must not be null
		 * @return whether the invariant is computed as true
		 */
		public boolean wellFormed(RobustQueue<?> r) {
			return r.wellFormed();
		}
			
		/**
		 * Check the invariant on the given Iterator.
		 * @param it iterator to check, must not be null
		 * @return whether the invariant is computed as true
		 */
		public <E> boolean wellFormed(Iterator<E> it) {
			RobustQueue<E>.MyIterator myIt = (RobustQueue<E>.MyIterator)it;
			return myIt.wellFormed();
		}
	}
}

