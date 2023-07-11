package it.pagopa.interop.probing.eservice.operations.mapping.dto.impl;

import it.pagopa.interop.probing.eservice.operations.mapping.dto.Dto;
import java.time.OffsetTime;
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
public class UpdateEserviceFrequencyDto implements Dto {

  @NotNull(message = "must not be null")
  @JsonProperty("eserviceId")
  private UUID eserviceId;

  @NotNull(message = "must not be null")
  @JsonProperty("versionId")
  private UUID versionId;

  @NotNull(message = "must not be null")
  @JsonProperty("pollingEndTime")
  private OffsetTime newPollingEndTime;

  @NotNull(message = "must not be null")
  @JsonProperty("pollingFrequency")
  private Integer newPollingFrequency;

  @NotNull(message = "must not be null")
  @JsonProperty("pollingStartTime")
  private OffsetTime newPollingStartTime;
}
