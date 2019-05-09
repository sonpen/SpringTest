package com.sonpen.user.domain;

/**
 * Created by sonikju on 2018-09-09.
 */
public class User {

    public User() {
    }

    public User(String id, String name, String password) {

        this.id = id;
        this.name = name;
        this.password = password;
    }

    String id;
    String name;
    String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
