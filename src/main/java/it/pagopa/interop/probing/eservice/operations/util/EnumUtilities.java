package it.pagopa.interop.probing.eservice.operations.util;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;

@Component
public class EnumUtilities {

  @Value("${minutes.ofTollerance.multiplier}")
  private int minOfTolleranceMultiplier;

  public static String fromMonitorToPdndState(EserviceMonitorState state) {
    switch (state) {
      case ONLINE:
        return EserviceInteropState.ACTIVE.getValue();
      case OFFLINE:
        return EserviceInteropState.INACTIVE.getValue();
      default:
        return null;
    }
  }

  public List<String> convertListFromMonitorToPdnd(List<EserviceMonitorState> statusList) {
    return statusList.stream().map(EnumUtilities::fromMonitorToPdndState).filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public EserviceMonitorState fromPdndToMonitorState(EserviceView view) {
    switch (view.getState()) {
      case ACTIVE:
        return checkND(view) ? EserviceMonitorState.N_D : EserviceMonitorState.ONLINE;
      case INACTIVE:
        return checkND(view) ? EserviceMonitorState.N_D : EserviceMonitorState.OFFLINE;
      default:
        return EserviceMonitorState.N_D;
    }
  }

  public boolean checkND(EserviceView view) {
    return (!view.isProbingEnabled() || Objects.isNull(view.getLastRequest())
        || (isBeenToLongRequest(view) && isResponseReceivedBeforeLastRequest(view))
        || Objects.isNull(view.getResponseReceived()));
  }

  private boolean isResponseReceivedBeforeLastRequest(EserviceView view) {
    OffsetDateTime defaultDate = Objects.isNull(view.getResponseReceived()) ? OffsetDateTime.MAX
        : view.getResponseReceived();

    return defaultDate.isBefore(view.getLastRequest());
  }

  private boolean isBeenToLongRequest(EserviceView view) {
    return Duration
        .between(view.getLastRequest().withOffsetSameInstant(ZoneOffset.UTC),
            OffsetDateTime.now(ZoneOffset.UTC))
        .toMinutes() > (view.getPollingFrequency() * minOfTolleranceMultiplier);
  }

}
