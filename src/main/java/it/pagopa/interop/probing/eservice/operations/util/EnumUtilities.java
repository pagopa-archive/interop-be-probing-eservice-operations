package it.pagopa.interop.probing.eservice.operations.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;
import it.pagopa.interop.probing.eservice.operations.util.constant.ProjectConstants;

@Component
public class EnumUtilities {

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


}
