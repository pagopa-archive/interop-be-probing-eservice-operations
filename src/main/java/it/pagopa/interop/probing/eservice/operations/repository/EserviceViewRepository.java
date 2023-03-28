package it.pagopa.interop.probing.eservice.operations.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;

@Repository
public interface EserviceViewRepository extends JpaRepository<EserviceView, Long> {

	Page<EserviceView> findAll(Specification<EserviceView> specs, Pageable pageable);

	@Query("SELECT DISTINCT new it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse(e.producerName, e.producerName) FROM it.pagopa.interop.probing.eservice.operations.model.view.EserviceView e WHERE UPPER(e.producerName) LIKE %:producerName%")
	List<SearchProducerNameResponse> getEservicesProducers(@Param("producerName") String producerName,
			Pageable pageable);
}
