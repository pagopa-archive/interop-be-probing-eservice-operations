package it.pagopa.interop.probing.eservice.operations.unit.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

import static org.junit.jupiter.api.Assertions.*;

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
    view.setState(EserviceStateBE.INACTIVE);

    assertEquals(EserviceStateFE.N_D, enumUtilities.fromBEtoFEState(view));
  }

  @Test
  @DisplayName("From BE to FE state return ONLINE when checkND return false ")
  void testfromBEtoFEState_whenStateIsACTIVE_thenReturnOLINEValue() {
    view.setState(EserviceStateBE.ACTIVE);

    assertEquals(EserviceStateFE.N_D, enumUtilities.fromBEtoFEState(view));
  }

  @Test
  @DisplayName("CheckND returns true when probing is disabled")
  void testCheckND_whenProbingIsNotEnabled_thenReturnsTrue(){
    view.setProbingEnabled(false);
    assertTrue(enumUtilities.checkND(view));
  }

  @Test
  @DisplayName("CheckND returns true when response received is null")
  void testCheckND_whenResponseReceivedIsNull_thenReturnsTrue(){
    view.setProbingEnabled(true);
    view.setLastRequest(OffsetDateTime.of(OffsetDateTime.now().getYear(), OffsetDateTime.now()
        .getMonthValue(), OffsetDateTime.now().getDayOfMonth(), OffsetDateTime.now().getHour(),
        OffsetDateTime.now().getMinute(), 0,0, ZoneOffset.UTC));
    view.setPollingFrequency(5);
    assertTrue(enumUtilities.checkND(view));
  }

}
