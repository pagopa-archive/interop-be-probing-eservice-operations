package it.pagopa.interop.probing.eservice.operations.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.pagopa.interop.probing.eservice.operations.dtos.Producer;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
import it.pagopa.interop.probing.eservice.operations.repository.query.builder.ProducerQueryBuilder;
import it.pagopa.interop.probing.eservice.operations.service.ProducerService;
import it.pagopa.interop.probing.eservice.operations.util.logging.Logger;

@Service
public class ProducerServiceImpl implements ProducerService {

  @Autowired
  Logger logger;

  @Autowired
  private ProducerQueryBuilder producerQueryBuilder;

  @Override
  public SearchProducerNameResponse getEservicesProducers(Integer limit, Integer offset,
      String producerName) {
    logger.logMessageSearchProducer(producerName);
    List<Producer> producers =
        producerQueryBuilder.findAllProducersByProducerName(limit, offset, producerName);

    return SearchProducerNameResponse.builder()
        .content(producers.stream().map(Producer::getProducerName).toList()).build();
  }

}
