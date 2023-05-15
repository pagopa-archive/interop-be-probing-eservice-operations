package it.pagopa.interop.probing.eservice.operations.unit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.util.EnumUtilities;

@SpringBootTest
class EnumUtilitiesTest {

  @Autowired
  EnumUtilities enumUtilities;

  @Mock
  EnumUtilities enumMock;

  EserviceView view;

  @BeforeEach
  void setup() {
    view = EserviceView.builder().build();
  }

  @Test
  @DisplayName("From Online to active value")
  void testfromFEtoBEState_whenStateIsOnline_thenReturnActiveValue() {
    assertEquals(EnumUtilities.fromMonitorToPdndState(EserviceMonitorState.ONLINE),
        EserviceInteropState.ACTIVE.getValue());
  }

  @Test
  @DisplayName("From Offline to Inactive value")
  void testfromFEtoBEState_whenStateIsOffline_thenReturnInactiveValue() {
    assertEquals(EnumUtilities.fromMonitorToPdndState(EserviceMonitorState.OFFLINE),
        EserviceInteropState.INACTIVE.getValue());
  }

  @Test
  @DisplayName("From EserviceMonitorState to List<String> with EserviceInteropState values")
  void testconvertListFromFEtoBE_whenIsEserviceStateFEList_thenReturnListOfStringWithEserviceInteropStateValues() {

    assertEquals(
        enumUtilities.convertListFromMonitorToPdnd(
            List.of(EserviceMonitorState.N_D, EserviceMonitorState.ONLINE)),
        List.of(EserviceInteropState.ACTIVE.getValue()));
  }

}
