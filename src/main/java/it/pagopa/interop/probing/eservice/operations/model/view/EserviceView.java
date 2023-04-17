package it.pagopa.interop.probing.eservice.operations.model.view;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Immutable;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The persistent class for the eservices database table.
 *
 */
@Entity
@Immutable
@Table(name = "eservice_view")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EserviceView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", updatable = false)
  private Long id;

  @NotBlank
  @Size(max = 255)
  @Column(name = "eservice_name")
  private String eserviceName;

  @NotNull
  @Column(name = "eservice_id")
  private UUID eserviceId;

  @NotBlank
  @Size(max = 255)
  @Column(name = "producer_name")
  private String producerName;

  @NotNull
  @Column(name = "probing_enabled")
  private boolean probingEnabled;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "state")
  private EserviceInteropState state;

  @NotNull
  @Column(name = "version_id")
  private UUID versionId;

  @Column(name = "version_number")
  private Integer versionNumber;

  @Column(name = "response_received", columnDefinition = "timestamp with time zone")
  private OffsetDateTime responseReceived;

  @Column(name = "last_request", columnDefinition = "timestamp with time zone")
  private OffsetDateTime lastRequest;

  @Column(name = "polling_frequency")
  private Integer pollingFrequency;

}
