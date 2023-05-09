package it.pagopa.interop.probing.eservice.operations.rest;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import it.pagopa.interop.probing.eservice.operations.api.EservicesApi;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeEserviceStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeLastRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingFrequencyRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceSaveRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.PollingEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapping.mapper.AbstractMapper;
import it.pagopa.interop.probing.eservice.operations.service.EserviceService;

@RestController
public class EserviceController implements EservicesApi {

  @Autowired
  private EserviceService eserviceService;

  @Autowired
  private AbstractMapper mapper;

  @Override
  public ResponseEntity<Void> updateEserviceFrequency(UUID eserviceId, UUID versionId,
      ChangeProbingFrequencyRequest changeProbingFrequencyRequest)
      throws EserviceNotFoundException {
    eserviceService.updateEserviceFrequency(
        mapper.toUpdateEserviceFrequencyDto(eserviceId, versionId, changeProbingFrequencyRequest));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> updateEserviceProbingState(UUID eserviceId, UUID versionId,
      ChangeProbingStateRequest changeProbingStateRequest) throws EserviceNotFoundException {
    eserviceService.updateEserviceProbingState(
        mapper.toUpdateEserviceProbingStateDto(eserviceId, versionId, changeProbingStateRequest));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> updateEserviceState(UUID eserviceId, UUID versionId,
      ChangeEserviceStateRequest changeEserviceStateRequest) throws Exception {
    eserviceService.updateEserviceState(
        mapper.toUpdateEserviceStateDto(eserviceId, versionId, changeEserviceStateRequest));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<SearchEserviceResponse> searchEservices(Integer limit, Integer offset,
      String eserviceName, String producerName, Integer versionNumber,
      List<EserviceMonitorState> state) {
    return ResponseEntity.ok(eserviceService.searchEservices(limit, offset, eserviceName,
        producerName, versionNumber, state));
  }

  @Override
  public ResponseEntity<Long> saveEservice(UUID eserviceId, UUID versionId,
      EserviceSaveRequest eserviceSaveRequest) {
    return ResponseEntity.ok(eserviceService.saveEservice(mapper
        .fromEserviceSaveRequestToSaveEserviceDto(eserviceId, versionId, eserviceSaveRequest)));
  }

  @Override
  public ResponseEntity<PollingEserviceResponse> getEservicesReadyForPolling(Integer limit,
      Integer offset) {
    return ResponseEntity.ok(eserviceService.getEservicesReadyForPolling(limit, offset));
  }

  @Override
  public ResponseEntity<Void> updateLastRequest(Long eserviceRecordId,
      ChangeLastRequest changeLastRequest) throws EserviceNotFoundException {
    eserviceService.updateLastRequest(
        mapper.toUpdateEserviceLastRequest(eserviceRecordId, changeLastRequest));
    return ResponseEntity.noContent().build();
  }
}
