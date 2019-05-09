package com.sonpen.template;

/**
 * Created by 1109806 on 2019-05-08.
 */
public interface LineCallback<T> {
    T doSomethingWithLine(String line, T value);
}
