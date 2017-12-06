package com.apisense.bee.ui.adapter.sort;

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

    /**
     * Define the type of this comparator. The types will define adapted drawables.
     *
     * @return The {@link ComparatorType} for this comparator.
     */
    ComparatorType getType();
}
