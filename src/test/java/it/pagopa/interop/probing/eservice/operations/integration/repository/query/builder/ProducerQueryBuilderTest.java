package it.pagopa.interop.probing.eservice.operations.integration.repository.query.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.dtos.Producer;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.repository.query.builder.ProducerQueryBuilder;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
    classes = Repository.class))
class ProducerQueryBuilderTest {
  @Autowired
  private ProducerQueryBuilder producerQueryBuilder;

  @Autowired
  private TestEntityManager testEntityManager;

  @BeforeEach
  void setUp() {
    Eservice eservice =
        Eservice.builder().eserviceId(UUID.randomUUID()).versionId(UUID.randomUUID())
            .eserviceName("e-service1").pollingEndTime(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC))
            .pollingStartTime(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC)).versionNumber(1)
            .basePath(new String[] {"test1", "test2"}).technology(EserviceTechnology.REST)
            .pollingFrequency(5).producerName("producer1").probingEnabled(true)
            .state(EserviceInteropState.ACTIVE).build();
    testEntityManager.persistAndFlush(eservice);
  }

  @Test
  @DisplayName("given valid producer name, a producer is found")
  void getEservicesProducers_givenValidProducerName_returnsProducerList() {
    List<Producer> producers = producerQueryBuilder.findAllProducersByProducerName(10, 0, "PROD");
    assertEquals(1, producers.size());
    assertEquals("producer1", producers.get(0).getProducerName());
  }

  @Test
  @DisplayName("given a random producer name, no producer is found")
  void getEservicesProducers_givenProducerName_returnsProducerList() {
    List<Producer> producers =
        producerQueryBuilder.findAllProducersByProducerName(10, 0, "Eservice name");
    assertEquals(0, producers.size(), "no producer with the given name was found");
  }
}
