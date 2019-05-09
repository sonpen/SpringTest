package com.sonpen.user.dao;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by 1109806 on 2019-04-19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class JUnitTest {

    @Autowired
    ApplicationContext context;
    static ApplicationContext contextObject = null;

    static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();

    @Test
    public void test1() {
        MatcherAssert.assertThat(testObjects, not(hasItem(this)));
        testObjects.add(this);

        MatcherAssert.assertThat( contextObject == null || contextObject == this.context, is(true));
        contextObject = context;
    }
    @Test
    public void test2() {
        MatcherAssert.assertThat(testObjects, not(hasItem(this)));
        testObjects.add(this);

        assertTrue( contextObject == null || contextObject == this.context);
        contextObject = context;
    }
    @Test
    public void test3() {
        MatcherAssert.assertThat(testObjects, not(hasItem(this)));
        testObjects.add(this);

        MatcherAssert.assertThat( contextObject, either(is(nullValue())).or(is(this.context)));
        contextObject = context;
    }
}
