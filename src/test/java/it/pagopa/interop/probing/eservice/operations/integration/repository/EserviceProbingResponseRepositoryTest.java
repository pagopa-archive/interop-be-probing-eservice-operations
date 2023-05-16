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
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStatus;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.model.EserviceProbingResponse;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceProbingResponseRepository;

@DataJpaTest
class EserviceProbingResponseRepositoryTest {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private EserviceProbingResponseRepository eserviceProbingResponseRepository;

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

    EserviceProbingResponse eserviceProbingResponse =
        EserviceProbingResponse.builder().eservice(eservice)
            .responseReceived(OffsetDateTime.of(2023, 5, 8, 10, 0, 0, 0, ZoneOffset.UTC))
            .responseStatus(EserviceStatus.OK).build();
    testEntityManager.persistAndFlush(eserviceProbingResponse);
  }

  @DisplayName("Find e-service probing response by valid eserviceRecordId")
  @Test
  void testFindByEserviceRecordId_whenGivenCorrectEserviceIdAndVersionId_ReturnsEserviceEntity() {
    Optional<EserviceProbingResponse> queryResult =
        eserviceProbingResponseRepository.findById(eservice.eserviceRecordId());

    assertNotNull(queryResult.get(), "e-service probing response object shouldn't be null");
  }

  @DisplayName("No e-service probing response found with wrong eserviceRecordId")
  @Test
  void testFindByEserviceRecordId_whenGivenEserviceRecordId_ReturnsNoEntity() {
    final Long wrongEserviceRecordId = 2L;
    Optional<EserviceProbingResponse> queryResult =
        eserviceProbingResponseRepository.findById(wrongEserviceRecordId);

    assertThrows(NoSuchElementException.class, () -> {
      queryResult.get();
    }, "There should be no e-service probing response with eserviceRecordId "
        + wrongEserviceRecordId);
  }
}
