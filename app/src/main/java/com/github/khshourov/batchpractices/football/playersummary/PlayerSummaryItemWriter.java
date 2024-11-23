package com.github.khshourov.batchpractices.football.playersummary;

import javax.sql.DataSource;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class PlayerSummaryItemWriter implements ItemWriter<PlayerSummary> {
  private static final String INSERT_SUMMARY =
      "INSERT into PLAYER_SUMMARY(ID, YEAR_NO, COMPLETES, ATTEMPTS, PASSING_YARDS, PASSING_TD, "
          + "INTERCEPTIONS, RUSHES, RUSH_YARDS, RECEPTIONS, RECEPTIONS_YARDS, TOTAL_TD) "
          + "values(:id, :year, :completes, :attempts, :passingYards, :passingTd, "
          + ":interceptions, :rushes, :rushYards, :receptions, :receptionYards, :totalTd)";

  private NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  public void write(Chunk<? extends PlayerSummary> chunk) throws Exception {
    for (PlayerSummary playerSummary : chunk) {
      SqlParameterSource source =
          new MapSqlParameterSource()
              .addValue("id", playerSummary.getId())
              .addValue("year", playerSummary.getYear())
              .addValue("completes", playerSummary.getCompletes())
              .addValue("attempts", playerSummary.getAttempts())
              .addValue("passingYards", playerSummary.getPassingYards())
              .addValue("passingTd", playerSummary.getPassingTd())
              .addValue("interceptions", playerSummary.getInterceptions())
              .addValue("rushes", playerSummary.getRushes())
              .addValue("rushYards", playerSummary.getRushYards())
              .addValue("receptions", playerSummary.getReceptions())
              .addValue("receptionYards", playerSummary.getReceptionYards())
              .addValue("totalTd", playerSummary.getTotalTd());

      this.jdbcTemplate.update(INSERT_SUMMARY, source);
    }
  }

  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }
}
