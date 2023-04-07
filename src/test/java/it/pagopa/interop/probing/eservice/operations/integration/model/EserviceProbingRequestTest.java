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
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStateBE;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.model.EserviceProbingRequest;

@DataJpaTest
class EserviceProbingRequestTest {

  @Autowired
  private TestEntityManager testEntityManager;
  EserviceProbingRequest probingRequest;
  Eservice eservice;

  @BeforeEach
  void setup() {
    eservice = Eservice.builder().state(EserviceStateBE.INACTIVE).eserviceId(UUID.randomUUID())
        .versionId(UUID.randomUUID()).eserviceName("e-service1")
        .basePath(new String[] {"test1", "test2"}).technology(EserviceTechnology.REST)
        .producerName("producer1").build();
    probingRequest = EserviceProbingRequest.builder()
        .lastRequest(OffsetDateTime.of(2023, 12, 12, 1, 0, 0, 0, ZoneOffset.UTC)).eservice(eservice)
        .build();
  }

  @Test
  @DisplayName("Request is correctly saved")
  void testEserviceProbingResponseEntity_whenCorrectDataIsProvided_returnsEserviceProbingResponse() {
    EserviceProbingRequest probingResponseDuplicate =
        testEntityManager.persistAndFlush(probingRequest);
    assertEquals(OffsetDateTime.of(2023, 12, 12, 1, 0, 0, 0, ZoneOffset.UTC),
        probingResponseDuplicate.lastRequest(), "Values should be equal");
    assertNotNull(probingResponseDuplicate.eservice(), "Value should not be null");
    assertEquals("e-service1", probingResponseDuplicate.eservice().eserviceName(),
        "Values should be equal");
  }

  @Test
  @DisplayName("Request isn't saved due to null last request timestamp")
  void testEserviceProbingResponseEntity_whenLastRequestIsNull_throwsException() {
    probingRequest.lastRequest(null);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(probingRequest),
        "Request should not be saved because response received shouldn't be null");
  }

  @Test
  @DisplayName("Request isn't saved due to null e-service reference value")
  void testEserviceProbingResponseEntity_whenAssociatedEserviceIsNull_throwsException() {
    probingRequest.eservice(null);
    assertThrows(PersistenceException.class,
        () -> testEntityManager.persistAndFlush(probingRequest),
        "Request should not be saved because e-service reference shouldn't be null");
  }

  @Test
  @DisplayName("Request isn't saved due to duplicated e-service reference")
  void testEserviceProbingResponseEntity_whenEserviceReferenceAlreadyExists_throwsException() {
    testEntityManager.persistAndFlush(probingRequest);
    EserviceProbingRequest duplicatedProbingRequest = new EserviceProbingRequest();
    duplicatedProbingRequest.lastRequest(
        OffsetDateTime.of(OffsetDateTime.now().getYear(), OffsetDateTime.now().getMonthValue(),
            OffsetDateTime.now().getDayOfMonth(), 23, 59, 0, 0, ZoneOffset.UTC));
    duplicatedProbingRequest.eservice(eservice);
    assertThrows(PersistenceException.class,
        () -> testEntityManager.persistAndFlush(duplicatedProbingRequest),
        "Request should not be saved because duplicated e-service reference shouldn't be allowed");
  }
}
