package com.sonpen.user.dao;

import com.sonpen.user.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sonikju on 2018-09-09.
 */
public class UserDao {
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    public void deleteAll() throws SQLException{
        this.jdbcTemplate.update("delete from users");
    }

    public void delete(String id) throws SQLException {
        this.jdbcTemplate.update("delete from users where id = ?", id);
    }

    public void add(User user) throws SQLException {
        this.jdbcTemplate.update("insert into users(id, name, password) values (?, ?, ?)",
                user.getId(), user.getName(), user.getPassword());
    }

    public int getCount() throws SQLException {
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }

    public User get(String id) throws SQLException {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id},
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        User user = new User();
                        user.setId(rs.getString("id"));
                        user.setName(rs.getString("name"));
                        user.setPassword(rs.getString("password"));
                        return user;
                    }
                });
    }

}
