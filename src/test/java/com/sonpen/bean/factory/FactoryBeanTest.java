package com.sonpen.bean.factory;

import com.sonpen.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by 1109806 on 2019-07-12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/FactoryBeanTest-context.xml")
public class FactoryBeanTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void getMessageFromFactoryBean() {
        Object message = context.getBean("message");
        //assertThat(message, is(Message.class));
        assertThat(((Message)message).getText(), is("Factory Bean"));
    }

}