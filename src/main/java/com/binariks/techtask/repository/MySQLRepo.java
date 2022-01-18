package com.binariks.techtask.repository;

import com.binariks.techtask.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class MySQLRepo {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/techtask";
    private static final String DB_USER = "root";
    @Value("${db.password}")
    private String DB_PASS;

    public void saveAll(Iterable<User> userSet) {
        String query = "INSERT INTO users (name, value) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            userSet.forEach(user -> {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, user.getName());
                    statement.setInt(2, user.getValue());
                    statement.executeUpdate();
                } catch (SQLException exception) {
                    throw new RuntimeException(exception);
                }
            });
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}