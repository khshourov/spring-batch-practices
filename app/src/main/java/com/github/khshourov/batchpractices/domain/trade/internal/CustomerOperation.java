package com.github.khshourov.batchpractices.domain.trade.internal;

import java.util.HashMap;
import java.util.Map;

public enum CustomerOperation {
  ADD('A'),
  UPDATE('U'),
  DELETE('D');

  private final char code;

  private static final Map<Character, CustomerOperation> CODE_MAP;

  CustomerOperation(char code) {
    this.code = code;
  }

  static {
    CODE_MAP = new HashMap<>();
    for (CustomerOperation operation : values()) {
      CODE_MAP.put(operation.getCode(), operation);
    }
  }

  public static CustomerOperation fromCode(char code) {
    if (CODE_MAP.containsKey(code)) {
      return CODE_MAP.get(code);
    } else {
      throw new IllegalArgumentException("Invalid code: [" + code + "]");
    }
  }

  public char getCode() {
    return code;
  }
}
