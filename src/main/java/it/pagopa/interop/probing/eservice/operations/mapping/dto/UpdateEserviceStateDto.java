package it.pagopa.interop.probing.eservice.operations.mapping.dto;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class UpdateEserviceStateDto {

  @NotNull(message = "must not be null")
  @JsonProperty("eserviceId")
  private UUID eserviceId;

  @NotNull(message = "must not be null")
  @JsonProperty("versionId")
  private UUID versionId;

  @NotNull(message = "must not be null")
  @JsonProperty("eServiceState")
  private EserviceInteropState newEServiceState;
}
