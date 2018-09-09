package com.sonpen.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by sonikju on 2018-09-09.
 */
public class DConnectionMaker implements ConnectionMaker {

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        System.out.println("DConnectionMaker::makeConnection()");
        Class.forName("org.h2.Driver");
        return DriverManager.getConnection(
                "jdbc:h2:~/test", "sa", "");
    }
}
