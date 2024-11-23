package com.github.khshourov.batchpractices.football.playersummary;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PlayerSummaryRowMapper implements RowMapper<PlayerSummary> {
  @Override
  public PlayerSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
    PlayerSummary summary = new PlayerSummary();

    summary.setId(rs.getString(1));
    summary.setYear(rs.getInt(2));
    summary.setCompletes(rs.getInt(3));
    summary.setAttempts(rs.getInt(4));
    summary.setPassingYards(rs.getInt(5));
    summary.setPassingTd(rs.getInt(6));
    summary.setInterceptions(rs.getInt(7));
    summary.setRushes(rs.getInt(8));
    summary.setRushYards(rs.getInt(9));
    summary.setReceptions(rs.getInt(10));
    summary.setReceptionYards(rs.getInt(11));
    summary.setTotalTd(rs.getInt(12));

    return summary;
  }
}
