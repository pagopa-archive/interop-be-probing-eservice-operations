package it.pagopa.interop.probing.eservice.operations.mapping.mapper;

import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeEserviceStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingFrequencyRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceSaveRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceContent;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.SaveEserviceDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;

@Mapper(componentModel = "spring")
public interface MapperImpl {

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

  List<SearchEserviceContent> toSearchEserviceResponse(List<EserviceView> eserviceViewEntity);

  SaveEserviceDto fromEserviceSaveRequestToSaveEserviceDto(UUID eserviceId, UUID versionId,
      EserviceSaveRequest eserviceSaveRequest);
}
