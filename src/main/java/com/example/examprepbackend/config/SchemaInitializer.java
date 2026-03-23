// Create a startup runner that ensures the `fail_count` column exists on the `users` table.
package com.example.examprepbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'fail_count'",
                    Integer.class);
            if (count == null || count == 0) {
                // MySQL supports ADD COLUMN IF NOT EXISTS only in newer versions; we already checked, so run plain ALTER
                jdbcTemplate.execute("ALTER TABLE users ADD COLUMN fail_count INT DEFAULT 0");
                System.out.println("SchemaInitializer: added column `fail_count` to `users` table.");
            } else {
                System.out.println("SchemaInitializer: column `fail_count` already exists.");
            }
        } catch (Exception e) {
            // Log and continue — failing to alter schema shouldn't prevent the app from starting.
            System.err.println("SchemaInitializer: failed to ensure fail_count column: " + e.getMessage());
        }
    }
}
