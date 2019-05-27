package com.sonpen.user.service;

import com.sonpen.user.dao.UserDao;
import com.sonpen.user.domain.Level;
import com.sonpen.user.domain.User;
import com.sonpen.user.service.UserLevelUpgradePolicy;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sonpen.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.sonpen.user.service.UserService.MIN_RECOMMEND_FOR_GOLD;

/**
 * Created by 1109806 on 2019-05-27.
 */
public class UserLevelUpgradePolicyImpl implements UserLevelUpgradePolicy {
    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch(currentLevel) {
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }

    @Override
    public void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }
}
