package it.pagopa.interop.probing.eservice.operations.service;

import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceStateDto;

public interface EserviceService {

	/**
	 * Updates the state of the e-service identified by the input eserviceId and
	 * versioneId
	 * 
	 * @param inputData the input data DTO containing the e-service id, version id
	 *                  and the probing new state
	 * @throws EserviceNotFoundException if the e-service isn't found in the
	 *                                   database
	 */
	void updateEserviceState(UpdateEserviceStateDto inputData) throws EserviceNotFoundException;

	/**
	 * Updates the probing state of the e-service identified by the input eserviceId
	 * and versioneId
	 * 
	 * @param inputData the input data DTO containing the e-service id, version id
	 *                  and the probing enabling/disabling
	 * @throws EserviceNotFoundException if the e-service isn't found in the
	 *                                   database
	 */
	void updateEserviceProbingState(UpdateEserviceProbingStateDto inputData) throws EserviceNotFoundException;

	/**
	 * Updates the frequency, pollingStartTime and pollingStartTime of the e-service
	 * identified by the input eserviceId and versioneId
	 * 
	 * @param inputData the input data DTO containing the e-service id, version id,
	 *                  the new frequency and new time interval for polling
	 * @throws EserviceNotFoundException if the e-service isn't found in the
	 *                                   database
	 */
	void updateEserviceFrequency(UpdateEserviceFrequencyDto inputData) throws EserviceNotFoundException;

}
