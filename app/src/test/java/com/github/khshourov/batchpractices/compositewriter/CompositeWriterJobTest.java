package com.github.khshourov.batchpractices.compositewriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.khshourov.batchpractices.domain.trade.Trade;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.test.jdbc.JdbcTestUtils;

class CompositeWriterJobTest {

  private static final String GET_TRADES =
      "SELECT isin, quantity, price, customer FROM TRADE order by isin";

  private static final String EXPECTED_OUTPUT_FILE =
      "Trade: [isin=UK21341EAH41,quantity=211,price=31.11,customer=customer1]"
          + "Trade: [isin=UK21341EAH42,quantity=212,price=32.11,customer=customer2]"
          + "Trade: [isin=UK21341EAH43,quantity=213,price=33.11,customer=customer3]"
          + "Trade: [isin=UK21341EAH44,quantity=214,price=34.11,customer=customer4]"
          + "Trade: [isin=UK21341EAH45,quantity=215,price=35.11,customer=customer5]";

  private JdbcTemplate jdbcTemplate;

  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  @Test
  void testJobLaunch() throws Exception {
    ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(CompositeWriterJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);
    jdbcTemplate = new JdbcTemplate(applicationContext.getBean(DataSource.class));

    JdbcTestUtils.deleteFromTables(jdbcTemplate, "TRADE");
    int before = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TRADE");

    JobExecution jobExecution = jobLauncher.run(job, new JobParameters());

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    assertFile("build/test-output/report1.txt");
    assertFile("build/test-output/report2.txt");
    assertTable(before);
  }

  private void assertTable(int before) {
    final List<Trade> trades =
        new ArrayList<>() {
          {
            add(new Trade("UK21341EAH41", 211, new BigDecimal("31.11"), "customer1"));
            add(new Trade("UK21341EAH42", 212, new BigDecimal("32.11"), "customer2"));
            add(new Trade("UK21341EAH43", 213, new BigDecimal("33.11"), "customer3"));
            add(new Trade("UK21341EAH44", 214, new BigDecimal("34.11"), "customer4"));
            add(new Trade("UK21341EAH45", 215, new BigDecimal("35.11"), "customer5"));
          }
        };

    int after = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TRADE");

    assertEquals(before + 5, after);

    jdbcTemplate.query(
        GET_TRADES,
        new RowCallbackHandler() {
          private int activeRow = 0;

          @Override
          public void processRow(ResultSet rs) throws SQLException {
            Trade trade = trades.get(activeRow++);

            assertEquals(trade.getIsin(), rs.getString(1));
            assertEquals(trade.getQuantity(), rs.getLong(2));
            assertEquals(trade.getPrice(), rs.getBigDecimal(3));
            assertEquals(trade.getCustomer(), rs.getString(4));
          }
        });
  }

  private void assertFile(String fileName) throws IOException {
    List<String> outputLines = IOUtils.readLines(new FileInputStream(fileName), "UTF-8");

    StringBuilder output = new StringBuilder();
    for (String line : outputLines) {
      output.append(line);
    }

    assertEquals(EXPECTED_OUTPUT_FILE, output.toString());
  }
}
