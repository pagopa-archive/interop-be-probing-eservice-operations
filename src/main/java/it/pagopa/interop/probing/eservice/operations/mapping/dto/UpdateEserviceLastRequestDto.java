package it.pagopa.interop.probing.eservice.operations.mapping.dto;

import java.time.OffsetDateTime;
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
public class UpdateEserviceLastRequestDto {

  @NotNull(message = "must not be null")
  @JsonProperty("eservices_record_id")
  private Long eservicesRecordId;

  @NotNull(message = "must not be null")
  @JsonProperty("lastRequest")
  private OffsetDateTime lastRequest;
}
