package it.pagopa.interop.probing.eservice.operations.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import it.pagopa.interop.probing.eservice.operations.dtos.Producer;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.repository.query.builder.ProducerQueryBuilder;
import it.pagopa.interop.probing.eservice.operations.service.ProducerService;
import it.pagopa.interop.probing.eservice.operations.service.impl.ProducerServiceImpl;
import it.pagopa.interop.probing.eservice.operations.util.logging.Logger;

@SpringBootTest
class ProducerServiceImplTest {

  @Mock
  Logger logger;

  @Mock
  ProducerQueryBuilder producerQueryBuilder;

  @InjectMocks
  ProducerService service = new ProducerServiceImpl();

  List<Producer> producerInput;

  private String producerNameInput = "producer name";

  @BeforeEach
  void setup() {
    producerInput =
        List.of(Producer.builder().label("producer name").value("producer name").build());
  }

  @Test
  @DisplayName("given producerName as parameter, service returns list of producers")
  void testGetEservicesProducers_whenProdcerNameAsParameter_thenReturnsListProducers()
      throws EserviceNotFoundException {
    Mockito.when(producerQueryBuilder.findAllProducersByProducerName(producerNameInput))
        .thenReturn(producerInput);

    List<Producer> producers = service.getEservicesProducers(producerNameInput);

    assertEquals(producerInput, producers);
  }
}
