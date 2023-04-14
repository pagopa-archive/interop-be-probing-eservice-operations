package it.pagopa.interop.probing.eservice.operations.util;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStateBE;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStateFE;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;

@Component
public class EnumUtilities {

  @Value("${minutes.ofTollerance.multiplier}")
  private int minOfTolleranceMultiplier;

  public static String fromFEtoBEState(EserviceStateFE state) {
    switch (state) {
      case ONLINE:
        return EserviceStateBE.ACTIVE.getValue();
      case OFFLINE:
        return EserviceStateBE.INACTIVE.getValue();
      default:
        return null;
    }
  }

  public List<String> convertListFromFEtoBE(List<EserviceStateFE> statusList) {
    return statusList.stream().map(EnumUtilities::fromFEtoBEState).filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public EserviceStateFE fromBEtoFEState(EserviceView view) {
    switch (view.getState()) {
      case ACTIVE:
        return checkND(view) ? EserviceStateFE.N_D : EserviceStateFE.ONLINE;
      case INACTIVE:
        return checkND(view) ? EserviceStateFE.N_D : EserviceStateFE.OFFLINE;
      default:
        return EserviceStateFE.N_D;
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
    return Duration.between(OffsetDateTime.now(), view.getLastRequest())
        .toMinutes() > (view.getPollingFrequency() * minOfTolleranceMultiplier);
  }

}
