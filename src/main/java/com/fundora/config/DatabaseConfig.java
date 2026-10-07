package com.fundora.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Module III (JDBC & Enterprise Architecture):
 * Thread-Safe Singleton JDBC Connection Manager utilizing DriverManager.
 * Supports both standalone JDBC DAO routines and Spring managed pools.
 */
@Component
public class DatabaseConfig {

    private static volatile DatabaseConfig instance;
    private static final Object lock = new Object();

    @Value("${spring.datasource.url:jdbc:h2:mem:fundoradb;DB_CLOSE_DELAY=-1;MODE=MySQL}")
    private String dbUrl;

    @Value("${spring.datasource.username:sa}")
    private String dbUser;

    @Value("${spring.datasource.password:}")
    private String dbPassword;

    @Value("${spring.datasource.driverClassName:org.h2.Driver}")
    private String driverClassName;

    public DatabaseConfig() {
        // Initializes singleton instance
        instance = this;
    }

    /**
     * Singleton Instance accessor (Double-Checked Locking Pattern)
     */
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseConfig();
                    instance.dbUrl = "jdbc:h2:mem:fundoradb;DB_CLOSE_DELAY=-1;MODE=MySQL";
                    instance.dbUser = "sa";
                    instance.dbPassword = "";
                    instance.driverClassName = "org.h2.Driver";
                }
            }
        }
        return instance;
    }

    /**
     * Obtains a standard raw JDBC Connection using DriverManager
     * @return java.sql.Connection
     * @throws SQLException on database error
     */
    public Connection getConnection() throws SQLException {
        try {
            Class.forName(driverClassName != null ? driverClassName : "org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver class not found: " + driverClassName, e);
        }
        return DriverManager.getConnection(
            dbUrl != null ? dbUrl : "jdbc:h2:mem:fundoradb;DB_CLOSE_DELAY=-1;MODE=MySQL",
            dbUser != null ? dbUser : "sa",
            dbPassword != null ? dbPassword : ""
        );
    }
}
