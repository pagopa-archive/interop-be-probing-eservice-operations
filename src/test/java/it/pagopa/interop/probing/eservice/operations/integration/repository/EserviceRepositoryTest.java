package it.pagopa.interop.probing.eservice.operations.integration.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStateBE;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceRepository;

@DataJpaTest
class EserviceRepositoryTest {
  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private EserviceRepository eserviceRepository;

  private final UUID eServiceId = UUID.randomUUID();
  private final UUID versionId = UUID.randomUUID();

  @BeforeEach
  void setup() {
    Eservice eservice = Eservice.builder().eserviceId(eServiceId).versionId(versionId)
        .eserviceName("e-service1").pollingEndTime(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC))
        .pollingStartTime(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC))
        .basePath(new String[] {"test1", "test2"}).technology(EserviceTechnology.REST)
        .pollingFrequency(5).producerName("producer1").probingEnabled(true)
        .state(EserviceStateBE.ACTIVE).build();
    testEntityManager.persistAndFlush(eservice);
  }

  @DisplayName("Find e-service by correct eserviceId and versionId")
  @Test
  void testFindByEserviceIdAndVersionId_whenGivenCorrectEserviceIdAndVersionId_ReturnsEserviceEntity() {
    Optional<Eservice> queryResult =
        eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId);

    assertNotNull(queryResult.get(), "e-service object shouldn't be null");
  }

  @DisplayName("No e-service found with incorrect eserviceId and versionId")
  @Test
  void testFindByEserviceIdAndVersionId_whenGivenIncorrectEserviceIdAndVersionId_ReturnsNoEntity() {
    final UUID wrongEServiceId = UUID.randomUUID();
    final UUID wrongVersionId = UUID.randomUUID();
    Optional<Eservice> queryResult =
        eserviceRepository.findByEserviceIdAndVersionId(wrongEServiceId, wrongVersionId);

    assertThrows(NoSuchElementException.class, () -> {
      queryResult.get();
    }, "There should be no e-service with eserviceId " + wrongVersionId + "and versionId "
        + wrongVersionId);
  }
}
