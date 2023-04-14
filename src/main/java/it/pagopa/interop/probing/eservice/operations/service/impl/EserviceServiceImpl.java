package it.pagopa.interop.probing.eservice.operations.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStateFE;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceContent;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.SaveEserviceDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.mapper.AbstractMapper;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceRepository;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceViewRepository;
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

  @Value("${minutes.ofTollerance.multiplier}")
  private int minOfTolleranceMultiplier;

  @Autowired
  EnumUtilities enumUtilities;

  @Autowired
  EserviceRepository eserviceRepository;

  @Autowired
  EserviceViewRepository eserviceViewRepository;

  @Autowired
  AbstractMapper mapper;

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
      String producerName, Integer versionNumber, List<EserviceStateFE> state) {

    Page<EserviceView> eserviceList = null;
    List<String> stateBE = Objects.isNull(state) || state.isEmpty() ? new ArrayList<>()
        : enumUtilities.convertListFromFEtoBE(state);

    if (Objects.isNull(state) || state.isEmpty() || (state.contains(EserviceStateFE.N_D)
        && state.contains(EserviceStateFE.ONLINE) && state.contains(EserviceStateFE.OFFLINE))) {
      eserviceList = eserviceViewRepository.findAll(
          EserviceViewSpecs.searchSpecBuilder(eserviceName, producerName, versionNumber),
          new OffsetLimitPageable(offset, limit,
              Sort.by(ProjectConstants.ESERVICE_NAME_FIELD).ascending()));
    } else if (state.contains(EserviceStateFE.N_D)) {
      eserviceList = eserviceViewRepository.findAllWithNDState(eserviceName, producerName,
          versionNumber, stateBE, minOfTolleranceMultiplier, new OffsetLimitPageable(offset, limit,
              Sort.by(ProjectConstants.ESERVICE_NAME_NATIVE_FIELD).ascending()));
    } else {
      eserviceList = eserviceViewRepository.findAllWithoutNDState(eserviceName, producerName,
          versionNumber, stateBE, minOfTolleranceMultiplier, new OffsetLimitPageable(offset, limit,
              Sort.by(ProjectConstants.ESERVICE_NAME_NATIVE_FIELD).ascending()));
    }

    List<SearchEserviceContent> lista = eserviceList.getContent().stream()
        .map(e -> mapper.toSearchEserviceContent(e)).collect(Collectors.toList());

    return SearchEserviceResponse.builder().content(lista).offset(eserviceList.getNumber())
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

}
