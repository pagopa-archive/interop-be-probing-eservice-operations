package it.pagopa.interop.probing.eservice.operations.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "eservice_probing_responses")
@Data
@NoArgsConstructor
public class EserviceProbingResponse implements Serializable {

  @Id
  private long id;

  @Column(name = "response_received", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  @NotNull
  private OffsetDateTime responseReceived;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  private Eservice eservice;

}
