/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Shane
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.utility;

public interface PriorityQueue {
    /**
     * Insert into the queue maintaining the sort order
     *
     * @param comp the item to insert
     */
    void add(Comparable<?> comp);

    /**
     * Removes the smallest item from the queue
     *
     * @return the item removed
     */
    Comparable<?> pop();

    /**
     * Retrieves the smallest item from the queue
     *
     * @return the item found
     */
    Comparable<?> pull();

    /**
     * @return {@code true} if the queue is empty, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Clears the queue
     */
    void clear();

    /**
     * @return the size of the queue
     */
    int size();

    boolean contains(Comparable<?> comp);
}
