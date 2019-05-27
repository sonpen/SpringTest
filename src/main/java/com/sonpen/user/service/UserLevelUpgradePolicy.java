package com.sonpen.user.service;

import com.sonpen.user.domain.User;

/**
 * Created by 1109806 on 2019-05-27.
 */
public interface UserLevelUpgradePolicy {
    boolean canUpgradeLevel(User user);
    void upgradeLevel(User user);
}
