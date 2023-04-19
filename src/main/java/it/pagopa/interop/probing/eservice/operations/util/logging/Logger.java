package it.pagopa.interop.probing.eservice.operations.util.logging;

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;

import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;

public interface Logger {

  void logMessageEserviceStateUpdated(UUID eserviceId, UUID versionId,
      EserviceInteropState eServiceState);

  void logMessageEserviceProbingStateUpdated(UUID eserviceId, UUID versionId,
      boolean probingEnabled);

  void logMessageEservicePollingConfigUpdated(UUID eserviceId, UUID versionId, OffsetTime startTime,
      OffsetTime endTime, int frequency);

  void logMessageSearchEservice(Integer limit, Integer offset, String eserviceName,
      String producerName, Integer versionNumber, List<EserviceMonitorState> state);

  void logMessageSearchProducer(String producerName);
  void logMessageEserviceSaved(UUID eserviceId, UUID versionId);

  void logMessageException(Exception exception);
}
