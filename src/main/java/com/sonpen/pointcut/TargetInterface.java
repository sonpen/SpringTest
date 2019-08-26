package com.sonpen.pointcut;

/**
 * Created by 1109806 on 2019-08-21.
 */
public interface TargetInterface {
    public void hello();
    public void hello(String a);
    public int minus(int a, int b) throws RuntimeException;
    public int plus(int a, int b);
}
