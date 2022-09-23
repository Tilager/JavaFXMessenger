package com.app;

import java.sql.*;

public class Test {
    static boolean checkLogin(String login) {
        return login.matches("^[\\dA-Za-z.-_]*$");
    }

    static boolean addUser(Statement stm, String login, String password) throws SQLException {
        String checkLogin = String.format("SELECT count(*) from users where login='%s'", login);
        ResultSet rs = stm.executeQuery(checkLogin);
        rs.next();

        if (rs.getInt(1) == 0) {
            String update = String.format("INSERT INTO users (login, password) values ('%s', '%s')", login, password);
            int res = stm.executeUpdate(update);
            return res == 1;
        }
        else
            return false;
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

    public static void main(String[] args) throws SQLException {
        try (Connection connection = DriverManager.getConnection(MainApplication.CFG.DATABASE_URL,
                                                                MainApplication.CFG.DATABASE_LOGIN,
                                                                MainApplication.CFG.DATABASE_PASSWORD)) {
            Statement stm = connection.createStatement();

            System.out.println(logUser(stm, "tamtface", "122342"));
        }
    }


}