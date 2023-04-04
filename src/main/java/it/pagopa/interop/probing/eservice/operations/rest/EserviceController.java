package it.pagopa.interop.probing.eservice.operations.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import it.pagopa.interop.probing.eservice.operations.api.EservicesApi;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeEserviceStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingFrequencyRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceSaveRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapstruct.mapper.MapStructMapper;
import it.pagopa.interop.probing.eservice.operations.service.EserviceService;

@RestController
public class EserviceController implements EservicesApi {

	@Autowired
	EserviceService eserviceService;

	@Autowired
	MapStructMapper mapstructMapper;

	@Override
	public ResponseEntity<Void> updateEserviceFrequency(UUID eserviceId, UUID versionId,
			ChangeProbingFrequencyRequest changeProbingFrequencyRequest) throws EserviceNotFoundException {
		eserviceService.updateEserviceFrequency(
				mapstructMapper.toUpdateEserviceFrequencyDto(eserviceId, versionId, changeProbingFrequencyRequest));
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> updateEserviceProbingState(UUID eserviceId, UUID versionId,
			ChangeProbingStateRequest changeProbingStateRequest) throws EserviceNotFoundException {
		eserviceService.updateEserviceProbingState(
				mapstructMapper.toUpdateEserviceProbingStateDto(eserviceId, versionId, changeProbingStateRequest));
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> updateEserviceState(UUID eserviceId, UUID versionId,
			ChangeEserviceStateRequest changeEserviceStateRequest) throws Exception {
		eserviceService.updateEserviceState(
				mapstructMapper.toUpdateEserviceStateDto(eserviceId, versionId, changeEserviceStateRequest));
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<SearchEserviceResponse> searchEservices(Integer limit, Integer offset, String eserviceName,
			Integer versionNumber, String eserviceProducerName, List<EserviceState> eServiceState) {
		return ResponseEntity.ok(eserviceService.searchEservices(limit, offset, eserviceName, eserviceProducerName,
				versionNumber, eServiceState));
	}
	
	@Override
	public ResponseEntity<Long> saveEservice(EserviceSaveRequest eserviceSaveRequest) {
		return ResponseEntity.ok(eserviceService.saveEservice(
				mapstructMapper.fromEserviceSaveRequestToSaveEserviceDto(eserviceSaveRequest)));
	}
}
