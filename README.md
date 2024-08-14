## Overview
The goal is to implement a generic **RobustQueue** data structure using a circular doubly-linked list with a dummy node. The **RobustQueue** supports robust iterators, which are designed to remain functional even if elements are added or removed from the queue while the iterator is in use.

## Key Features
- **Robust Iterators**: Iterators that never go stale, handling concurrent modifications gracefully.
- **Circular Doubly-Linked List**: The queue is implemented using a circular doubly-linked list with a dummy node, ensuring that no null pointers are encountered during traversal.
- **Queue ADT Operations**: Implements standard Queue operations (`add`, `offer`, `remove`, `poll`, `element`, `peek`), with robust handling for edge cases.
- **No Null Elements**: Follows the common convention of disallowing null values in the queue.

## Data Structure Invariant
The **RobustQueue** maintains the following invariants:
1. The dummy node is never null.
2. The dummy node's data is always null.
3. Traversing the `next` links or `prev` links from the dummy node will always return to the dummy node (circular structure).
4. Every node in the list has consistent `next` and `prev` links.
5. No node other than the dummy node has null data.
6. The number of nodes in the queue matches the size field.

## Iterator Invariant
The iterator maintains the following invariants:
1. The current pointer is never null.
2. If the current pointer's data is not null, the node is part of the active list.
3. The current pointer's node can reach the dummy by traversing `prev` links only.
4. Any node reachable by `prev` links from the current pointer with non-null data is in the active list.

## Class Structure
- **RobustQueue**: The main class implementing the queue ADT using a circular doubly-linked list.
- **Node**: A static inner class representing each node in the list.
- **RobustQueueIterator**: A private nested class implementing a robust iterator.
- **Spy**: A static inner class for testing and debugging, adapted from the Spy class used in previous assignments.

## Testing
- Comprehensive unit tests are provided to ensure correctness and robustness of the **RobustQueue** implementation.
- The project includes:
  - Invariant tests to validate the integrity of the data structure.
  - Functional tests to ensure the correct behavior of queue operations and iterators.
  - Randomized tests for robustness.
  - Efficiency tests to evaluate the performance of the implementation.

## License
This project is licensed under the MIT License. You are free to use, modify, and distribute this code with attribution.

---

This README should provide a clear and professional overview of the project, making it suitable for showcasing to potential employers.
