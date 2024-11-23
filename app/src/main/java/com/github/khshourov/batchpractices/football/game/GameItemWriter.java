package com.github.khshourov.batchpractices.football.game;

import java.util.Objects;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class GameItemWriter extends JdbcDaoSupport implements ItemWriter<Game> {
  private SimpleJdbcInsert gameInsert;

  @Override
  public void initDao() throws Exception {
    super.initDao();
    this.gameInsert =
        new SimpleJdbcInsert(Objects.requireNonNull(getDataSource()))
            .withTableName("GAMES")
            .usingColumns(
                "player_id",
                "year_no",
                "team",
                "week",
                "opponent",
                " completes",
                "attempts",
                "passing_yards",
                "passing_td",
                "interceptions",
                "rushes",
                "rush_yards",
                "receptions",
                "receptions_yards",
                "total_td");
  }

  @Override
  public void write(Chunk<? extends Game> chunk) throws Exception {
    for (Game game : chunk) {
      SqlParameterSource values =
          new MapSqlParameterSource()
              .addValue("player_id", game.getId())
              .addValue("year_no", game.getYear())
              .addValue("team", game.getTeam())
              .addValue("week", game.getWeek())
              .addValue("opponent", game.getOpponent())
              .addValue("completes", game.getCompletes())
              .addValue("attempts", game.getAttempts())
              .addValue("passing_yards", game.getPassingYards())
              .addValue("passing_td", game.getPassingTd())
              .addValue("interceptions", game.getInterceptions())
              .addValue("rushes", game.getRushes())
              .addValue("rush_yards", game.getRushYards())
              .addValue("receptions", game.getReceptions())
              .addValue("receptions_yards", game.getReceptionYards())
              .addValue("total_td", game.getTotalTd());

      this.gameInsert.execute(values);
    }
  }
}
