package it.pagopa.interop.probing.eservice.operations.mapping.dto;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
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
