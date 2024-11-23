package com.github.khshourov.batchpractices.football.player;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class PlayerItemWriter implements ItemWriter<Player> {
  private PlayerDao playerDao;

  @Override
  public void write(Chunk<? extends Player> chunk) throws Exception {
    for (Player player : chunk) {
      playerDao.savePlayer(player);
    }
  }

  public void setPlayerDao(PlayerDao playerDao) {
    this.playerDao = playerDao;
  }
}
