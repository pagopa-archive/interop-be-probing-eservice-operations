package it.pagopa.interop.probing.eservice.operations.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EserviceStatus {

  KO("KO"),

  OK("OK");

  private String value;

  EserviceStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static EserviceStatus fromValue(String value) {
    for (EserviceStatus b : EserviceStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
