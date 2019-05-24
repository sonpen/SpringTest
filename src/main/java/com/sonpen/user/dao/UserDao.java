package com.sonpen.user.dao;

import com.sonpen.user.domain.User;

import java.util.List;

/**
 * Created by 1109806 on 2019-05-21.
 */
public interface UserDao {
    void deleteAll();
    void delete(String id);
    void add(User user);
    User get(String id);
    int getCount();
    List<User> getAll();

    void update(User user);
}
