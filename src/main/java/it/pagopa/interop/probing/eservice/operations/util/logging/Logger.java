package it.pagopa.interop.probing.eservice.operations.util.logging;

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;

import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;

public interface Logger {

  void logMessageEserviceStateUpdated(Eservice eServiceToUpdate);

  void logMessageEserviceProbingStateUpdated(Eservice eServiceToUpdate);

  void logMessageEservicePollingConfigUpdated(Eservice eServiceToUpdate);

  void logMessageSearchEservice(Integer limit, Integer offset, String eserviceName,
      String producerName, Integer versionNumber, List<EserviceMonitorState> state);

  void logMessageSearchProducer(String producerName);
  void logMessageEserviceSaved(Eservice eServiceToUpdate);

  void logMessageException(Exception exception);
}
