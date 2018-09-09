package com.sonpen.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by sonikju on 2018-09-09.
 */
public class CountingConnectionMaker implements ConnectionMaker {
    private int counter = 0;
    private ConnectionMaker realConnectionMaker;

    public CountingConnectionMaker(ConnectionMaker realConnectionMaker) {
        this.realConnectionMaker = realConnectionMaker;
    }

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        this.counter++;
        return realConnectionMaker.makeConnection();
    }

    public int getCounter() {
        return this.counter;
    }
}
