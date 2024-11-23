package com.github.khshourov.batchpractices.football;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.football.game.Game;
import com.github.khshourov.batchpractices.football.game.GameFieldSetMapper;
import com.github.khshourov.batchpractices.football.game.GameItemWriter;
import com.github.khshourov.batchpractices.football.player.JdbcPlayerDao;
import com.github.khshourov.batchpractices.football.player.Player;
import com.github.khshourov.batchpractices.football.player.PlayerFieldSetMapper;
import com.github.khshourov.batchpractices.football.player.PlayerItemWriter;
import com.github.khshourov.batchpractices.football.playersummary.PlayerSummary;
import com.github.khshourov.batchpractices.football.playersummary.PlayerSummaryItemWriter;
import com.github.khshourov.batchpractices.football.playersummary.PlayerSummaryRowMapper;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class FootballJob {
  @Bean
  public Step playerLoad(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      FlatFileItemReader<Player> playerItemReader,
      PlayerItemWriter playerItemWriter) {
    return new StepBuilder("playerLoad", jobRepository)
        .<Player, Player>chunk(1, transactionManager)
        .reader(playerItemReader)
        .writer(playerItemWriter)
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<Player> playerItemReader(
      @Value("#{jobParameters['playerInputFile']}") Resource resource) {
    return new FlatFileItemReaderBuilder<Player>()
        .name("playerItemReader")
        .resource(resource)
        .delimited()
        .names("id", "lastName", "firstName", "position", "debutYear", "birthYear")
        .fieldSetMapper(new PlayerFieldSetMapper())
        .build();
  }

  @Bean
  public PlayerItemWriter playerItemWriter(DataSource dataSource) {
    JdbcPlayerDao playerDao = new JdbcPlayerDao();
    playerDao.setDataSource(dataSource);

    PlayerItemWriter itemWriter = new PlayerItemWriter();
    itemWriter.setPlayerDao(playerDao);
    return itemWriter;
  }

  @Bean
  public Step gameLoad(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      FlatFileItemReader<Game> gameItemReader,
      GameItemWriter gameItemWriter) {
    return new StepBuilder("gameLoad", jobRepository)
        .<Game, Game>chunk(1, transactionManager)
        .reader(gameItemReader)
        .writer(gameItemWriter)
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<Game> gameItemReader(
      @Value("#{jobParameters['gameInputFile']}") Resource resource) {
    return new FlatFileItemReaderBuilder<Game>()
        .name("gameItemReader")
        .resource(resource)
        .delimited()
        .names(
            "id",
            "year",
            "team",
            "week",
            "opponent",
            "completes",
            "attempts",
            "passingYards",
            "passingTd",
            "interceptions",
            "rushes",
            "rushYards",
            "receptions",
            "receptionYards",
            "totalTd")
        .fieldSetMapper(new GameFieldSetMapper())
        .build();
  }

  @Bean
  public GameItemWriter gameItemWriter(DataSource dataSource) {
    GameItemWriter itemWriter = new GameItemWriter();
    itemWriter.setDataSource(dataSource);
    return itemWriter;
  }

  @Bean
  public Step playerSummaryLoad(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      JdbcCursorItemReader<PlayerSummary> playerSummaryItemReader,
      PlayerSummaryItemWriter playerSummaryItemWriter) {
    return new StepBuilder("playerSummaryLoad", jobRepository)
        .<PlayerSummary, PlayerSummary>chunk(1, transactionManager)
        .reader(playerSummaryItemReader)
        .writer(playerSummaryItemWriter)
        .build();
  }

  @Bean
  public JdbcCursorItemReader<PlayerSummary> playerSummaryItemReader(DataSource dataSource) {
    String sql =
        """
SELECT GAMES.player_id, GAMES.year_no, SUM(COMPLETES),
SUM(ATTEMPTS), SUM(PASSING_YARDS), SUM(PASSING_TD),
SUM(INTERCEPTIONS), SUM(RUSHES), SUM(RUSH_YARDS),
SUM(RECEPTIONS), SUM(RECEPTIONS_YARDS), SUM(TOTAL_TD)
from GAMES, PLAYERS where PLAYERS.player_id =
GAMES.player_id group by GAMES.player_id, GAMES.year_no
				""";

    return new JdbcCursorItemReaderBuilder<PlayerSummary>()
        .name("playerSummaryItemReader")
        .ignoreWarnings(true)
        .sql(sql)
        .dataSource(dataSource)
        .rowMapper(new PlayerSummaryRowMapper())
        .build();
  }

  @Bean
  public PlayerSummaryItemWriter playerSummaryItemWriter(DataSource dataSource) {
    PlayerSummaryItemWriter playerSummaryItemWriter = new PlayerSummaryItemWriter();
    playerSummaryItemWriter.setDataSource(dataSource);
    return playerSummaryItemWriter;
  }

  @Bean
  public Job job(
      JobRepository jobRepository, Step playerLoad, Step gameLoad, Step playerSummaryLoad) {
    return new JobBuilder("footballJob", jobRepository)
        .start(playerLoad)
        .next(gameLoad)
        .next(playerSummaryLoad)
        .build();
  }
}
