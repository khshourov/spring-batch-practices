package com.github.khshourov.batchpractices.adapter.tasklet;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TaskletJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step1, Step step2) {
    return new JobBuilder("taskletJob", jobRepository).start(step1).next(step2).build();
  }

  @Bean
  public Step step1(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      TransactionAttribute transactionAttribute,
      MethodInvokingTaskletAdapter adapter) {
    return new StepBuilder("taskletJobStep1", jobRepository)
        .tasklet(adapter, transactionManager)
        .transactionAttribute(transactionAttribute)
        .build();
  }

  @Bean
  public Step step2(
      JobRepository jobRepository, JdbcTransactionManager transactionManager, Task task) {
    return new StepBuilder("taskletJobStep2", jobRepository)
        .tasklet(
            (contribution, chunkContext) -> {
              task.doWork(chunkContext);
              return RepeatStatus.FINISHED;
            },
            transactionManager)
        .build();
  }

  @Bean
  public MethodInvokingTaskletAdapter adapter(TestBean testBean) {
    MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();
    adapter.setTargetObject(testBean);
    adapter.setTargetMethod("execute");
    adapter.setArguments(new Object[] {"foo2", 3, 3.14});
    return adapter;
  }

  @Bean
  public TransactionAttribute transactionAttribute() {
    DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute();
    transactionAttribute.setPropagationBehaviorName("PROPAGATION_REQUIRED");
    return transactionAttribute;
  }

  @Bean
  @StepScope
  public TestBean testBean(@Value("#{jobParameters['value']}") String value) {
    TestBean testBean = new TestBean();
    testBean.setValue(value);
    return testBean;
  }

  @Bean
  public Task task() {
    return new Task();
  }
}
