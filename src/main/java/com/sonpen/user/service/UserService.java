package com.sonpen.user.service;

import com.sonpen.user.dao.UserDao;
import com.sonpen.user.domain.Level;
import com.sonpen.user.domain.User;

import java.util.List;

/**
 * Created by 1109806 on 2019-05-24.
 */
public class UserService {

    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for(User user : users) {
            Boolean changed = false;
            if( user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
                user.setLevel(Level.SILVER);
                changed = true;
            } else if( user.getLevel() == Level.SILVER && user.getRecommend() >= 30 ) {
                user.setLevel(Level.GOLD);
                changed = true;
            } else if( user.getLevel() == Level.GOLD ) {
                changed = false;
            } else {
                changed = false;
            }

            if(changed == true) { userDao.update(user); }
        }
    }

    public void add(User user) {
        if( user.getLevel() == null ) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
