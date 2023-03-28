package it.pagopa.interop.probing.eservice.operations.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EserviceTechnology {

	REST("REST"),

	SOAP("SOAP");

	private String value;

	EserviceTechnology(String value) {
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
	public static EserviceTechnology fromValue(String value) {
		for (EserviceTechnology b : EserviceTechnology.values()) {
			if (b.value.equals(value)) {
				return b;
			}
		}
		throw new IllegalArgumentException("Unexpected value '" + value + "'");
	}
}
