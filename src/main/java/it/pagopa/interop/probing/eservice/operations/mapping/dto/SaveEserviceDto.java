package it.pagopa.interop.probing.eservice.operations.mapping.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveEserviceDto {
	
	@NotBlank
	@JsonProperty("name")
	private String name;

	@NotBlank
	@JsonProperty("producerName")
	private String producerName;

	@NotEmpty
	@JsonProperty("basePath")
	private String[] basePath;

	@NotNull
	@JsonProperty("technology")
	private EserviceTechnology technology;

	@NotNull
	@JsonProperty("eserviceId")
	private String eserviceId;

	@NotNull
	@JsonProperty("versionId")
	private String versionId;

	@NotNull
	@JsonProperty("state")
	private EserviceState state;

	@NotBlank
	@JsonProperty("versionNumber")
	private String versionNumber;
}
