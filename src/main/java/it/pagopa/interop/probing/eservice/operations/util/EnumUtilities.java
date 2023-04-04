package it.pagopa.interop.probing.eservice.operations.util;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStateBE;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStateFE;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;

public class EnumUtilities {

  private EnumUtilities() {}

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

  public static List<String> convertListFromFEtoBE(List<EserviceStateFE> statusList) {
    return statusList.stream().map(EnumUtilities::fromFEtoBEState).filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public static EserviceStateFE fromBEtoFEState(EserviceView view) {
    switch (view.getState()) {
      case ACTIVE:
        return checkND(view) ? EserviceStateFE.N_D : EserviceStateFE.ONLINE;
      case INACTIVE:
        return checkND(view) ? EserviceStateFE.N_D : EserviceStateFE.OFFLINE;
      default:
        return null;
    }
  }

  public static boolean checkND(EserviceView view) {
    OffsetDateTime defaultDate = view.getResponseReceived() == null
        ? OffsetDateTime.of(9999, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC)
        : view.getResponseReceived();

    return (!view.isProbingEnabled() || view.getLastRequest() == null
        || ((Math.abs(Duration.between(OffsetDateTime.now(), view.getLastRequest())
            .toMinutes()) > view.getPollingFrequency() * 20)
            && defaultDate.isBefore(view.getLastRequest()))
        || view.getResponseReceived() == null);
  }

}
