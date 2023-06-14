package it.pagopa.interop.probing.eservice.operations.integration.repository.query.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStatus;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.repository.query.builder.EserviceViewQueryBuilder;


@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
    classes = Repository.class))
class EserviceViewQueryBuilderTest {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private EserviceViewQueryBuilder eserviceViewQueryBuilder;

  @BeforeEach
  void setup() {
    EserviceView eserviceView = EserviceView.builder().eserviceId(UUID.randomUUID())
        .versionId(UUID.randomUUID()).eserviceName("e-service Name").producerName("Producer Name")
        .pollingEndTime(OffsetTime.of(23, 59, 0, 0, ZoneOffset.UTC))
        .pollingStartTime(OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC)).pollingFrequency(5)
        .probingEnabled(true).versionNumber(1).state(EserviceInteropState.ACTIVE)
        .responseReceived(OffsetDateTime.parse("2023-03-21T00:00:15.995Z"))
        .responseStatus(EserviceStatus.OK)
        .lastRequest(OffsetDateTime.parse("2023-03-21T00:00:05.995Z"))
        .technology(EserviceTechnology.REST).basePath(new String[] {"base_path_test"})
        .eserviceRecordId(10L).build();
    testEntityManager.persistAndFlush(eserviceView);
  }

  @Test
  @DisplayName("given state n/d, service does not find any e-service view entity")
  void testFindAll_whenGivenStateND_ReturnsEserviceEntity() {
    List<EserviceView> e = eserviceViewQueryBuilder.findAllWithNDState(1, 0, "e-service1",
        "producer1", null, List.of(EserviceMonitorState.N_D), 0);

    assertNotNull(e, "e-service object shouldn't be null");
    assertEquals(0, e.size(), "no e-service is found with the given state");
  }

  @Test
  @DisplayName("no e-service is found with the given state")
  void testCount_whenGivenStateND_ReturnsEserviceEntity() {
    Long totalCount = eserviceViewQueryBuilder.getTotalCountWithNDState("e-service1", "producer1",
        null, List.of(EserviceMonitorState.N_D), 0);

    assertEquals(0, totalCount, "service returns an empty content");
  }

  @Test
  @DisplayName("given state active, service returns e-service view entity")
  void testFindAll_whenGivenStateActive_ReturnsEserviceViewEntity() {
    List<EserviceView> e = eserviceViewQueryBuilder.findAllWithoutNDState(1, 0, "e-service Name",
        "Producer Name", null, List.of(EserviceMonitorState.ONLINE), 3);

    assertNotNull(e, "e-service object shouldn't be null");
    assertEquals(1, e.size(), "an e-service is found with the given state");
  }

  @Test
  @DisplayName("given state active, an e-service is found")
  void testCount_whenGivenStateOnline_ReturnsEserviceEntity() {
    Long e = eserviceViewQueryBuilder.getTotalCountWithoutNDState("e-service Name", "Producer Name",
        1, List.of(EserviceMonitorState.ONLINE), 3);

    assertEquals(1L, e, "service returns a content not empty");
  }
}
