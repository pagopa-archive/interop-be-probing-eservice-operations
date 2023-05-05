package it.pagopa.interop.probing.eservice.operations.mapping.mapper;

import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeEserviceStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeLastRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingFrequencyRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceContent;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceSaveRequest;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.SaveEserviceDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceLastRequestDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.util.EnumUtilities;

@Mapper(componentModel = "spring")
public abstract class AbstractMapper {

  @Autowired
  EnumUtilities enumUtilities;

  @Mapping(source = "changeEServiceStateRequest.eServiceState", target = "newEServiceState")
  public abstract UpdateEserviceStateDto toUpdateEserviceStateDto(UUID eserviceId, UUID versionId,
      ChangeEserviceStateRequest changeEServiceStateRequest);

  public abstract UpdateEserviceProbingStateDto toUpdateEserviceProbingStateDto(UUID eserviceId,
      UUID versionId, ChangeProbingStateRequest changeProbingStateRequest);

  @Mapping(source = "changeProbingFrequencyRequest.frequency", target = "newPollingFrequency")
  @Mapping(source = "changeProbingFrequencyRequest.startTime", target = "newPollingStartTime")
  @Mapping(source = "changeProbingFrequencyRequest.endTime", target = "newPollingEndTime")
  public abstract UpdateEserviceFrequencyDto toUpdateEserviceFrequencyDto(UUID eserviceId,
      UUID versionId, ChangeProbingFrequencyRequest changeProbingFrequencyRequest);

  public abstract UpdateEserviceLastRequestDto toUpdateEserviceLastRequest(Long eservicesRecordId,
      ChangeLastRequest changeLastRequest);

  public abstract SaveEserviceDto fromEserviceSaveRequestToSaveEserviceDto(UUID eserviceId,
      UUID versionId, EserviceSaveRequest eserviceSaveRequest);

  @Mapping(target = "state", expression = "java(mapStatus(eserviceViewEntity))")
  public abstract EserviceContent toSearchEserviceContent(EserviceView eserviceViewEntity);

  EserviceMonitorState mapStatus(EserviceView eserviceViewEntity) {
    return enumUtilities.fromPdndToMonitorState(eserviceViewEntity);
  }

}
