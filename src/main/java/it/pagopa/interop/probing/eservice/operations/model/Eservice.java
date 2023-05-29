package it.pagopa.interop.probing.eservice.operations.model;

import java.io.Serializable;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.Version;
import it.pagopa.interop.probing.eservice.operations.annotations.ValidateStringArraySize;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * The persistent class for the eservices database table.
 *
 */
@Entity
@Table(name = "eservices",
    uniqueConstraints = @UniqueConstraint(columnNames = {"eservice_id", "version_id"}))
@TypeDef(name = "basePathType", typeClass = CustomStringArrayType.class)
@TypeDef(name = "audienceType", typeClass = CustomStringArrayType.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true, fluent = true)
public class Eservice implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eservice_sequence")
  @SequenceGenerator(name = "eservice_sequence", sequenceName = "eservice_sequence",
      allocationSize = 1)
  @Column(name = "id", updatable = false)
  private Long eserviceRecordId;

  @NotNull(message = "must not be null")
  @ValidateStringArraySize(maxSize = 2048)
  @Basic(optional = false)
  @Column(name = "base_path", columnDefinition = "varchar(2048) array")
  @Type(type = "basePathType")
  private String[] basePath;

  @NotBlank(message = "must not be blank")
  @Size(max = 255, message = "must not be longer than 255 chars")
  @Column(name = "eservice_name")
  private String eserviceName;

  @NotNull(message = "must not be null")
  @Column(name = "eservice_technology")
  @Enumerated(EnumType.STRING)
  private EserviceTechnology technology;

  @NotNull(message = "must not be null")
  @Column(name = "eservice_id")
  private UUID eserviceId;

  @NotNull(message = "must not be null")
  @Column(name = "polling_end_time")
  @Builder.Default
  private OffsetTime pollingEndTime = OffsetTime.of(23, 59, 0, 0, ZoneOffset.UTC);

  @NotNull(message = "must not be null")
  @Min(value = 1, message = "must be at least 1")
  @Column(name = "polling_frequency")
  @Builder.Default
  private Integer pollingFrequency = 5;

  @NotNull(message = "must not be null")
  @Column(name = "polling_start_time", columnDefinition = "TIME with time zone")
  @Builder.Default
  private OffsetTime pollingStartTime = OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC);

  @NotNull(message = "must not be null")
  @Column(name = "probing_enabled")
  @Builder.Default
  private boolean probingEnabled = true;

  @NotBlank(message = "must not be blank")
  @Size(max = 255, message = "must not be longer than 255 chars")
  @Column(name = "producer_name")
  private String producerName;

  @NotNull(message = "must not be null")
  @Enumerated(EnumType.STRING)
  @Column(name = "state")
  private EserviceInteropState state;


  @NotNull(message = "must not be null")
  @Column(name = "version_id")
  private UUID versionId;

  @Version
  @Column(name = "lock_version")
  private Integer lockVersion;

  @NotNull(message = "must not be null")
  @Min(value = 1, message = "must be at least 1")
  @Column(name = "version_number")
  private Integer versionNumber;

  @NotNull(message = "must not be null")
  @ValidateStringArraySize(maxSize = 2048)
  @Basic(optional = false)
  @Column(name = "audience", columnDefinition = "varchar(2048) array")
  @Type(type = "audienceType")
  private String[] audience;

}
