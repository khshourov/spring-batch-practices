package com.github.khshourov.batchpractices.football.game;

import javax.annotation.Nullable;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class GameFieldSetMapper implements FieldSetMapper<Game> {
  @Override
  @Nullable public Game mapFieldSet(@Nullable FieldSet fieldSet) throws BindException {
    if (fieldSet == null) {
      return null;
    }

    Game game = new Game();
    game.setId(fieldSet.readString("id"));
    game.setYear(fieldSet.readInt("year"));
    game.setTeam(fieldSet.readString("team"));
    game.setWeek(fieldSet.readInt("week"));
    game.setOpponent(fieldSet.readString("opponent"));
    game.setCompletes(fieldSet.readInt("completes"));
    game.setAttempts(fieldSet.readInt("attempts"));
    game.setPassingYards(fieldSet.readInt("passingYards"));
    game.setPassingTd(fieldSet.readInt("passingTd"));
    game.setInterceptions(fieldSet.readInt("interceptions"));
    game.setRushes(fieldSet.readInt("rushes"));
    game.setRushYards(fieldSet.readInt("rushYards"));
    game.setReceptions(fieldSet.readInt("receptions", 0));
    game.setReceptionYards(fieldSet.readInt("receptionYards"));
    game.setTotalTd(fieldSet.readInt("totalTd"));

    return game;
  }
}
