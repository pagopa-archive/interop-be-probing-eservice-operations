package it.pagopa.interop.probing.eservice.operations.integration.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.UUID;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.hsqldb.HsqlException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;

@DataJpaTest
class EserviceTest {
  @Autowired
  private TestEntityManager testEntityManager;

  Eservice eservice;

  @BeforeEach
  void setup() {
    eservice = Eservice.builder().state(EserviceInteropState.INACTIVE).eserviceId(UUID.randomUUID())
        .versionId(UUID.randomUUID()).eserviceName("e-service1")
        .basePath(new String[] {"test1", "test2"}).technology(EserviceTechnology.REST)
        .producerName("producer1").versionNumber(1).build();
  }

  @Test
  @DisplayName("e-service is saved with default values")
  void testEserviceEntity_whenDefaultValuesAreSet_returnsEservice() {
    Eservice eserviceDuplicate = testEntityManager.persistAndFlush(eservice);
    assertEquals(OffsetTime.of(23, 59, 0, 0, ZoneOffset.UTC), eserviceDuplicate.pollingEndTime(),
        "Values should be equal");
    assertEquals(OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC), eserviceDuplicate.pollingStartTime(),
        "Values should be equal");
    assertEquals(5, eserviceDuplicate.pollingFrequency(), "Values should be equal");
  }

  @Test
  @DisplayName("e-service is saved with non default values")
  void testEserviceEntity_whenDefaultValuesAreCorrectlyOverwritten_returnsEservice() {
    eservice.pollingEndTime(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC))
        .pollingStartTime(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC)).probingEnabled(true)
        .pollingFrequency(1);
    Eservice eserviceDuplicate = testEntityManager.persistAndFlush(eservice);
    assertEquals(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC), eserviceDuplicate.pollingEndTime(),
        "Values should be equal");
    assertEquals(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC), eserviceDuplicate.pollingStartTime(),
        "Values should be equal");
    assertEquals(1, eserviceDuplicate.pollingFrequency(), "Values should be equal");
    assertTrue(eserviceDuplicate.probingEnabled(), "Value should be true");
  }

  @Test
  @DisplayName("e-service isn't saved due to missing required data")
  void testEserviceEntity_whenEserviceDataNotProvided_throwsException() {
    Eservice emptyEservice = Eservice.builder().build();
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(emptyEservice),
        "e-service should not be saved when missing required data");
  }

  @Test
  @DisplayName("e-service isn't saved due to too long basePath value")
  void testEserviceEntity_whenBasePathIsTooLong_throwsException() {
    eservice.basePath(new String[] {RandomStringUtils.randomAlphabetic(2049)});
    assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when base path data is too long");
  }

  @Test
  @DisplayName("e-service isn't saved due to null basePath value")
  void testEserviceEntity_whenBasePathIsNull_throwsException() {
    eservice.basePath(null);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when base path data is null");
  }

  @Test
  @DisplayName("e-service isn't saved due to too long e-service name value")
  void testEserviceEntity_whenEserviceNameIsTooLong_throwsException() {
    eservice.eserviceName(RandomStringUtils.randomAlphabetic(256));
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when e-service name is too long");
  }

  @Test
  @DisplayName("e-service isn't saved due to null e-service name value")
  void testEserviceEntity_whenEserviceNameIsNull_throwsException() {
    eservice.eserviceName(null);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when e-service name is null");
  }

  @Test
  @DisplayName("e-service isn't saved due to blank e-service name value")
  void testEserviceEntity_whenEserviceNameIsBlank_throwsException() {
    eservice.eserviceName("");
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when e-service name is blank");
  }

  @Test
  @DisplayName("e-service isn't saved due to null e-serviceId value")
  void testEserviceEntity_whenEserviceIdIsNull_throwsException() {
    eservice.eserviceId(null);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when e-service id is null");
  }

  @Test
  @DisplayName("e-service isn't saved due to null polling end time value")
  void testEserviceEntity_whenPollingEndTimeIsNull_throwsException() {
    eservice.pollingEndTime(null);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when e-service polling end time is null");
  }

  @Test
  @DisplayName("e-service isn't saved due to null polling frequency value")
  void testEserviceEntity_whenPollingFrequencyIsNull_throwsException() {
    eservice.pollingFrequency(null);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when e-service polling frequency is null");
  }

  @Test
  @DisplayName("e-service isn't saved due to polling frequency value equal to zero")
  void testEserviceEntity_whenPollingFrequencyIsZero_throwsException() {
    eservice.pollingFrequency(0);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when e-service polling frequency is zero");
  }

  @Test
  @DisplayName("e-service isn't saved due to polling frequency negative value")
  void testEserviceEntity_whenPollingFrequencyIsNegative_throwsException() {
    eservice.pollingFrequency(-1);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when e-service polling frequency is negative");
  }

  @Test
  @DisplayName("e-service isn't saved due to null polling start time value")
  void testEserviceEntity_whenPollingStartTimeIsNull_throwsException() {
    eservice.pollingStartTime(null);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when e-service polling start time is null");
  }

  @Test
  @DisplayName("e-service isn't saved due to too long producer name value")
  void testEserviceEntity_whenProducerNameIsTooLong_throwsException() {
    eservice.producerName(RandomStringUtils.randomAlphabetic(256));
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when producer name is too long");
  }

  @Test
  @DisplayName("e-service isn't saved due to null producer name value")
  void testEserviceEntity_whenProducerNameIsNull_throwsException() {
    eservice.producerName(null);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when e-service producer name is null");
  }

  @Test
  @DisplayName("e-service isn't saved due to blank producer name value")
  void testEserviceEntity_whenProducerNameIsBlank_throwsException() {
    eservice.producerName("");
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when producer name is blank");
  }

  @Test
  @DisplayName("e-service isn't saved due to null state value")
  void testEserviceEntity_whenStateIsNull_throwsException() {
    eservice.state(null);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when state is null");
  }

  @Test
  @DisplayName("e-service isn't saved due to null version id value")
  void testEserviceEntity_whenVersionIdIsNull_throwsException() {
    eservice.versionId(null);
    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved when version id is null");
  }

  @Test
  @DisplayName("e-service isn't saved due to e-service id and version id already existing")
  void testEserviceEntity_whenGivenDuplicatedEserviceIdAndVersionId_throwsException() {
    testEntityManager.persistAndFlush(eservice);
    Eservice duplicateEservice = Eservice.builder().state(EserviceInteropState.INACTIVE)
        .eserviceId(UUID.randomUUID()).versionId(UUID.randomUUID()).eserviceName("e-service2")
        .basePath(new String[] {"test1", "test2"}).technology(EserviceTechnology.REST)
        .producerName("producer2").versionId(eservice.versionId()).eserviceId(eservice.eserviceId())
        .build();

    assertThrows(ConstraintViolationException.class,
        () -> testEntityManager.persistAndFlush(duplicateEservice),
        "e-service should not be saved when e-service id and version id are already existing");
  }

  @Test
  @DisplayName("e-service isn't saved because id can't be manually updated")
  void testEserviceEntity_whenIdIsManuallyUpdated_throwsException() {
    eservice.id(1L);
    assertThrows(PersistenceException.class, () -> testEntityManager.persistAndFlush(eservice),
        "e-service should not be saved because id shouldn't be manually updatable");
  }
}
