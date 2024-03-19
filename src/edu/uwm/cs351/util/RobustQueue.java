package edu.uwm.cs351.util;
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
	    if (dummy == null) {
	        return report("Dummy node is null");
	    }
	    if (dummy.data != null) {
	        return report("Dummy node's data or tag is not null");
	    }
	    Node<E> current = dummy.next;
	    Node<E> prevNode = dummy;
	    boolean dummySeen = false;
	    if (size == 0) {
	    	if (dummy.next != dummy || dummy.prev != dummy) return report("Incorrect prev link in a node");
	    	dummySeen = true;
	    }
	    else {
		    while (!dummySeen && current != null) {
		    	if (current == dummy) dummySeen = true;
		        if (current.prev != prevNode) {
		            return report("Incorrect prev link in a node");
		        }
		        prevNode = current;
		        current = current.next;
		    }
	    }
	    int count = 0;
	    current = dummy.next;
	    while (current != dummy && current != null) {
	        count++;
	        if (current.prev == null || current.next == null) return report("Null link in a node");
	        if (current.data == null) return report("Null tag in a node");
	        current = current.next;
	    }
	    if (count != size) {
	        return report("Size field does not match the number of non-dummy nodes");
	    }
	    return true;
	}


    
	private RobustQueue(boolean ignored) {} // do not change this constructor

    // Constructor
    public RobustQueue() {
        dummy = new Node<>(null);
        dummy.next = dummy;
        dummy.prev = dummy;
        size = 0;
        assert wellFormed(): "invariant failed at the end of the constructor";
    }
    
    
	@Override // required
	public boolean offer(E e) {
	    assert wellFormed(): "invariant broken in offer";
	    if (e == null) throw new NullPointerException("nothig to add");
	    // Create a new node with the given element
	    Node<E> newNode = new Node<>(e);
	    
	    // Link the new node to the end of the queue
	    newNode.prev = dummy.prev;
	    newNode.next = dummy;
	    dummy.prev.next = newNode;
	    dummy.prev = newNode;
	    
	    size++; // Increment the size of the queue
	    
	    assert wellFormed(): "invariant broken by offer";
	    return true;
	}

    @Override // required
    public E poll() {
        assert wellFormed(): "invariant in poll";
        
        if (size == 0) // If the queue is empty, return null
            return null;
        
        // Remove the first element in the queue
        Node<E> firstNode = dummy.next;
        dummy.next = firstNode.next;
        firstNode.next.prev = dummy;
        
        size--; // Decrement the size of the queue
        
        assert wellFormed(): "invariant by poll";
        return firstNode.data;
    }

    @Override // required
    public E peek() {
        assert wellFormed(): "invariant in peek";
        
        if (size == 0) // If the queue is empty, return null
            return null;
        
        // Return the data of the first element in the queue
        return dummy.next.data;
    }
    
    
    @Override // required
    public int size() {
        return size;
    }
    
  
    @Override
    public Iterator<E> iterator() {
    	assert wellFormed(): "invariant in iterator";
        return new MyIterator();
    }
    
    // Iterator class
    private class MyIterator implements Iterator<E> {
        private Node<E> current;
        
        private boolean wellFormed() {
            if (!RobustQueue.this.wellFormed()) 
                return false;

            // 1. The current pointer is never null.
            if (current == null)
                return report("Current pointer is null");

//             2. If the current pointer's data field is not null, then it is in the "real" list.
            if (current.data != null) {
                boolean found = false;
                Node<E> temp = dummy.next;
                while (temp != dummy) {
                    if (temp == current) {
                        found = true;
                        break;
                    }
                    temp = temp.next;
                }
                if (!found) 
                    return report("Current pointer's data field is not null but not found in the real list");
            }
            
            // 3. The current pointer's node can reach the dummy by traversing "prev" links only.
            Node<E> slow = current;
            Node<E> fast = current.prev;
    	    while (fast != dummy) {
    	    	if (fast == null || fast == slow ) return report("cycle detected");
    	    	slow = slow.prev;
    	        fast = fast.prev;
    	    	if (fast == null) return report("cycle detected");
    	    	if(fast != dummy) fast = fast.prev;
    	    }
            // 4. Any node reached by "prev" links from the current pointer that has a non-null data
            // field is in the "real" list.
            Node<E> pointer = current.prev;
            while (pointer != dummy) {
                if (pointer.data != null) {
                    boolean found = false;
                    Node<E> temp = dummy.next;
                    while (temp != dummy) {
                        if (temp == pointer) {
                            found = true;
                            break;
                        }
                        temp = temp.next;
                    }
                    if (!found) 
                        return report("Node reached by 'prev' links from current pointer has a non-null data field but not found in the real list");
                }
                pointer = pointer.prev;
            }
            return true;
        }

        MyIterator() {
            current = dummy;
        	assert wellFormed(): "invariant in constructor of MyIterator";

        }
        
		MyIterator(boolean ignored) {} // do not change this constructor
		
		private void moveToValid() {
            if (current.data != null) {
                boolean found = false;
                Node<E> temp = dummy.next;
                while (temp != dummy) {
                    if (temp == current) {
                        found = true;
                        break;
                    }
                    temp = temp.next;
                }
                if (!found) {
                	current = current.prev;
                }
            }
		}

        @Override // required
        public boolean hasNext() {
        	moveToValid();
        	assert wellFormed(): "invariant in hasNext";
            return current.next != dummy;
        }
        
        @Override // required
        public E next() {
        	moveToValid();
        	assert wellFormed(): "invariant in next";
            if (!hasNext())
                throw new NoSuchElementException();
            E element = current.next.data;
            current = current.next;
        	assert wellFormed(): "invariant by next";
            return element;
        }
        
        @Override
        public void remove() {
        	moveToValid();
            assert wellFormed() : "Invariant broken in remove";
            if (current == dummy || current.data == null) {
                throw new IllegalStateException("Cannot call remove before next()");
            }
            Node<E> prevNode = current.prev; // Store the previous node before removal
            current.prev.next = current.next;
            current.next.prev = current.prev;
            current = prevNode;
            size--;
            assert wellFormed() : "Invariant broken by remove";
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
		 * @param c the current node
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

