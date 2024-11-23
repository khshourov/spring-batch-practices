package com.github.khshourov.batchpractices.lib;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;

public class SqlFileExecutor {

  private final JdbcTemplate jdbcTemplate;

  public SqlFileExecutor(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void executeSqlFile(String filePath) throws IOException {
    // Read the SQL file into a string
    String sql =
        FileCopyUtils.copyToString(
            new java.io.InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(filePath)),
                StandardCharsets.UTF_8));

    // Split the SQL by semicolons (assuming each statement ends with a semicolon)
    String[] sqlStatements = sql.split(";");

    // Execute each SQL statement
    for (String statement : sqlStatements) {
      if (!statement.trim().isEmpty()) { // Skip empty lines
        jdbcTemplate.execute(statement.trim());
      }
    }
  }
}
