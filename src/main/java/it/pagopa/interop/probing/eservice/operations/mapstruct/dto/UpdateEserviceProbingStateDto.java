package it.pagopa.interop.probing.eservice.operations.mapstruct.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

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
