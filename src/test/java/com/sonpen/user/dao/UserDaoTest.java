package com.sonpen.user.dao;

import com.sonpen.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by 1109806 on 2019-04-10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserDaoTest {
    @Autowired
    private ApplicationContext context;
    @Autowired
    UserDao dao;

    @Autowired
    UserDao dao2;

    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() {
        // ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        //AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
        //ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        dao2 = context.getBean("userDao", UserDao.class);

        user1 = new User("id1", "이름1", "pwd1");
        user2 = new User("id2", "이름2", "pwd2");
        user3 = new User("id3", "이름3", "pwd3");

        // 테스트를 위한 수동 DI 적용
        //DataSource dataSource = new SingleConnectionDataSource("jdbc:h2:~/test", "sa", "sa", true);
        //dao.setDataSource(dataSource);
    }

    @Test
    public void testDI() {
        assertTrue(dao == dao2);
    }

    @Test
    public void add() throws Exception {

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        User userget1 = dao.get(user1.getId());
        assertThat(userget1.getName(), is(user1.getName()));

        User userget2 = dao.get(user2.getId());
        assertThat(userget2.getName(), is(user2.getName()));
    }

    @Test
    public void count() throws Exception {

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));
        dao.add(user2);
        assertThat(dao.getCount(), is(2));
        dao.add(user3);
        assertThat(dao.getCount(), is(3));
    }

    /**
     * 예외가 발생하는지 테스트
     * @throws SQLException
     */
    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws SQLException {
        dao.deleteAll();

        dao.get("unknown");
    }

    @Test
    public void getAll() throws SQLException {
        dao.deleteAll();
        assertThat(dao.getAll().size(), is(0));

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size(), is(3));
        checkSameUser(user1, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(2));
    }

    public void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
    }

}