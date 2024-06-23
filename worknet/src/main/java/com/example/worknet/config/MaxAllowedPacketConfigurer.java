package com.example.worknet.config;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MaxAllowedPacketConfigurer {


    // The purpose of this class is to allow packets of 50MBs for multiple file uploading
    // because MySQL has a limit which we surpass with our files in Post creation.
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void init() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("SET GLOBAL max_allowed_packet = 52428800");  // 50 MB max size, used for multiple file uploading.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}