package it.pagopa.interop.probing.eservice.operations.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "eservice_probing_responses")
@Data
@NoArgsConstructor
public class EserviceProbingRequest implements Serializable {

	@Id
	private long id;

	@Column(name = "last_request", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@NotNull
	private OffsetDateTime lastRequest;

	@MapsId
	@OneToOne(fetch = FetchType.LAZY)
	private Eservice eservice;

}
