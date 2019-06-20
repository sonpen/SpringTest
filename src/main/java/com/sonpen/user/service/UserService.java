package com.sonpen.user.service;

import com.sonpen.user.domain.User;

/**
 * Created by 1109806 on 2019-06-20.
 */
public interface UserService {
    void add(User user);
    void upgradeLevels();
}
