package it.pagopa.interop.probing.eservice.operations.integration.repository.query.builder;

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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;
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
        .responseReceived(OffsetDateTime.parse("2023-03-21T00:00:05.995Z"))
        .lastRequest(OffsetDateTime.parse("2023-03-21T00:00:15.995Z"))
        .technology(EserviceTechnology.REST).basePath(new String[] {"base_path_test"})
        .eserviceRecordId(10L).build();
    testEntityManager.persistAndFlush(eserviceView);
  }

  @Test
  @DisplayName("given state n/d, service returns e-service view entity")
  void testFindAll_whenGivenStateND_ReturnsEserviceEntity() {
    Page<EserviceView> e = eserviceViewQueryBuilder.findAllWithNDState(1, 0, "e-service1",
        "producer1", null, List.of(EserviceMonitorState.N_D), 0);

    assertNotNull(e.getContent(), "e-service object shouldn't be null");
  }

  @Test
  @DisplayName("given state active, service returns e-service view entity")
  void testFindAll_whenGivenStateActive_ReturnsEserviceViewEntity() {
    Page<EserviceView> e = eserviceViewQueryBuilder.findAllWithoutNDState(1, 0, "e-service1",
        "producer1", null, List.of(EserviceMonitorState.ONLINE), 0);

    assertNotNull(e.getContent(), "e-service object shouldn't be null");
  }
}
