package com.sonpen.user.service;

import com.sonpen.user.dao.UserDao;
import com.sonpen.user.domain.Level;
import com.sonpen.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import sun.java2d.pipe.SpanShapeRenderer;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sonpen.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.sonpen.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by 1109806 on 2019-05-24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {
    @Autowired
    DataSource dataSource;

    @Autowired
    UserDao userDao;
    @Autowired
    UserService userService;
    @Autowired
    UserServiceImpl userServiceImpl;
    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    MailSender mailSender;

    @Autowired
    ApplicationContext context;

    List<User> users;   // 테스트 픽스쳐

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "sonpen76@gmail.com"),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "sonpen76@gmail.com"),
                new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1, "sonpen76@gmail.com"),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "sonpen76@gmail.com"),
                new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "sonpen76@gmail.com")
        );
    }

    @Test
    public void upgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class);              // 목 오브젝트를 생성하고
        when(mockUserDao.getAll()).thenReturn(this.users);      // getAll() 이 호출되었을때 리턴값을 설정해 준 뒤
        userServiceImpl.setUserDao(mockUserDao);                // 테스트 대상에 DI

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        verify(mockUserDao, times(2)).update(any(User.class));      // update() 가 2번 호출 되었는지?
        verify(mockUserDao).update(users.get(1));                   // users.get(1) 로 업데이트가 호출되었는지?
        assertThat(users.get(1).getLevel(), is(Level.SILVER));      // 레벨 변화는 직접 확인이 안되므로...
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel(), is(Level.GOLD));

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);        // MailSender 목 오브젝트에 전달된 파라미터를 가져와서 내용을 검증
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId(), is(expectedId));
        assertThat(updated.getLevel(), is(expectedLevel));
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if( upgraded) {
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        }
        else {
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
    }

    static class TestUserServiceException extends RuntimeException {

    }
    static class TestUserService extends UserServiceImpl {
        private String id;

        private TestUserService(String id) {
            this.id = id;
        }
        public void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    @Test
    @DirtiesContext         // 컨텍스트 설정을 변경하기 때문에 여전히 필요하다.
    public void upgradeAllOrNothing() throws Exception {
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setMailSender(mailSender);

        ProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", ProxyFactoryBean.class);      // 팩토리 빈 자체를 가져와야 하므로 빈 이름에 &를 반드시 넣어야 한다.
        txProxyFactoryBean.setTarget(testUserService);
        UserService txUserService = (UserService)txProxyFactoryBean.getObject();    // FactoryBean 타입이므로 동일하게 getObject() 로 프록시를 가져온다.

        userDao.deleteAll();
        for(User user : users) userDao.add(user);
        try {
            txUserService.upgradeLevels();            // 업그레이드 작업 중에 예외가 발생해야한다. 정상종료라면 문제가 있으니 실패
            fail("TestUserServiceException expected");
        }
        catch(TestUserServiceException e) {     // 예외를 잡아서 계속 진행하도록한다. 다른 예외가 발생하면 테스트 실패

        }
        checkLevelUpgraded(users.get(1), false);    // 예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레빌이 처음 상태로 바뀌었나 확인
    }
    @Test
    public void upgradeAllOrNothingWithDynamicProxy() throws Exception {
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setMailSender(mailSender);

        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(testUserService);
        txHandler.setTransactionManager(this.transactionManager);
        txHandler.setPattern("upgradeLevels");

        UserService txUserService = (UserService)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{UserService.class}, txHandler);

        userDao.deleteAll();
        for(User user : users) userDao.add(user);
        try {
            txUserService.upgradeLevels();            // 업그레이드 작업 중에 예외가 발생해야한다. 정상종료라면 문제가 있으니 실패
            fail("TestUserServiceException expected");
        }
        catch(TestUserServiceException e) {     // 예외를 잡아서 계속 진행하도록한다. 다른 예외가 발생하면 테스트 실패

        }
        checkLevelUpgraded(users.get(1), false);    // 예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레빌이 처음 상태로 바뀌었나 확인
    }


    // 목 오브젝트로 만든 메일 전송 확인용 클래스
    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<String>();

        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage simpleMailMessage) throws MailException {
            requests.add(simpleMailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage... simpleMailMessages) throws MailException {

        }
    }
}