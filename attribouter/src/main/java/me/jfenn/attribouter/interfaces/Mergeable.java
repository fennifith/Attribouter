package me.jfenn.attribouter.interfaces;

public interface Mergeable<T> {

    T merge(T mergee);
    boolean hasAll();
    boolean isHidden();

}
