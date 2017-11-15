package com.apisense.bee.ui.adapter;

import java.util.Comparator;

import io.apisense.sdk.core.store.Crop;

public enum CropField implements SortComparator<Crop> {
    NAME(new Comparator<Crop>() {
        @Override
        public int compare(Crop o1, Crop o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }),
    AUTHOR(new Comparator<Crop>() {
        @Override
        public int compare(Crop o1, Crop o2) {
            return o1.getOwner().toLowerCase().compareTo(o2.getOwner().toLowerCase());
        }
    });

    private final Comparator<Crop> comparator;

    CropField(Comparator<Crop> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Crop> getComparator() {
        return this.comparator;
    }
}
