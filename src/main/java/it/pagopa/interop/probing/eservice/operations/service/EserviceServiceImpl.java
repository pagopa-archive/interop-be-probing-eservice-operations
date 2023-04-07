package it.pagopa.interop.probing.eservice.operations.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStateFE;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceViewDTO;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.mapper.MapStructMapper;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceRepository;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceViewRepository;
import it.pagopa.interop.probing.eservice.operations.repository.specs.EserviceViewSpecs;
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
  EserviceRepository eserviceRepository;

  @Autowired
  EserviceViewRepository eserviceViewRepository;

  @Autowired
  MapStructMapper mapstructMapper;

  @Override
  public void updateEserviceState(UpdateEserviceStateDto inputData)
      throws EserviceNotFoundException {
    Optional<Eservice> queryResult = eserviceRepository
        .findByEserviceIdAndVersionId(inputData.getEserviceId(), inputData.getVersionId());

    Eservice eServiceToUpdate = queryResult
        .orElseThrow(() -> new EserviceNotFoundException(ErrorMessages.ELEMENT_NOT_FOUND));

    eServiceToUpdate.setState(inputData.getNewEServiceState());
    eserviceRepository.save(eServiceToUpdate);

    log.info("EserviceState of eservice " + eServiceToUpdate.getEserviceId() + " with version "
        + eServiceToUpdate.getVersionId() + " has been updated into "
        + eServiceToUpdate.getState());
  }

  @Override
  public void updateEserviceProbingState(UpdateEserviceProbingStateDto inputData)
      throws EserviceNotFoundException {

    Optional<Eservice> queryResult = eserviceRepository
        .findByEserviceIdAndVersionId(inputData.getEserviceId(), inputData.getVersionId());

    Eservice eServiceToUpdate = queryResult
        .orElseThrow(() -> new EserviceNotFoundException(ErrorMessages.ELEMENT_NOT_FOUND));

    eServiceToUpdate.setProbingEnabled(inputData.isProbingEnabled());
    eserviceRepository.save(eServiceToUpdate);

    log.info("EserviceProbingState of eservice " + eServiceToUpdate.getEserviceId()
        + " with version " + eServiceToUpdate.getVersionId() + " has been updated into "
        + eServiceToUpdate.isProbingEnabled());
  }

  @Override
  public void updateEserviceFrequency(UpdateEserviceFrequencyDto inputData)
      throws EserviceNotFoundException {

    Optional<Eservice> queryResult = eserviceRepository
        .findByEserviceIdAndVersionId(inputData.getEserviceId(), inputData.getVersionId());

    Eservice eServiceToUpdate = queryResult
        .orElseThrow(() -> new EserviceNotFoundException(ErrorMessages.ELEMENT_NOT_FOUND));

    eServiceToUpdate.setPollingFrequency(inputData.getNewPollingFrequency());
    eServiceToUpdate.setPollingStartTime(inputData.getNewPollingStartTime());
    eServiceToUpdate.setPollingEndTime(inputData.getNewPollingEndTime());
    eserviceRepository.save(eServiceToUpdate);

    log.info("Eservice " + eServiceToUpdate.getEserviceId() + " with version "
        + eServiceToUpdate.getVersionId() + " has been updated with startTime: "
        + eServiceToUpdate.getPollingStartTime() + " and endTime: "
        + eServiceToUpdate.getPollingEndTime() + " and frequency: "
        + eServiceToUpdate.getPollingFrequency());
  }

  @Override
  public SearchEserviceResponse searchEservices(Integer limit, Integer offset, String eserviceName,
      String producerName, Integer versionNumber, List<EserviceStateFE> state) {

    Page<EserviceView> eserviceList = null;
    List<String> stateBE = state == null || state.isEmpty() ? new ArrayList<>()
        : new EnumUtilities().convertListFromFEtoBE(state);

    if (state == null || state.isEmpty() || (state.contains(EserviceStateFE.N_D)
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

    List<EserviceViewDTO> lista = eserviceList.getContent().stream()
        .map(e -> mapstructMapper.toSearchEserviceResponse(e)).collect(Collectors.toList());

    return SearchEserviceResponse.builder().content(lista).offset(eserviceList.getNumber())
        .limit(eserviceList.getSize()).totalElements(eserviceList.getTotalElements()).build();
  }

  @Override
  public List<SearchProducerNameResponse> getEservicesProducers(String producerName) {
    return eserviceViewRepository.getEservicesProducers(producerName.toUpperCase(),
        new OffsetLimitPageable(0, 10,
            Sort.by(ProjectConstants.ESERVICE_NAME_NATIVE_FIELD).ascending()));
  }

}
