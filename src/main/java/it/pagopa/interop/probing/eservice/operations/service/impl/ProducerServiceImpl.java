package it.pagopa.interop.probing.eservice.operations.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceRepository;
import it.pagopa.interop.probing.eservice.operations.service.ProducerService;
import it.pagopa.interop.probing.eservice.operations.util.OffsetLimitPageable;
import it.pagopa.interop.probing.eservice.operations.util.constant.ProjectConstants;

@Service
public class ProducerServiceImpl implements ProducerService {

  @Autowired
  EserviceRepository eserviceRepository;

  @Override
  public List<SearchProducerNameResponse> getEservicesProducers(String producerName) {
    return eserviceRepository.getEservicesProducers(producerName.toUpperCase(),
        new OffsetLimitPageable(0, 10, Sort.by(ProjectConstants.PRODUCER_NAME_FIELD).ascending()));
  }

}
