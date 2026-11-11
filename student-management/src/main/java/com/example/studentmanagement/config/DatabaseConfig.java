package com.example.studentmanagement.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Creates the DataSource bean. Credentials come from application.properties,
 * which in turn reads environment variables (nothing secret is committed).
 */
@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource(@Value("${app.db.url}") String url,
                                 @Value("${app.db.username}") String username,
                                 @Value("${app.db.password}") String password) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }
}
