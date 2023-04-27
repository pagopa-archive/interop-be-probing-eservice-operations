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
import it.pagopa.interop.probing.eservice.operations.util.constant.ProjectConstants;

@Component
public class EnumUtilities {

  @Value("${toleranceMultiplierInMinutes}")
  private int toleranceMultiplierInMinutes;

  public static String fromMonitorToPdndState(EserviceMonitorState state) {
    return switch (state) {
      case ONLINE -> EserviceInteropState.ACTIVE.getValue();
      case OFFLINE -> EserviceInteropState.INACTIVE.getValue();
      case N_D -> ProjectConstants.N_D;
      default -> throw new IllegalArgumentException("Invalid state {}= " + state);
    };
  }

  public List<String> convertListFromMonitorToPdnd(List<EserviceMonitorState> statusList) {
    return statusList.stream().map(EnumUtilities::fromMonitorToPdndState).filter(Objects::nonNull)
        .filter(str -> !str.equals(ProjectConstants.N_D)).collect(Collectors.toList());
  }

  public EserviceMonitorState fromPdndToMonitorState(EserviceView view) {
    return switch (view.getState()) {
      case ACTIVE -> checkND(view) ? EserviceMonitorState.N_D : EserviceMonitorState.ONLINE;
      case INACTIVE -> checkND(view) ? EserviceMonitorState.N_D : EserviceMonitorState.OFFLINE;
      default -> throw new IllegalArgumentException("Invalid state {}= " + view.getState());
    };
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
        .toMinutes() > (Long.valueOf(view.getPollingFrequency() * toleranceMultiplierInMinutes));
  }

}
