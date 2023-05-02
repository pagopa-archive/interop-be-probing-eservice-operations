package it.pagopa.interop.probing.eservice.operations.mapping.dto;

import it.pagopa.interop.probing.eservice.operations.annotations.ValidateStringArraySize;
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class SaveEserviceDto {

  @NotBlank(message = "must not be blank")
  @Size(max = 255, message = "must not be longer than 255 chars")
  @JsonProperty("name")
  private String name;

  @NotBlank(message = "must not be blank")
  @Size(max = 255, message = "must not be longer than 255 chars")
  @JsonProperty("producerName")
  private String producerName;

  @NotNull(message = "must not be null")
  @ValidateStringArraySize(maxSize = 2048)
  @JsonProperty("basePath")
  private String[] basePath;

  @NotNull(message = "must not be null")
  @JsonProperty("technology")
  private EserviceTechnology technology;

  @NotNull(message = "must not be null")
  @JsonProperty("eserviceId")
  private UUID eserviceId;

  @NotNull(message = "must not be null")
  @JsonProperty("versionId")
  private UUID versionId;

  @NotNull(message = "must not be null")
  @JsonProperty("state")
  private EserviceInteropState state;

  @NotNull(message = "must not be null")
  @Min(value=1, message="must be at least 1")
  @JsonProperty("versionNumber")
  private Integer versionNumber;
}
