package it.pagopa.interop.probing.eservice.operations.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import it.pagopa.interop.probing.eservice.operations.api.ProducersApi;
import it.pagopa.interop.probing.eservice.operations.dtos.ProducerResponse;
import it.pagopa.interop.probing.eservice.operations.service.ProducerService;

@RestController
public class ProducerController implements ProducersApi {

  @Autowired
  ProducerService producerService;

  @Override
  public ResponseEntity<List<ProducerResponse>> getEservicesProducers(String producerName) {
    return ResponseEntity.ok(producerService.getEservicesProducers(producerName));
  }
}
