package com.skytix.mconsul.utils;

/**
 * Created by marc on 13/11/2016.
 */
public class ValueHolder<T> {
    private T mValue;

    public ValueHolder(T aValue) {
        mValue = aValue;
    }

    public synchronized T getValue() {
        return mValue;
    }

    public synchronized void setValue(T aValue) {
        mValue = aValue;
    }

}
