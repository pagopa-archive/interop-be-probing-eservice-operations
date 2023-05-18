package it.pagopa.interop.probing.eservice.operations.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import it.pagopa.interop.probing.eservice.operations.api.ProducersApi;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
import it.pagopa.interop.probing.eservice.operations.service.ProducerService;

@RestController
public class ProducerController implements ProducersApi {

  @Autowired
  private ProducerService producerService;

  @Override
  public ResponseEntity<SearchProducerNameResponse> getEservicesProducers(Integer limit,
      Integer offset, String producerName) {
    return ResponseEntity.ok(producerService.getEservicesProducers(limit, offset, producerName));
  }
}
