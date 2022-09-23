package com.app.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBControl {
    public static boolean addUser(Statement stm, String login, String password) throws SQLException {
        String checkLogin = String.format("SELECT count(*) from users where login='%s'", login);
        ResultSet rs = stm.executeQuery(checkLogin);
        rs.next();

        if (rs.getInt(1) == 0) {
            String update = String.format("INSERT INTO users (login, password) values ('%s', '%s')", login, password);
            return stm.executeUpdate(update) == 1;
        }
        else
            return false; // login already exists
    }

    public static boolean logUser(Statement stm, String login, String password) throws SQLException {
        String query = "SELECT * from users where login = '%s'".formatted(login);
        ResultSet rs = stm.executeQuery(query);
        while (rs.next()) {
            if (rs.getString("password").equals(password))
                return true;
        }

        return false;
    }
}
