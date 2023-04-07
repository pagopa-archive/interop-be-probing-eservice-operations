package it.pagopa.interop.probing.eservice.operations.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import it.pagopa.interop.probing.eservice.operations.api.ProducersApi;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
import it.pagopa.interop.probing.eservice.operations.service.EserviceService;

@RestController
public class ProducerController implements ProducersApi {

  @Autowired
  EserviceService eserviceService;

  @Override
  public ResponseEntity<List<SearchProducerNameResponse>> getEservicesProducers(
      String producerName) {
    return ResponseEntity.ok(eserviceService.getEservicesProducers(producerName));
  }
}
