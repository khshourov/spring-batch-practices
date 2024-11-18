package com.github.khshourov.batchpractices.customfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

public class CustomFilterJobTest {
  private static final String GET_CUSTOMERS = "select NAME, CREDIT from CUSTOMER order by NAME";

  private List<Customer> customers;

  private int activeRow = 0;

  private ApplicationContext applicationContext;

  private JdbcTemplate jdbcTemplate;

  private final Map<String, Double> credits = new HashMap<>();

  @BeforeEach
  void onSetUp() {
    applicationContext = new AnnotationConfigApplicationContext(CustomFilterJob.class);
    jdbcTemplate = new JdbcTemplate(applicationContext.getBean(DataSource.class));

    JdbcTestUtils.deleteFromTables(jdbcTemplate, "TRADE");
    JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "CUSTOMER", "ID > 4");
    jdbcTemplate.update("update CUSTOMER set credit=100000");

    List<Map<String, Object>> list = jdbcTemplate.queryForList("select name, CREDIT from CUSTOMER");

    for (Map<String, Object> map : list) {
      credits.put((String) map.get("NAME"), ((Number) map.get("CREDIT")).doubleValue());
    }
  }

  @AfterEach
  void tearDown() {
    JdbcTestUtils.deleteFromTables(jdbcTemplate, "TRADE");
    JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "CUSTOMER", "ID > 4");
  }

  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  @Test
  void testFilterJob() throws Exception {
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);

    JobExecution jobExecution = jobLauncher.run(job, new JobParameters());

    customers =
        Arrays.asList(
            new Customer("customer1", (credits.get("customer1"))),
            new Customer("customer2", (credits.get("customer2"))),
            new Customer("customer3", 100500),
            new Customer("customer4", credits.get("customer4")),
            new Customer("customer5", 32345),
            new Customer("customer6", 123456));

    activeRow = 0;
    jdbcTemplate.query(
        GET_CUSTOMERS,
        rs -> {
          Customer customer = customers.get(activeRow++);
          assertEquals(customer.name(), rs.getString(1));
          assertEquals(customer.credit(), rs.getDouble(2), .01);
        });

    Map<String, Object> step1Execution = this.getStepExecution(jobExecution, "customFilterStep");
    assertEquals("4", step1Execution.get("READ_COUNT").toString());
    assertEquals("1", step1Execution.get("FILTER_COUNT").toString());
    assertEquals("3", step1Execution.get("WRITE_COUNT").toString());
  }

  private Map<String, Object> getStepExecution(JobExecution jobExecution, String stepName) {
    Long jobExecutionId = jobExecution.getId();
    return jdbcTemplate.queryForMap(
        "SELECT * from BATCH_STEP_EXECUTION where JOB_EXECUTION_ID = ? and STEP_NAME = ?",
        jobExecutionId,
        stepName);
  }
}
