package com.sonpen.user.service;

import com.sonpen.user.dao.UserDao;
import com.sonpen.user.domain.Level;
import com.sonpen.user.domain.User;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by 1109806 on 2019-05-24.
 */
public class UserService {

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    private UserDao userDao;
    private DataSource dataSource;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

//    public void upgradeLevels() {
//        List<User> users = userDao.getAll();
//        for(User user : users) {
//            Boolean changed = false;
//            if( user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
//                user.setLevel(Level.SILVER);
//                changed = true;
//            } else if( user.getLevel() == Level.SILVER && user.getRecommend() >= 30 ) {
//                user.setLevel(Level.GOLD);
//                changed = true;
//            } else if( user.getLevel() == Level.GOLD ) {
//                changed = false;
//            } else {
//                changed = false;
//            }
//
//            if(changed == true) { userDao.update(user); }
//        }
//    }

    /**
     * upgradeLevels() 리펙토링
     */
    public void upgradeLevels() throws Exception {
        TransactionSynchronizationManager.initSynchronization();        // 트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화 한다.
        Connection c = DataSourceUtils.getConnection(dataSource);       // DB커넥션을 생성하고 트랜잭션을 시작한다. 이후의 DAO 작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.
        c.setAutoCommit(false);

        try {
            List<User> users = userDao.getAll();
            for(User user : users) {
                if(canUpgradeLevel(user)){
                    upgradeLevel(user);
                }
            }
            c.commit();
        } catch (Exception e) {
            c.rollback();
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(c, dataSource);       // 스프링 유틸리티 메소드를 이용해 DB 커넥션을 안전하게 닫는다.
            TransactionSynchronizationManager.unbindResource(this.dataSource);      // 동기화 작업 종료 및 정리
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch(currentLevel) {
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }
    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }

    public void add(User user) {
        if( user.getLevel() == null ) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
