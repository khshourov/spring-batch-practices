package com.github.khshourov.batchpractices.football.player;

import javax.annotation.Nullable;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class PlayerFieldSetMapper implements FieldSetMapper<Player> {
  @Override
  @Nullable public Player mapFieldSet(@Nullable FieldSet fieldSet) throws BindException {
    if (fieldSet == null) {
      return null;
    }

    Player player = new Player();

    player.setId(fieldSet.readString("id"));
    player.setLastName(fieldSet.readString("lastName"));
    player.setFirstName(fieldSet.readString("firstName"));
    player.setPosition(fieldSet.readString("position"));
    player.setDebutYear(fieldSet.readInt("debutYear"));
    player.setBirthYear(fieldSet.readInt("birthYear"));

    return player;
  }
}
