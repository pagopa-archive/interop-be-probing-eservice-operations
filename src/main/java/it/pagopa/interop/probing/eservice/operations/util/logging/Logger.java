package it.pagopa.interop.probing.eservice.operations.util.logging;

import java.util.List;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.model.EserviceProbingRequest;
import it.pagopa.interop.probing.eservice.operations.model.EserviceProbingResponse;

public interface Logger {

  void logMessageEserviceStateUpdated(Eservice eServiceToUpdate);

  void logMessageEserviceProbingStateUpdated(Eservice eServiceToUpdate);

  void logMessageLastRequestUpdated(EserviceProbingRequest eServiceToUpdate);

  void logMessageResponseReceivedUpdated(EserviceProbingResponse eServiceToUpdate);

  void logMessageEservicePollingConfigUpdated(Eservice eServiceToUpdate);

  void logMessageSearchEservice(Integer limit, Integer offset, String eserviceName,
      String producerName, Integer versionNumber, List<EserviceMonitorState> state);

  void logMessageSearchProducer(String producerName);

  void logMessageEserviceSaved(Eservice eServiceToUpdate);

  void logMessageException(Exception exception);

  void logMessageEserviceReadyForPolling(Integer limit, Integer offset);

  void logMessageEserviceMainData(Long eserviceRecordId);
}
