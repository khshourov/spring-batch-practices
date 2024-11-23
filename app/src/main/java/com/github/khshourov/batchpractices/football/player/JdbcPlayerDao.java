package com.github.khshourov.batchpractices.football.player;

import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcPlayerDao implements PlayerDao {
  public static final String INSERT_PLAYER =
      "INSERT into PLAYERS (player_id, last_name, first_name, pos, year_of_birth, year_drafted)"
          + " values (:id, :lastName, :firstName, :position, :birthYear, :debutYear)";
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  public void savePlayer(Player player) {
    this.jdbcTemplate.update(INSERT_PLAYER, new BeanPropertySqlParameterSource(player));
  }

  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }
}
