package it.pagopa.interop.probing.eservice.operations.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.util.constant.ProjectConstants;

@Repository
public interface EserviceViewRepository extends JpaRepository<EserviceView, Long> {

  static final String COMMON_SEARCH_FIELD_WITH_NO_ND_CONDITION =
      "WHERE ((:eserviceName is null or e.eservice_name= :eserviceName)"
          + " AND (:producerName is null or e.producer_name= :producerName)"
          + " AND (:versionNumber is null or e.version_number= :versionNumber))"
          + " AND ((e.state in (:stateList) AND (e.probing_enabled=true "
          + " AND e.last_request is not null "
          + " AND (EXTRACT(MINUTE from CURRENT_TIMESTAMP - e.last_request) < (e.polling_frequency*:minOfTolleranceMultiplier)"
          + " OR e.response_received > e.last_request) AND e.response_received is not null)"
          + "  )";

  static final String SELECT_ALL_FROM_ESERVICE_VIEW = "SELECT e.* FROM eservice_view e ";

  static final String ND_WHERE_CONDITION = " OR (e.probing_enabled=false"
      + " OR e.last_request is null"
      + " OR (EXTRACT(MINUTE from CURRENT_TIMESTAMP - e.last_request) > (e.polling_frequency*:minOfTolleranceMultiplier)"
      + " AND e.response_received < e.last_request)" + " OR e.response_received is null )) ";

  static final String BASE_QUERY_WHERE_STATE =
      SELECT_ALL_FROM_ESERVICE_VIEW + COMMON_SEARCH_FIELD_WITH_NO_ND_CONDITION;

  Page<EserviceView> findAll(Specification<EserviceView> specs, Pageable pageable);

  @Query(value = BASE_QUERY_WHERE_STATE + ND_WHERE_CONDITION, nativeQuery = true)
  Page<EserviceView> findAllWithNDState(
      @Param(ProjectConstants.ESERVICE_NAME_FIELD) String eserviceName,
      @Param(ProjectConstants.PRODUCER_NAME_FIELD) String producerName,
      @Param(ProjectConstants.VERSION_NUMBER_FIELD) Integer versionNumber,
      @Param(ProjectConstants.STATE_LIST_PARAM) List<String> stateList,
      @Param(ProjectConstants.MIN_OF_TOLLERANCE_PARAM) int minOfTolleranceMultiplier,
      Pageable pageable);

  @Query(value = BASE_QUERY_WHERE_STATE + " )", nativeQuery = true)
  Page<EserviceView> findAllWithoutNDState(
      @Param(ProjectConstants.ESERVICE_NAME_FIELD) String eserviceName,
      @Param(ProjectConstants.PRODUCER_NAME_FIELD) String producerName,
      @Param(ProjectConstants.VERSION_NUMBER_FIELD) Integer versionNumber,
      @Param(ProjectConstants.STATE_LIST_PARAM) List<String> stateList,
      @Param(ProjectConstants.MIN_OF_TOLLERANCE_PARAM) int minOfTolleranceMultiplier,
      Pageable pageable);

  @Query(
      value = "SELECT e.* FROM eservice_view e WHERE (e.state = 'ACTIVE' AND e.probing_enabled=true AND (e.last_request + make_interval(mins=>e.polling_frequency)<= CURRENT_TIMESTAMP AND e.response_received >= e.last_request AND (CURRENT_TIME between e.polling_start_time and e.polling_end_time)))",
      nativeQuery = true)
  Page<EserviceView> findAll(Pageable pageable);
}
