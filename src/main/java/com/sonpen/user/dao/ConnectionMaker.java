package com.sonpen.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by sonikju on 2018-09-09.
 */
public interface ConnectionMaker {
    public Connection makeConnection() throws ClassNotFoundException, SQLException;
}
