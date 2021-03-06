package com.sonpen.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by 1109806 on 2019-07-11.
 */
public class UppercaseHandler implements InvocationHandler{
    Object target;

    public UppercaseHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = method.invoke(target, args);
        if( ret instanceof String && method.getName().startsWith("say")) {
            return ((String)ret).toUpperCase();
        }
        return ret;
    }
}
