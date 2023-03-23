package it.pagopa.interop.probing.eservice.operations.mapstruct.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import it.pagopa.interop.probing.eservice.operations.dtos.ChangeEserviceStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingFrequencyRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingStateRequest;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceStateDto;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

	@Mapping(source = "changeEServiceStateRequest.eServiceState", target = "newEServiceState")
	UpdateEserviceStateDto toUpdateEserviceStateDto(UUID eserviceId, UUID versionId,
			ChangeEserviceStateRequest changeEServiceStateRequest);

	UpdateEserviceProbingStateDto toUpdateEserviceProbingStateDto(UUID eserviceId, UUID versionId,
			ChangeProbingStateRequest changeProbingStateRequest);

	@Mapping(source = "changeProbingFrequencyRequest.frequency", target = "newPollingFrequency")
	@Mapping(source = "changeProbingFrequencyRequest.startTime", target = "newPollingStartTime")
	@Mapping(source = "changeProbingFrequencyRequest.endTime", target = "newPollingEndTime")
	UpdateEserviceFrequencyDto toUpdateEserviceFrequencyDto(UUID eserviceId, UUID versionId,
			ChangeProbingFrequencyRequest changeProbingFrequencyRequest);
}
