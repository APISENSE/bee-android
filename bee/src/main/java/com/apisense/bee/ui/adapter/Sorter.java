package com.apisense.bee.ui.adapter;

/**
 * Sort the elements depending on a comparator.
 *
 * @param <T> The type to sort.
 */
public interface Sorter<T> {
    /**
     * Perform the sort with the given comparator.
     *
     * @param comparator The comparator to apply on data.
     */
    void sort(SortComparator<T> comparator);


    void reverseSort(SortComparator<T> comparator);
}
