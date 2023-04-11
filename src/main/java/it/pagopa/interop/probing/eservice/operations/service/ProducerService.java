/**************************************************************************
 *
 * Copyright 2023 (C) DXC
 *
 * Created on : Mar 28, 2023 Author : dxc technology Project Name:
 * interop-be-probing-eservice-operations Package :
 * it.pagopa.interop.probing.eservice.operations.service File Name : EserviceService.java
 *
 * ----------------------------------------------------------------------------- Revision History
 * (Release ) ----------------------------------------------------------------------------- VERSION
 * DESCRIPTION OF CHANGE
 * ----------------------------------------------------------------------------- --/1.0 | Initial
 * Create. ---------|------------------------------------------------------------------
 ***************************************************************************/
package it.pagopa.interop.probing.eservice.operations.service;

import java.util.List;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;

public interface ProducerService {

  /**
   * Get the list of eservices producers.
   *
   * @param producerName the eservice producer name
   * @return the eservices producers
   */
  List<SearchProducerNameResponse> getEservicesProducers(String producerName);
}
