package com.sonpen.pointcut;

/**
 * Created by 1109806 on 2019-08-21.
 */
public class Target implements TargetInterface {
    @Override
    public void hello() {

    }

    @Override
    public void hello(String a) {

    }

    @Override
    public int minus(int a, int b) throws RuntimeException {
        return 0;
    }

    @Override
    public int plus(int a, int b) {
        return 0;
    }

    public void method() {

    }
}
