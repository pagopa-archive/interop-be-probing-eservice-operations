package it.pagopa.interop.probing.eservice.operations.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceContent;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;
import it.pagopa.interop.probing.eservice.operations.dtos.PollingEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.SaveEserviceDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.mapper.AbstractMapper;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.model.EserviceContentCriteria;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView_;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceRepository;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceViewRepository;
import it.pagopa.interop.probing.eservice.operations.repository.query.builder.EserviceViewQueryBuilder;
import it.pagopa.interop.probing.eservice.operations.repository.specs.EserviceViewSpecs;
import it.pagopa.interop.probing.eservice.operations.service.EserviceService;
import it.pagopa.interop.probing.eservice.operations.util.EnumUtilities;
import it.pagopa.interop.probing.eservice.operations.util.OffsetLimitPageable;
import it.pagopa.interop.probing.eservice.operations.util.constant.ErrorMessages;
import it.pagopa.interop.probing.eservice.operations.util.constant.ProjectConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class EserviceServiceImpl implements EserviceService {

  @Autowired
  private EnumUtilities enumUtilities;

  @Autowired
  private EserviceRepository eserviceRepository;

  @Autowired
  private EserviceViewRepository eserviceViewRepository;

  @Autowired
  private EserviceViewQueryBuilder eserviceViewQueryBuilder;

  @Autowired
  private AbstractMapper mapper;

  @Value("${toleranceMultiplierInMinutes}")
  private int toleranceMultiplierInMinutes;

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public void updateEserviceState(UpdateEserviceStateDto inputData)
      throws EserviceNotFoundException {
    Optional<Eservice> queryResult = eserviceRepository
        .findByEserviceIdAndVersionId(inputData.getEserviceId(), inputData.getVersionId());

    Eservice eServiceToUpdate = queryResult
        .orElseThrow(() -> new EserviceNotFoundException(ErrorMessages.ELEMENT_NOT_FOUND));

    eServiceToUpdate.state(inputData.getNewEServiceState());
    eserviceRepository.save(eServiceToUpdate);

    log.info("EserviceState of eservice " + eServiceToUpdate.eserviceId() + " with version "
        + eServiceToUpdate.versionId() + " has been updated into " + eServiceToUpdate.state());
  }

  @Override
  public void updateEserviceProbingState(UpdateEserviceProbingStateDto inputData)
      throws EserviceNotFoundException {

    Optional<Eservice> queryResult = eserviceRepository
        .findByEserviceIdAndVersionId(inputData.getEserviceId(), inputData.getVersionId());

    Eservice eServiceToUpdate = queryResult
        .orElseThrow(() -> new EserviceNotFoundException(ErrorMessages.ELEMENT_NOT_FOUND));

    eServiceToUpdate.probingEnabled(inputData.isProbingEnabled());
    eserviceRepository.save(eServiceToUpdate);

    log.info("EserviceProbingState of eservice " + eServiceToUpdate.eserviceId() + " with version "
        + eServiceToUpdate.versionId() + " has been updated into "
        + eServiceToUpdate.probingEnabled());
  }

  @Override
  public void updateEserviceFrequency(UpdateEserviceFrequencyDto inputData)
      throws EserviceNotFoundException {

    Optional<Eservice> queryResult = eserviceRepository
        .findByEserviceIdAndVersionId(inputData.getEserviceId(), inputData.getVersionId());

    Eservice eServiceToUpdate = queryResult
        .orElseThrow(() -> new EserviceNotFoundException(ErrorMessages.ELEMENT_NOT_FOUND));

    eServiceToUpdate.pollingFrequency(inputData.getNewPollingFrequency())
        .pollingStartTime(inputData.getNewPollingStartTime())
        .pollingEndTime(inputData.getNewPollingEndTime());
    eserviceRepository.save(eServiceToUpdate);

    log.info("Eservice " + eServiceToUpdate.eserviceId() + " with version "
        + eServiceToUpdate.versionId() + " has been updated with startTime: "
        + eServiceToUpdate.pollingStartTime() + " and endTime: " + eServiceToUpdate.pollingEndTime()
        + " and frequency: " + eServiceToUpdate.pollingFrequency());
  }

  @Override
  public SearchEserviceResponse searchEservices(Integer limit, Integer offset, String eserviceName,
      String producerName, Integer versionNumber, List<EserviceMonitorState> state) {

    Page<EserviceView> eserviceList = null;
    List<String> stateBE = Objects.isNull(state) || state.isEmpty() ? new ArrayList<>()
        : enumUtilities.convertListFromMonitorToPdnd(state);

    if (Objects.isNull(state) || state.isEmpty()
        || (state.contains(EserviceMonitorState.N_D) && state.contains(EserviceMonitorState.ONLINE)
            && state.contains(EserviceMonitorState.OFFLINE))) {
      eserviceList = eserviceViewRepository.findAll(
          EserviceViewSpecs.searchSpecBuilder(eserviceName, producerName, versionNumber),
          new OffsetLimitPageable(offset, limit,
              Sort.by(ProjectConstants.ESERVICE_NAME_FIELD).ascending()));
    } else if (state.contains(EserviceMonitorState.N_D)) {
      eserviceList = eserviceViewQueryBuilder.findAllWithNDState(limit, offset, eserviceName,
          producerName, versionNumber, stateBE, toleranceMultiplierInMinutes);
    } else {
      eserviceList = eserviceViewQueryBuilder.findAllWithoutNDState(limit, offset, eserviceName,
          producerName, versionNumber, stateBE, toleranceMultiplierInMinutes);
    }

    List<EserviceContent> list = eserviceList.stream().map(e -> mapper.toSearchEserviceContent(e))
        .collect(Collectors.toList());

    return SearchEserviceResponse.builder().content(list).offset(eserviceList.getNumber())
        .limit(eserviceList.getSize()).totalElements(eserviceList.getTotalElements()).build();
  }

  @Override
  public Long saveEservice(SaveEserviceDto inputData) {
    Eservice eServiceToUpdate = eserviceRepository
        .findByEserviceIdAndVersionId(inputData.getEserviceId(), inputData.getVersionId())
        .orElseGet(() -> Eservice.builder().eserviceId(inputData.getEserviceId())
            .versionId(inputData.getVersionId()).lockVersion(1)
            .versionNumber(inputData.getVersionNumber()).build());

    eServiceToUpdate.eserviceName(inputData.getName()).producerName(inputData.getProducerName())
        .basePath(inputData.getBasePath()).technology(inputData.getTechnology())
        .state(inputData.getState());

    Long id = eserviceRepository.save(eServiceToUpdate).id();

    log.info("Service " + eServiceToUpdate.eserviceId() + " with version "
        + eServiceToUpdate.versionId() + " has been saved.");
    return id;
  }

  @Override
  public PollingEserviceResponse getEservicesReadyForPolling(Integer limit, Integer offset) {

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<EserviceContentCriteria> query = cb.createQuery(EserviceContentCriteria.class);
    Root<EserviceView> root = query.from(EserviceView.class);

    query.distinct(true).multiselect(root.get(EserviceView_.ID), root.get(EserviceView_.TECHNOLOGY),
        root.get(EserviceView_.BASE_PATH));

    Expression<Timestamp> makeInterval = cb.function("make_interval", Timestamp.class,
        root.get(EserviceView_.LAST_REQUEST), root.get(EserviceView_.POLLING_FREQUENCY));

    Expression<Boolean> compareTimestampInterval =
        cb.function("compare_timestamp_interval", Boolean.TYPE,
            root.get(EserviceView_.POLLING_START_TIME), root.get(EserviceView_.POLLING_END_TIME));

    Predicate predicate =
        cb.and(cb.equal(root.get(EserviceView_.STATE), EserviceInteropState.ACTIVE),
            cb.isTrue(root.get(EserviceView_.PROBING_ENABLED)),
            cb.lessThanOrEqualTo(makeInterval, cb.currentTimestamp()),
            cb.lessThanOrEqualTo(root.get(EserviceView_.RESPONSE_RECEIVED),
                root.get(EserviceView_.LAST_REQUEST)),
            cb.isTrue(compareTimestampInterval));

    query.where(predicate);
    TypedQuery<EserviceContentCriteria> q = entityManager.createQuery(query);

    List<EserviceContentCriteria> pollingActiveEserviceContent = q.getResultList();

    Page<EserviceContentCriteria> pollingActiveEservicePagable =
        new PageImpl<>(pollingActiveEserviceContent.stream().collect(Collectors.toList()),
            PageRequest.of(offset, limit, Sort.by(ProjectConstants.ID_FIELD).ascending()),
            pollingActiveEserviceContent.size());

    return PollingEserviceResponse.builder()
        .content(pollingActiveEservicePagable.stream().map(c -> (EserviceContent) c).toList())
        .totalElements(pollingActiveEservicePagable.getTotalElements()).build();
  }
}
