package it.pagopa.interop.probing.eservice.operations.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name="eservice_probing_responses")
@Data
@NoArgsConstructor
public class EserviceProbingResponse implements Serializable {

    @Id
    private long id;

    @Column(name="response_received", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @NotNull
    private OffsetDateTime responseReceived;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private Eservice eservice;
    
}
