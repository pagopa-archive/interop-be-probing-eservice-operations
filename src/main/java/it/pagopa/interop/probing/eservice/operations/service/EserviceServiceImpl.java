package it.pagopa.interop.probing.eservice.operations.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.mapper.MapStructMapper;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceRepository;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceViewRepository;
import it.pagopa.interop.probing.eservice.operations.repository.specs.EserviceViewSpecs;
import it.pagopa.interop.probing.eservice.operations.util.OffsetLimitPageable;
import it.pagopa.interop.probing.eservice.operations.util.constant.ErrorMessages;
import it.pagopa.interop.probing.eservice.operations.util.constant.ProjectConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class EserviceServiceImpl implements EserviceService {

	@Autowired
	EserviceRepository eserviceRepository;

	@Autowired
	EserviceViewRepository eserviceViewRepository;

	@Autowired
	MapStructMapper mapstructMapper;

	@Autowired
	Validator validator;

	@Override
	public void updateEserviceState(UpdateEserviceStateDto inputData) throws EserviceNotFoundException {
		Optional<Eservice> queryResult = eserviceRepository.findByEserviceIdAndVersionId(inputData.getEserviceId(),
				inputData.getVersionId());

		Eservice eServiceToUpdate = queryResult
				.orElseThrow(() -> new EserviceNotFoundException(ErrorMessages.ELEMENT_NOT_FOUND));

		eServiceToUpdate.setState(inputData.getNewEServiceState());
		eserviceRepository.save(eServiceToUpdate);

		log.info("EserviceState of eservice " + eServiceToUpdate.getEserviceId() + " with version "
				+ eServiceToUpdate.getVersionId() + " has been updated into " + eServiceToUpdate.getState());
	}

	@Override
	public void updateEserviceProbingState(UpdateEserviceProbingStateDto inputData) throws EserviceNotFoundException {

		Optional<Eservice> queryResult = eserviceRepository.findByEserviceIdAndVersionId(inputData.getEserviceId(),
				inputData.getVersionId());

		Eservice eServiceToUpdate = queryResult
				.orElseThrow(() -> new EserviceNotFoundException(ErrorMessages.ELEMENT_NOT_FOUND));

		eServiceToUpdate.setProbingEnabled(inputData.isProbingEnabled());
		eserviceRepository.save(eServiceToUpdate);

		log.info("EserviceProbingState of eservice " + eServiceToUpdate.getEserviceId() + " with version "
				+ eServiceToUpdate.getVersionId() + " has been updated into " + eServiceToUpdate.isProbingEnabled());
	}

	@Override
	public void updateEserviceFrequency(UpdateEserviceFrequencyDto inputData) throws EserviceNotFoundException {

		Optional<Eservice> queryResult = eserviceRepository.findByEserviceIdAndVersionId(inputData.getEserviceId(),
				inputData.getVersionId());

		Eservice eServiceToUpdate = queryResult
				.orElseThrow(() -> new EserviceNotFoundException(ErrorMessages.ELEMENT_NOT_FOUND));

		eServiceToUpdate.setPollingFrequency(inputData.getNewPollingFrequency());
		eServiceToUpdate.setPollingStartTime(inputData.getNewPollingStartTime());
		eServiceToUpdate.setPollingEndTime(inputData.getNewPollingEndTime());
		eserviceRepository.save(eServiceToUpdate);

		log.info("Eservice " + eServiceToUpdate.getEserviceId() + " with version " + eServiceToUpdate.getVersionId()
				+ " has been updated with startTime: " + eServiceToUpdate.getPollingStartTime() + " and endTime: "
				+ eServiceToUpdate.getPollingEndTime() + " and frequency: " + eServiceToUpdate.getPollingFrequency());
	}

	public SearchEserviceResponse searchEservices(Integer limit, Integer offset, String eserviceName,
			String eserviceProducerName, Integer versionNumber, List<EserviceState> eServiceState) {
		Page<EserviceView> eserviceList = eserviceViewRepository.findAll(
				EserviceViewSpecs.searchSpecBuilder(eserviceName, eserviceProducerName, versionNumber, eServiceState),
				new OffsetLimitPageable(offset, limit, Sort.by(ProjectConstants.ESERVICE_NAME_FIELD).ascending()));
		return SearchEserviceResponse.builder()
				.content(mapstructMapper.toSearchEserviceResponse(eserviceList.getContent()))
				.offset(eserviceList.getNumber()).limit(eserviceList.getSize())
				.totalElements(eserviceList.getTotalElements()).build();
	}

}
