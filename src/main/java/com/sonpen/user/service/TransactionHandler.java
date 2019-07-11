package com.sonpen.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by 1109806 on 2019-07-11.
 */
public class TransactionHandler implements InvocationHandler {
    private Object target;      // 부가기능을 제공할 타깃 오브젝트. 어떤 타입의 오브젝트에도 적용 가능하다.
    private PlatformTransactionManager transactionManager;      // 트랜잭션 기능을 제공하는 데 필요한 트랜잭션 매니저
    private String pattern;     // 트랜잭션을 적용할 메소드 이름 패턴

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if( method.getName().startsWith(pattern))
            return invokeTranscation(method, args);
        return method.invoke(target, args);
    }

    private Object invokeTranscation(Method method, Object[] args) throws Throwable {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Object ret = method.invoke(target, args);
            this.transactionManager.commit(status);
            return ret;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            this.transactionManager.rollback(status);
            throw e.getTargetException();
        }
    }
}
