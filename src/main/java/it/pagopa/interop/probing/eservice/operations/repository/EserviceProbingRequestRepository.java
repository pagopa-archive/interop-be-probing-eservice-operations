package it.pagopa.interop.probing.eservice.operations.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import it.pagopa.interop.probing.eservice.operations.model.EserviceProbingRequest;

@Repository
public interface EserviceProbingRequestRepository
    extends JpaRepository<EserviceProbingRequest, Long> {
  Optional<EserviceProbingRequest> findById(@Param("eserviceRecordId") Long eserviceRecordId);
}
