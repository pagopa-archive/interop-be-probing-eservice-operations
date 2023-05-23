package it.pagopa.interop.probing.eservice.operations.mapping.dto.impl;

import it.pagopa.interop.probing.eservice.operations.mapping.dto.Dto;
import java.time.OffsetDateTime;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStatus;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UpdateEserviceResponseReceivedDto implements Dto {

  @NotNull(message = "must not be null")
  @JsonProperty("eserviceRecordId")
  private Long eserviceRecordId;

  @NotNull(message = "must not be null")
  @JsonProperty("responseReceived")
  private OffsetDateTime responseReceived;

  @NotNull(message = "must not be null")
  @JsonProperty("status")
  private EserviceStatus status;

}
