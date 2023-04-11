package it.pagopa.interop.probing.eservice.operations.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;

@Repository
public interface EserviceRepository extends JpaRepository<Eservice, Long> {
  Optional<Eservice> findByEserviceIdAndVersionId(@Param("eserviceId") UUID eserviceId,
      @Param("versionId") UUID versionId);

  @Query("SELECT DISTINCT new it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse(e.producerName, e.producerName) FROM Eservice e WHERE UPPER(e.producerName) LIKE %:producerName%")
  List<SearchProducerNameResponse> getEservicesProducers(@Param("producerName") String producerName,
      Pageable pageable);
}
