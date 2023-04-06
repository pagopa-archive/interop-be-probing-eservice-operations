package it.pagopa.interop.probing.eservice.operations.mapping.dto;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateEserviceStateDto {

	@NotNull
	@JsonProperty("eserviceId")
	private UUID eserviceId;

	@NotNull
	@JsonProperty("versionId")
	private UUID versionId;

	@NotNull
	@JsonProperty("eServiceState")
	private EserviceState newEServiceState;
}
