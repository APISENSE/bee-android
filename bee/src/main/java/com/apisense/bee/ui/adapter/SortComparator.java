package com.apisense.bee.ui.adapter;

import java.util.Comparator;

/**
 * Define the way of sorting elements of type T.
 *
 * @param <T> The type to sort.
 */
public interface SortComparator<T> {
    /**
     * Generate a comparator for the given type parameter.
     *
     * @return The {@link Comparator}.
     */
    Comparator<T> getComparator();
}
