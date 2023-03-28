package it.pagopa.interop.probing.eservice.operations.mapstruct.dto;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdateEserviceProbingStateDto {

	@NotNull
	@JsonProperty("eserviceId")
	private UUID eserviceId;

	@NotNull
	@JsonProperty("versionId")
	private UUID versionId;

	@NotNull
	private boolean probingEnabled;
}
