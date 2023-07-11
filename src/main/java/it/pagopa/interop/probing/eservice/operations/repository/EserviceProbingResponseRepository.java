package it.pagopa.interop.probing.eservice.operations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.pagopa.interop.probing.eservice.operations.model.EserviceProbingResponse;

@Repository
public interface EserviceProbingResponseRepository
    extends JpaRepository<EserviceProbingResponse, Long> {

}
