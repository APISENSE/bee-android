package com.apisense.bee.ui.adapter.sort;

import java.util.Comparator;

import io.apisense.sdk.core.store.Crop;

/**
 * Defines the different crop fields that are available for a sort.
 */
public enum CropField implements SortComparator<Crop> {
    NAME(ComparatorType.ALPHABETICAL, new Comparator<Crop>() {
        @Override
        public int compare(Crop o1, Crop o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }),
    AUTHOR(ComparatorType.ALPHABETICAL, new Comparator<Crop>() {
        @Override
        public int compare(Crop o1, Crop o2) {
            return o1.getOwner().toLowerCase().compareTo(o2.getOwner().toLowerCase());
        }
    }),
    UPDATE(ComparatorType.NUMERICAL, new Comparator<Crop>() {
        @Override
        public int compare(Crop o1, Crop o2) {
            return o1.getStatistics().updatedAt.compareTo(o2.getStatistics().updatedAt);
        }
    }),
    SUBSCRIBERS(ComparatorType.NUMERICAL, new Comparator<Crop>() {
        @Override
        public int compare(Crop o1, Crop o2) {
            return Integer
                    .valueOf(o1.getStatistics().numberOfSubscribers)
                    .compareTo(o2.getStatistics().numberOfSubscribers);
        }
    });

    private final ComparatorType type;
    private final Comparator<Crop> comparator;

    CropField(ComparatorType comparatorType, Comparator<Crop> comparator) {
        this.type = comparatorType;
        this.comparator = comparator;

    }

    @Override
    public Comparator<Crop> getComparator() {
        return this.comparator;
    }

    @Override
    public ComparatorType getType() {
        return type;
    }
}
