package com.sonpen.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by 1109806 on 2019-04-08.
 */
public interface StatementStrategy {

    PreparedStatement makePreparedStatement(Connection c) throws SQLException;
}
