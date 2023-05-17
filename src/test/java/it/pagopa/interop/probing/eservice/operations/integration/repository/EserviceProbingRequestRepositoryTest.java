package it.pagopa.interop.probing.eservice.operations.integration.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.OffsetDateTime;
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
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.model.EserviceProbingRequest;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceProbingRequestRepository;

@DataJpaTest
class EserviceProbingRequestRepositoryTest {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private EserviceProbingRequestRepository eserviceProbingRequestRepository;

  private final UUID eServiceId = UUID.randomUUID();
  private final UUID versionId = UUID.randomUUID();

  private Eservice eservice;

  @BeforeEach
  void setup() {
    eservice = Eservice.builder().eserviceId(eServiceId).versionId(versionId)
        .eserviceName("e-service1").pollingEndTime(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC))
        .pollingStartTime(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC))
        .basePath(new String[] {"test1", "test2"}).technology(EserviceTechnology.REST)
        .pollingFrequency(5).producerName("producer1").probingEnabled(true)
        .state(EserviceInteropState.ACTIVE).versionNumber(1).build();

    EserviceProbingRequest eserviceProbingRequest =
        EserviceProbingRequest.builder().eservice(eservice)
            .lastRequest(OffsetDateTime.of(2023, 5, 8, 10, 0, 0, 0, ZoneOffset.UTC)).build();
    testEntityManager.persistAndFlush(eserviceProbingRequest);
  }

  @DisplayName("Find e-service probing request object by valid eserviceRecordId")
  @Test
  void testFindByEserviceRecordId_whenGivenCorrectEserviceIdAndVersionId_ReturnsEserviceEntity() {
    Optional<EserviceProbingRequest> queryResult =
        eserviceProbingRequestRepository.findById(eservice.eserviceRecordId());

    assertNotNull(queryResult.get(), "e-service probing request object shouldn't be null");
  }

  @DisplayName("No e-service probing request object found with wrong eserviceRecordId")
  @Test
  void testFindByEserviceRecordId_whenGivenEserviceRecordId_ReturnsNoEntity() {
    final Long wrongEserviceRecordId = 2L;
    Optional<EserviceProbingRequest> queryResult =
        eserviceProbingRequestRepository.findById(wrongEserviceRecordId);

    assertThrows(NoSuchElementException.class, () -> queryResult.get(),
        "There should be no e-service probing request object with eserviceRecordId "
            + wrongEserviceRecordId);
  }
}
