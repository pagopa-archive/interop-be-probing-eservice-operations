/**************************************************************************
 *
 * Copyright 2023 (C) DXC
 *
 * Created on : Apr 26, 2023 Author : dxc technology Project Name:
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
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;
import it.pagopa.interop.probing.eservice.operations.dtos.MainDataEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.dtos.PollingEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.SaveEserviceDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceLastRequestDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceStateDto;

public interface EserviceService {

  /**
   * Updates the last request of the e-service identified by eserviceRecordId
   * 
   * @param inputData the input data DTO containing the eserviceRecordId
   * @throws EserviceNotFoundException if the e-service isn't found in the database
   */
  void updateLastRequest(UpdateEserviceLastRequestDto inputData) throws EserviceNotFoundException;

  /**
   * Saves the e-service or updates it if already exists
   *
   * @param inputData the input data DTO containing the e-services to save or update
   * @return e-service's id
   */
  Long saveEservice(SaveEserviceDto inputData);

  /**
   * Updates the state of the e-service identified by the input eserviceId and versionId
   *
   * @param inputData the input data DTO containing the e-service id, version id and the probing new
   *        state
   * @throws EserviceNotFoundException if the e-service isn't found in the database
   */
  void updateEserviceState(UpdateEserviceStateDto inputData) throws EserviceNotFoundException;

  /**
   * Updates the probing state of the e-service identified by the input eserviceId and versionId
   *
   * @param inputData the input data DTO containing the e-service id, version id and the probing
   *        enabling/disabling
   * @throws EserviceNotFoundException if the e-service isn't found in the database
   */
  void updateEserviceProbingState(UpdateEserviceProbingStateDto inputData)
      throws EserviceNotFoundException;

  /**
   * Updates the frequency, pollingStartTime and pollingStartTime of the e-service identified by the
   * input eserviceId and versionId
   *
   * @param inputData the input data DTO containing the e-service id, version id, the new frequency
   *        and new time interval for polling
   * @throws EserviceNotFoundException if the e-service isn't found in the database
   */
  void updateEserviceFrequency(UpdateEserviceFrequencyDto inputData)
      throws EserviceNotFoundException;

  /**
   * Retrive the eservices by input filter.
   *
   * @param limit the limit
   * @param offset the offset
   * @param eserviceName the eservice name
   * @param producerName the eservice producer name
   * @param versionNumber the version number
   * @param state the e service state
   * @return the SearchEserviceResponse which contain eserviceList and pagination parameter
   */
  public SearchEserviceResponse searchEservices(Integer limit, Integer offset, String eserviceName,
      String producerName, Integer versionNumber, List<EserviceMonitorState> state);

  /**
   * Gets the eservices ready for polling.
   *
   * @param limit the limit
   * @param offset the offset
   * @return the eservices ready for polling
   */
  public PollingEserviceResponse getEservicesReadyForPolling(Integer limit, Integer offset);


  /**
   * Get the main data of the selected service.
   *
   * @param eserviceRecordId the eservice record id
   * @return the eservice main data
   * @throws EserviceNotFoundException
   */
  public MainDataEserviceResponse getEserviceMainData(Long eserviceRecordId)
      throws EserviceNotFoundException;
}
