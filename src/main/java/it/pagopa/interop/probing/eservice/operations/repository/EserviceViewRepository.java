package it.pagopa.interop.probing.eservice.operations.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;

@Repository
public interface EserviceViewRepository extends JpaRepository<EserviceView, Long> {
  Page<EserviceView> findAll(Specification<EserviceView> specs, Pageable pageable);
}
