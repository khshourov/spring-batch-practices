package com.github.khshourov.batchpractices.tablepartition;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class ColumnRangePartitioner implements Partitioner {
  private JdbcTemplate jdbcTemplate;

  @Override
  public Map<String, ExecutionContext> partition(int gridSize) {
    int min = this.jdbcTemplate.queryForObject("SELECT MIN(id) FROM customer", Integer.class);
    int max = this.jdbcTemplate.queryForObject("SELECT MAX(id) FROM customer", Integer.class);
    int targetSize = (max - min) / gridSize + 1;

    Map<String, ExecutionContext> result = new HashMap<>();

    int partitionNumber = 0;
    int start = min;
    int end = start + targetSize - 1;
    while (start <= max) {
      ExecutionContext executionContext = new ExecutionContext();
      end = Math.min(end, max);
      executionContext.put("minId", start);
      executionContext.put("maxId", end);

      result.put("partition" + partitionNumber, executionContext);

      start = start + targetSize;
      end = end + targetSize;
      partitionNumber = partitionNumber + 1;
    }

    return result;
  }

  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }
}
