package it.pagopa.interop.probing.eservice.operations.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.SaveEserviceDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.mapper.MapperImpl;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceRepository;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceViewRepository;
import it.pagopa.interop.probing.eservice.operations.repository.specs.EserviceViewSpecs;
import it.pagopa.interop.probing.eservice.operations.util.OffsetLimitPageable;
import it.pagopa.interop.probing.eservice.operations.util.constant.ErrorMessages;
import it.pagopa.interop.probing.eservice.operations.util.constant.ProjectConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class EserviceServiceImpl implements EserviceService {

  @Autowired
  EserviceRepository eserviceRepository;

  @Autowired
  EserviceViewRepository eserviceViewRepository;

  @Autowired
  MapperImpl mapper;

  @Override
  public Long saveEservice(SaveEserviceDto inputData) {
    UUID eserviceId = UUID.fromString(inputData.getEserviceId());
    UUID versionId = UUID.fromString(inputData.getVersionId());
    Eservice eServiceToUpdate =
        eserviceRepository.findByEserviceIdAndVersionId(eserviceId, versionId).orElseGet(
            () -> Eservice.builder().versionId(versionId).eserviceId(eserviceId).lockVersion(1)
                .versionNumber(Integer.valueOf(inputData.getVersionNumber())).build());

    eServiceToUpdate.eserviceName(inputData.getName()).producerName(inputData.getProducerName())
        .basePath(inputData.getBasePath()).technology(inputData.getTechnology())
        .state(inputData.getState());

    Long id = eserviceRepository.save(eServiceToUpdate).id();

    log.info("Service " + eServiceToUpdate.eserviceId() + " with version "
        + eServiceToUpdate.versionId() + " has been saved.");
    return id;
  }



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

  public SearchEserviceResponse searchEservices(Integer limit, Integer offset, String eserviceName,
      String eserviceProducerName, Integer versionNumber, List<EserviceState> eServiceState) {
    Page<EserviceView> eserviceList = eserviceViewRepository.findAll(
        EserviceViewSpecs.searchSpecBuilder(eserviceName, eserviceProducerName, versionNumber,
            eServiceState),
        new OffsetLimitPageable(offset, limit,
            Sort.by(ProjectConstants.ESERVICE_NAME_FIELD).ascending()));
    return SearchEserviceResponse.builder()
        .content(mapper.toSearchEserviceResponse(eserviceList.getContent()))
        .offset(eserviceList.getNumber()).limit(eserviceList.getSize())
        .totalElements(eserviceList.getTotalElements()).build();
  }

}
