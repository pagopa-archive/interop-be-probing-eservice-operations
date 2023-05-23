package it.pagopa.interop.probing.eservice.operations.mapping.dto.impl;

import it.pagopa.interop.probing.eservice.operations.mapping.dto.Dto;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateEserviceProbingStateDto implements Dto {

  @NotNull(message = "must not be null")
  @JsonProperty("eserviceId")
  private UUID eserviceId;

  @NotNull(message = "must not be null")
  @JsonProperty("versionId")
  private UUID versionId;

  @NotNull(message = "must not be null")
  private boolean probingEnabled;
}
