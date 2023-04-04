/**************************************************************************
*
* Copyright 2023 (C) DXC
*
* Created on  : Mar 28, 2023
* Author      : dxc technology
* Project Name: interop-be-probing-eservice-operations 
* Package     : it.pagopa.interop.probing.eservice.operations.service
* File Name   : EserviceService.java
*
*-----------------------------------------------------------------------------
* Revision History (Release )
*-----------------------------------------------------------------------------
* VERSION     DESCRIPTION OF CHANGE
*-----------------------------------------------------------------------------
** --/1.0  |  Initial Create.
**---------|------------------------------------------------------------------
***************************************************************************/
package it.pagopa.interop.probing.eservice.operations.service;

import java.util.List;

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStateFE;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceStateDto;

public interface EserviceService {

	/**
	 * Updates the state of the e-service identified by the input eserviceId and
	 * versionId
	 * 
	 * @param inputData the input data DTO containing the e-service id, version id
	 *                  and the probing new state
	 * @throws EserviceNotFoundException if the e-service isn't found in the
	 *                                   database
	 */
	void updateEserviceState(UpdateEserviceStateDto inputData) throws EserviceNotFoundException;

	/**
	 * Updates the probing state of the e-service identified by the input eserviceId
	 * and versionId
	 * 
	 * @param inputData the input data DTO containing the e-service id, version id
	 *                  and the probing enabling/disabling
	 * @throws EserviceNotFoundException if the e-service isn't found in the
	 *                                   database
	 */
	void updateEserviceProbingState(UpdateEserviceProbingStateDto inputData) throws EserviceNotFoundException;

	/**
	 * Updates the frequency, pollingStartTime and pollingStartTime of the e-service
	 * identified by the input eserviceId and versionId
	 * 
	 * @param inputData the input data DTO containing the e-service id, version id,
	 *                  the new frequency and new time interval for polling
	 * @throws EserviceNotFoundException if the e-service isn't found in the
	 *                                   database
	 */
	void updateEserviceFrequency(UpdateEserviceFrequencyDto inputData) throws EserviceNotFoundException;

	/**
	 * Retrive the eservices by input filter.
	 *
	 * @param limit                the limit
	 * @param offset               the offset
	 * @param eserviceName         the eservice name
	 * @param producerName the eservice producer name
	 * @param versionNumber        the version number
	 * @param state        the e service state
	 * @return the SearchEserviceResponse which contain eserviceList and pagination
	 *         parameter
	 */
	public SearchEserviceResponse searchEservices(Integer limit, Integer offset, String eserviceName,
			String producerName, Integer versionNumber, List<EserviceStateFE> state);

	/**
	 * Get the list of eservices producers.
	 *
	 * @param producerName the eservice producer name
	 * @return the eservices producers
	 */
	List<SearchProducerNameResponse> getEservicesProducers(String producerName);
}
