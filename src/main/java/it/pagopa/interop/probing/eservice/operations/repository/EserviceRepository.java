package it.pagopa.interop.probing.eservice.operations.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;

@Repository
public interface EserviceRepository extends JpaRepository<Eservice, Long> {
  Optional<Eservice> findByEserviceIdAndVersionId(@Param("eserviceId") UUID eserviceId,
      @Param("versionId") UUID versionId);

  Optional<Eservice> findById(@Param("id") Long id);
}
