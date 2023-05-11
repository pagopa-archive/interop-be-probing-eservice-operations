package it.pagopa.interop.probing.eservice.operations.model.view;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.model.CustomStringArrayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/*** The persistent class for the eservices database table. **/

@Entity
@Immutable
@Table(name = "eservice_view")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeDef(name = "basePathType", typeClass = CustomStringArrayType.class)
@Accessors(chain = true)
public class EserviceView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", updatable = false)
  private Long eserviceRecordId;

  @Size(max = 255)
  @Column(name = "eservice_name")
  private String eserviceName;

  @Column(name = "eservice_id")
  private UUID eserviceId;

  @Size(max = 255)
  @Column(name = "producer_name")
  private String producerName;

  @Column(name = "probing_enabled")
  private boolean probingEnabled;

  @Enumerated(EnumType.STRING)
  @Column(name = "state")
  private EserviceInteropState state;

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

  @Column(name = "polling_start_time")
  private OffsetTime pollingStartTime;

  @Column(name = "polling_end_time")
  private OffsetTime pollingEndTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "eservice_technology")
  private EserviceTechnology technology;

  @Column(name = "base_path", columnDefinition = "varchar(2048) array")
  @Type(type = "basePathType")
  private String[] basePath;

}
