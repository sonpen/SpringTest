package com.sonpen;

import com.sonpen.user.dao.CountingConnectionMaker;
import com.sonpen.user.dao.CountingDaoFactory;
import com.sonpen.user.dao.DaoFactory;
import com.sonpen.user.dao.UserDao;
import com.sonpen.user.domain.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

/**
 * Created by sonikju on 2018-09-09.
 */
//@SpringBootApplication
public class Application {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        //SpringApplication.run(Application.class, args);

//        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
//        UserDao dao = context.getBean("userDao", UserDao.class);
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(CountingDaoFactory.class);
        UserDao dao = context.getBean("userDao", UserDao.class);

        CountingConnectionMaker ccm = context.getBean("connectionMaker", CountingConnectionMaker.class);
        System.out.println("Before Connection Counter: " + ccm.getCounter());

        User user = new User();
        user.setId("sonpen");
        user.setName("sonikju");
        user.setPassword("1234");

        dao.add(user);

        System.out.println("등록성공!!!");

        User user2 = dao.get("sonpen");
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());
        System.out.println(user2.getId() + "조회 성공");

        dao.delete("sonpen");

        System.out.println("After Connection Counter: " + ccm.getCounter());
    }
}
