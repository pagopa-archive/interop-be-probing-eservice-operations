package it.pagopa.interop.probing.eservice.operations.mapping.dto.impl;

import it.pagopa.interop.probing.eservice.operations.mapping.dto.Dto;
import java.time.OffsetDateTime;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateEserviceLastRequestDto implements Dto {

  @NotNull(message = "must not be null")
  @JsonProperty("eserviceRecordId")
  private Long eserviceRecordId;

  @NotNull(message = "must not be null")
  @JsonProperty("lastRequest")
  private OffsetDateTime lastRequest;
}
