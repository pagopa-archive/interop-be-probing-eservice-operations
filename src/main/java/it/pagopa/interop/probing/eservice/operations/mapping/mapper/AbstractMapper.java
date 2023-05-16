package it.pagopa.interop.probing.eservice.operations.mapping.mapper;

import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeEserviceStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeLastRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingFrequencyRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeResponseReceived;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceContent;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceSaveRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ProbingDataEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.SaveEserviceDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceLastRequestDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceResponseReceivedDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;

@Mapper(componentModel = "spring")
public abstract class AbstractMapper {


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

  public abstract UpdateEserviceLastRequestDto toUpdateEserviceLastRequest(Long eserviceRecordId,
      ChangeLastRequest changeLastRequest);

  public abstract UpdateEserviceResponseReceivedDto toUpdateEserviceResponseReceivedDto(
      Long eserviceRecordId, ChangeResponseReceived changeResponseReceived);

  public abstract SaveEserviceDto fromEserviceSaveRequestToSaveEserviceDto(UUID eserviceId,
      UUID versionId, EserviceSaveRequest eserviceSaveRequest);

  public abstract EserviceContent toSearchEserviceContent(EserviceView eserviceViewEntity);


  public abstract ProbingDataEserviceResponse toProbingDataEserviceResponse(
      EserviceView eserviceViewEntity);

}
