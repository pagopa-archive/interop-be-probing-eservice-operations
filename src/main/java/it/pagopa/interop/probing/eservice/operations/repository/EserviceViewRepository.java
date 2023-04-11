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

  static final String COMMON_SEARCH_FIELD_NO_ND =
      "WHERE ((:eserviceName is null or e.eservice_name= :eserviceName) AND (:producerName is null or e.producer_name= :producerName) AND (:versionNumber is null or e.version_number= :versionNumber)) AND ((e.state in (:stateList) AND "
          + " (e.probing_enabled=true " + "AND e.last_request is not null "
          + "AND (EXTRACT(MINUTE from CURRENT_TIMESTAMP - e.last_request) < (e.polling_frequency*:minOfTolleranceMultiplier) OR e.response_received > e.last_request) "
          + "AND e.response_received is not null))";

  Page<EserviceView> findAll(Specification<EserviceView> specs, Pageable pageable);

  @Query("SELECT DISTINCT new it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse(e.producerName, e.producerName) FROM it.pagopa.interop.probing.eservice.operations.model.view.EserviceView e WHERE UPPER(e.producerName) LIKE %:producerName%")
  List<SearchProducerNameResponse> getEservicesProducers(@Param("producerName") String producerName,
      Pageable pageable);

  @Query(value = "SELECT e.* FROM eservice_view e " + COMMON_SEARCH_FIELD_NO_ND + " OR "
      + " (e.probing_enabled=false " + "OR e.last_request is null "
      + "OR (EXTRACT(MINUTE from CURRENT_TIMESTAMP - e.last_request) > (e.polling_frequency*:minOfTolleranceMultiplier) AND e.response_received < e.last_request) "
      + "OR e.response_received is null )) ", nativeQuery = true)
  Page<EserviceView> findAllWithNDState(@Param("eserviceName") String eserviceName,
      @Param("producerName") String producerName, @Param("versionNumber") Integer versionNumber,
      @Param("stateList") List<String> stateList,
      @Param("minOfTolleranceMultiplier") int minOfTolleranceMultiplier, Pageable pageable);

  @Query(value = "SELECT e.* FROM eservice_view e " + COMMON_SEARCH_FIELD_NO_ND + " )",
      nativeQuery = true)
  Page<EserviceView> findAllWithoutNDState(@Param("eserviceName") String eserviceName,
      @Param("producerName") String producerName, @Param("versionNumber") Integer versionNumber,
      @Param("stateList") List<String> stateList,
      @Param("minOfTolleranceMultiplier") int minOfTolleranceMultiplier, Pageable pageable);
}
