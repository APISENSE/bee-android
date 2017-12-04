package com.apisense.bee.ui.adapter.sort;

import com.apisense.bee.R;

/**
 * Defines the default state and drawables of {@link SortComparator} depending on the type of sorted elements.
 */
public enum ComparatorType {
    ALPHABETICAL(R.drawable.ic_sort_az_white, R.drawable.ic_sort_za_white, R.drawable.ic_sort_az_unsorted_white, true),
    NUMERICAL(R.drawable.ic_sort_19_white, R.drawable.ic_sort_91_white, R.drawable.ic_sort_19_unsorted_white, false);

    public final int upDrawableId;
    public final int downDrawableId;
    public final int idleDrawableId;
    public final boolean isDefaultAscending;

    ComparatorType(int upDrawableId, int downDrawableId, int idleDrawableId, boolean isDefaultAscending) {
        this.upDrawableId = upDrawableId;
        this.downDrawableId = downDrawableId;
        this.idleDrawableId = idleDrawableId;
        this.isDefaultAscending = isDefaultAscending;
    }
}
