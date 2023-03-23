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

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import it.pagopa.interop.probing.eservice.operations.util.EserviceTechnology;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The persistent class for the eservices database table.
 *
 */
@Entity
@Table(name = "eservices", uniqueConstraints = @UniqueConstraint(columnNames = { "eservice_id", "version_id" }))
@TypeDef(name = "basePathType", typeClass = CustomStringArrayType.class)
@Data
@NoArgsConstructor
public class Eservice implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eservice_sequence")
	@SequenceGenerator(name = "eservice_sequence", sequenceName = "eservice_sequence", allocationSize = 1)
	@Column(updatable = false)
	private Long id;

	@NotNull
	@Size(max = 255)
	@Basic(optional = false)
	@Column(name = "base_path", columnDefinition = "varchar(2048) array")
	@Type(type = "basePathType")
	private String[] basePath;

	@NotBlank
	@Size(max = 255)
	@Column(name = "eservice_name")
	private String eserviceName;

	@NotNull
	@Column(name = "eservice_technology")
	@Enumerated(EnumType.STRING)
	private EserviceTechnology technology;

	@NotNull
	@Column(name = "eservice_id")
	private UUID eserviceId;

	@NotNull
	@Column(name = "polling_end_time", columnDefinition = "TIME with time zone")
	private OffsetTime pollingEndTime = OffsetTime.of(23, 59, 0, 0, ZoneOffset.UTC);

	@NotNull
	@Min(1)
	@Column(name = "polling_frequency", columnDefinition = "integer default 5")
	private Integer pollingFrequency = 5;

	@NotNull
	@Column(name = "polling_start_time", columnDefinition = "TIME with time zone")
	private OffsetTime pollingStartTime = OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC);

	@NotNull
	@Column(name = "probing_enabled")
	private boolean probingEnabled;

	@NotBlank
	@Size(max = 255)
	@Column(name = "producer_name")
	private String producerName;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "state")
	private EserviceState state;

	@NotNull
	@Column(name = "version_id")
	private UUID versionId;
}
