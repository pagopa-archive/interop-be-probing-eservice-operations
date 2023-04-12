package it.pagopa.interop.probing.eservice.operations.unit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStateBE;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStateFE;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.util.EnumUtilities;

@SpringBootTest
class EnumUtilitiesTest {

  @Autowired
  EnumUtilities enumUtilities;

  @Mock
  EnumUtilities enumMock;

  private EserviceView view = new EserviceView();

  @BeforeEach
  void setup() {

  }

  @Test
  @DisplayName("From Online to active value")
  void testfromFEtoBEState_whenStateIsOnline_thenReturnActiveValue() {
    assertEquals(EnumUtilities.fromFEtoBEState(EserviceStateFE.ONLINE),
        EserviceStateBE.ACTIVE.getValue());
  }

  @Test
  @DisplayName("From Offline to Inactive value")
  void testfromFEtoBEState_whenStateIsOffline_thenReturnInactiveValue() {
    assertEquals(EnumUtilities.fromFEtoBEState(EserviceStateFE.OFFLINE),
        EserviceStateBE.INACTIVE.getValue());
  }

  @Test
  @DisplayName("From ND to null value")
  void testfromFEtoBEState_whenStateIsND_thenReturnNull() {
    assertNull(EnumUtilities.fromFEtoBEState(EserviceStateFE.N_D));
  }

  @Test
  @DisplayName("From EserviceStateFE to List<String> with EserviceStateBE values")
  void testconvertListFromFEtoBE_whenIsEserviceStateFEList_thenReturnListOfStringWithEserviceStateBEValues() {

    assertEquals(
        enumUtilities.convertListFromFEtoBE(List.of(EserviceStateFE.N_D, EserviceStateFE.ONLINE)),
        List.of(EserviceStateBE.ACTIVE.getValue()));
  }

  @Test
  @DisplayName("From BE to FE state return OFFLINE when checkND return false ")
  void testfromBEtoFEState_whenStateIsINACTIVE_thenReturnOFFLINEValue() {
    view = EserviceView.builder().state(EserviceStateBE.INACTIVE).build();

    assertEquals(EserviceStateFE.N_D, enumUtilities.fromBEtoFEState(view));
  }


}
