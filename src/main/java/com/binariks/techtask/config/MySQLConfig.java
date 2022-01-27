package com.binariks.techtask.config;


import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
public class MySQLConfig {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/techtask";
    private static final String DB_USER = "root";
    private Connection connection;
    @Value("${db.password}")
    private String DB_PASS;

    @Bean
    @SneakyThrows
    public Connection getMySQLConnection() {
        if (connection == null) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        }
        return connection;
    }

    @SneakyThrows
    @PreDestroy
    public void closeConnection() {
        connection.close();
    }
}
