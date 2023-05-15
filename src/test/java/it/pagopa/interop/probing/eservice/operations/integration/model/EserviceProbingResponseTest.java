package it.pagopa.interop.probing.eservice.operations.integration.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStatus;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.model.EserviceProbingResponse;

@DataJpaTest
class EserviceProbingResponseTest {

  @Autowired
  private TestEntityManager testEntityManager;

  EserviceProbingResponse probingResponse;
  Eservice eservice;

  @BeforeEach
  void setup() {
    eservice = Eservice.builder().state(EserviceInteropState.INACTIVE).eserviceId(UUID.randomUUID())
        .versionId(UUID.randomUUID()).eserviceName("e-service1")
        .basePath(new String[] {"test1", "test2"}).technology(EserviceTechnology.REST)
        .producerName("producer1").versionNumber(1).build();
    probingResponse = EserviceProbingResponse.builder()
        .responseReceived(OffsetDateTime.of(2023, 12, 12, 1, 0, 0, 0, ZoneOffset.UTC))
        .status(EserviceStatus.OK).eservice(eservice).build();
  }

  @Test
  @DisplayName("Response is correctly saved")
  void testEserviceProbingResponseEntity_whenCorrectDataIsProvided_returnsEserviceProbingResponse() {
    eservice = testEntityManager.persistAndFlush(eservice);
    EserviceProbingResponse probingResponseDuplicate =
        testEntityManager.persistAndFlush(probingResponse);
    assertEquals(probingResponse.responseReceived(), probingResponseDuplicate.responseReceived(),
        "Values should be equal");
    assertNotNull(probingResponseDuplicate.eservice(), "Value should not be null");
    assertEquals("e-service1", probingResponseDuplicate.eservice().eserviceName(),
        "Values should be equal");
  }

  @Test
  @DisplayName("Response isn't saved due to null response received timestamp")
  void testEserviceProbingResponseEntity_whenResponseReceivedIsNull_throwsException() {
    probingResponse.responseReceived(null);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(probingResponse),
        "Response should not be saved because response received shouldn't be null");
  }

  @Test
  @DisplayName("Response isn't saved due to null response status")
  void testEserviceProbingResponseEntity_whenResponseStatusIsNull_throwsException() {
    probingResponse.status(null);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(probingResponse),
        "Response should not be saved because response received shouldn't be null");
  }

  @Test
  @DisplayName("Response isn't saved due to null e-service reference value")
  void testEserviceProbingResponseEntity_whenAssociatedEserviceIsNull_throwsException() {
    probingResponse.eservice(null);
    assertThrows(PersistenceException.class,
        () -> testEntityManager.persistAndFlush(probingResponse),
        "Response should not be saved because e-service reference shouldn't be null");
  }

  @Test
  @DisplayName("Response isn't saved due to duplicated e-service reference")
  void testEserviceProbingResponseEntity_whenEserviceReferenceAlreadyExists_throwsException() {
    testEntityManager.persistAndFlush(probingResponse);
    EserviceProbingResponse duplicatedProbingRequest =
        EserviceProbingResponse.builder()
            .responseReceived(OffsetDateTime.of(OffsetDateTime.now().getYear(),
                OffsetDateTime.now().getMonthValue(), OffsetDateTime.now().getDayOfMonth(), 23, 59,
                0, 0, ZoneOffset.UTC))
            .eservice(eservice).build();
    assertThrows(PersistenceException.class,
        () -> testEntityManager.persistAndFlush(duplicatedProbingRequest),
        "Response should not be saved because duplicated e-service reference shouldn't be allowed");
  }
}
