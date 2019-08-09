package com.sonpen.proxy;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.util.PatternMatchUtils;

/**
 * Created by 1109806 on 2019-08-08.
 */
public class NameMatchClassMethodPointcut extends NameMatchMethodPointcut {
    public void setMappedClassName(String mappedClassName) {
        this.setClassFilter(new SimpleClassFilter(mappedClassName));        // 모든 클래스를 다 허용하던 디폴트 클래스 필터를 프로퍼티로 받은 클래스 이름을 이용해서 필터를 만들어 덮어씌운다.
    }

    static class SimpleClassFilter implements ClassFilter {
        String mappedName;
        private SimpleClassFilter(String mappedName) {
            this.mappedName = mappedName;
        }

        @Override
        public boolean matches(Class<?> aClass) {
            return PatternMatchUtils.simpleMatch(mappedName, aClass.getSimpleName());      // 와일드카드(*)가 들어간 문자열 비교를 지원하는 스프링의 유틸리티 메소드다. name*, *name, *name* 세가지 방식을 모두 지원한다.
        }
    }
}
