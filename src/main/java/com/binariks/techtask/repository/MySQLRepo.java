package com.binariks.techtask.repository;

import com.binariks.techtask.User;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class MySQLRepo {

    private final Connection connection;

    public MySQLRepo(Connection connection) {
        this.connection = connection;
    }

    public void saveAll(Iterable<User> userSet) {
        String query = "INSERT INTO users (name, value) VALUES (?, ?)";
        userSet.forEach(user -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, user.getName());
                statement.setInt(2, user.getValue());
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }


}