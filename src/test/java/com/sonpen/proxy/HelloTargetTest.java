package com.sonpen.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.mockito.cglib.proxy.MethodProxy;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by 1109806 on 2019-07-09.
 */
public class HelloTargetTest {

    @Test
    public void simpleProxy() {
        Hello hello = new HelloTarget();

        assertThat(hello.sayHello("Toby"), is("Hello Toby"));
        assertThat(hello.sayHi("Toby"), is("Hi Toby"));
        assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));

        Hello proxiedHello = new HelloUppercase(new HelloTarget());
        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

    @Test
    public void dynamicProxy() {
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {Hello.class},
                new UppercaseHandler(new HelloTarget()));

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

    @Test
    public void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());        // 타깃 설정
        pfBean.addAdvice(new UppercaseAdvice());    // 부가기능을 담은 어드바이스를 추가한다. 여러개를 추가할 수도 있다.

        Hello proxiedHello = (Hello)pfBean.getObject();     // FactoryBean이므로 getObject()로 생성된 프록시를 가져온다.

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

    static class UppercaseAdvice implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            String ret = (String) methodInvocation.proceed();       // 리플렉션의 Method와 달리 메소드 실행 시 타깃 오브젝트를 전달할 필요가 없다.
                                                                    // MethodInvocation은 메소드 정보와 함께 타깃 오브젝트를 알고 있기 때문이다.
            return ret.toUpperCase();       // 부가기능 적용
        }
    }

    @Test
    public void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();       // 메소드 이름을 비교해서 대상을 선정하는 알고리즘을 제공하는 포인트 컷 생성
        pointcut.setMappedName("sayH*");        // 이름 비교조건 설정. sayH로 시작하는 모든 메소드를 선택하게 한다.

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));     // 포인트컷과 어드바이스를 Advisor로 묶어서 한번에 추가.

        Hello proxiedHello = (Hello)pfBean.getObject();

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));     // 메소드 이름이 포인트컷의 선정조건에 맞지 않으므로, 부가기능이적용되지 않는다.
    }

    @Test
    public void classNamePointcutAdvisor() {
        // 포인트컷 준비
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
            public ClassFilter getClassFilter() {   // 익명 내부 클래스 방식으로 클래스를 정의한다.
                return new ClassFilter() {
                    @Override
                    public boolean matches(Class<?> aClass) {
                        return aClass.getSimpleName().startsWith("HelloT");     // 클래스 이름이 HelloT로 시작하는 것만 선정한다.
                    }
                };
            }
        };

        classMethodPointcut.setMappedName("sayH*");     // sayH로 시작하는 메소드 이름을 가진 메소드만 선정한다.

        // 테스트
        checkAdviced(new HelloTarget(), classMethodPointcut, true);     // HelloTarget -> 적용 클래스 이다.

        class HelloWorld extends HelloTarget{};
        checkAdviced(new HelloWorld(), classMethodPointcut, false);     // HelloWorld -> 적용 클래스가 아니다.

        class HelloToby extends HelloTarget{};
        checkAdviced(new HelloToby(), classMethodPointcut, true);       // HelloToby -> 적용 클래스 이다.
    }

    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxiedHello = (Hello)pfBean.getObject();

        if( adviced ) {
            assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));        // 메소드 선정 방식을 통해 어드바이스 적용
            assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));              // 메소드 선정 방식을 통해 어드바이스 적용
            assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
        }
        else {
            assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
            assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
            assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
        }

    }
}