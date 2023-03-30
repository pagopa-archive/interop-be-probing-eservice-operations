package it.pagopa.interop.probing.eservice.operations.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceViewDTO;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.mapper.MapStructMapper;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceRepository;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceViewRepository;
import it.pagopa.interop.probing.eservice.operations.service.EserviceService;
import it.pagopa.interop.probing.eservice.operations.service.EserviceServiceImpl;

@SpringBootTest
class EserviceServiceImplTest {
	@Mock
	EserviceRepository eserviceRepository;

	@Mock
	EserviceViewRepository eserviceViewRepository;

	@Mock
	MapStructMapper mapstructMapper;

	@InjectMocks
	EserviceService service = new EserviceServiceImpl();

	private final UUID eServiceId = UUID.randomUUID();
	private final UUID versionId = UUID.randomUUID();
	private Eservice testService;
	private UpdateEserviceStateDto updateEserviceStateDto;

	private UpdateEserviceProbingStateDto updateEserviceProbingStateDto;

	private UpdateEserviceFrequencyDto updateEserviceFrequencyDto;

	private List<EserviceView> eservicesViewExpectedList;

	List<SearchProducerNameResponse> searchProducerNameResponseExpectedList;

	@BeforeEach
	void setup() {
		testService = new Eservice();
		testService.setState(EserviceState.ONLINE);
		updateEserviceStateDto = new UpdateEserviceStateDto();
		updateEserviceStateDto.setEserviceId(eServiceId);
		updateEserviceStateDto.setVersionId(versionId);
		updateEserviceStateDto.setNewEServiceState(EserviceState.fromValue("OFFLINE"));

		updateEserviceProbingStateDto = new UpdateEserviceProbingStateDto();
		updateEserviceProbingStateDto.setProbingEnabled(false);
		updateEserviceProbingStateDto.setEserviceId(eServiceId);
		updateEserviceProbingStateDto.setVersionId(versionId);

		updateEserviceFrequencyDto = new UpdateEserviceFrequencyDto();
		updateEserviceFrequencyDto.setEserviceId(eServiceId);
		updateEserviceFrequencyDto.setVersionId(versionId);
		updateEserviceFrequencyDto.setNewPollingFrequency(5);
		updateEserviceFrequencyDto.setNewPollingStartTime(OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC));
		updateEserviceFrequencyDto.setNewPollingEndTime(OffsetTime.of(20, 0, 0, 0, ZoneOffset.UTC));

		EserviceView eserviceView = new EserviceView();
		eserviceView.setEserviceName("Eservice-Name");
		eserviceView.setProducerName("Eservice-Producer-Name");
		eserviceView.setVersionNumber(1);
		eserviceView.setState(EserviceState.ONLINE);

		eservicesViewExpectedList = Arrays.asList(eserviceView);
	}

	@Test
	@DisplayName("e-service state correctly updated with new state")
	void testUpdateEserviceState_whenGivenCorrectEserviceIdAndVersionIdAndState_thenEserviceStateIsUpdated()
			throws EserviceNotFoundException {
		Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
				.thenReturn(Optional.of(testService));
		Mockito.when(eserviceRepository.save(Mockito.any(Eservice.class))).thenReturn(testService);
		service.updateEserviceState(updateEserviceStateDto);
		assertEquals(EserviceState.OFFLINE, testService.getState(), "e-service state should be INACTIVE");
	}

	@Test
	@DisplayName("e-service to update state of not found")
	void testUpdateEserviceState_whenNoEServiceIsFound_thenThrowsException() {
		Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
				.thenReturn(Optional.empty());
		assertThrows(EserviceNotFoundException.class, () -> service.updateEserviceState(updateEserviceStateDto),
				"e-service should not be found and an EserviceNotFoundException should be thrown");
	}

	@Test
	@DisplayName("e-service probing gets enabled")
	void testEserviceProbingState_whenGivenCorrectEserviceIdAndVersionId_thenEserviceProbingIsEnabled()
			throws EserviceNotFoundException {
		Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
				.thenReturn(Optional.of(testService));
		Mockito.when(eserviceRepository.save(Mockito.any(Eservice.class))).thenReturn(testService);
		updateEserviceProbingStateDto.setProbingEnabled(true);
		service.updateEserviceProbingState(updateEserviceProbingStateDto);
		assertTrue(testService.isProbingEnabled(), "e-service probing should be enabled");
	}

	@Test
	@DisplayName("e-service to update probing state of not found")
	void testEserviceProbingState_whenNoEServiceIsFound_thenThrowsException() {
		Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
				.thenReturn(Optional.empty());
		assertThrows(EserviceNotFoundException.class,
				() -> service.updateEserviceProbingState(updateEserviceProbingStateDto),
				"e-service should not be found and an EserviceNotFoundException should be thrown");
	}

	@Test
	@DisplayName("e-service frequency correctly updated with new state")
	void testUpdateEserviceFrequencyDto_whenGivenCorrectEserviceIdAndVersionIdAndState_thenEserviceStateIsUpdated()
			throws EserviceNotFoundException {
		Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
				.thenReturn(Optional.of(testService));
		Mockito.when(eserviceRepository.save(Mockito.any(Eservice.class))).thenReturn(testService);
		service.updateEserviceFrequency(updateEserviceFrequencyDto);
		assertEquals(5, testService.getPollingFrequency(), "e-service frequency should be 5");
	}

	@Test
	@DisplayName("e-service to update frequency of not found")
	void testUpdateEserviceFrequencyDto_whenNoEServiceIsFound_thenThrowsException() {
		Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
				.thenReturn(Optional.empty());
		assertThrows(EserviceNotFoundException.class, () -> service.updateEserviceFrequency(updateEserviceFrequencyDto),
				"e-service frequency should not be found and an EserviceNotFoundException should be thrown");
	}

	@Test
	@DisplayName("service returns SearchEserviceResponse object with content not empty")
	void testSearchEservice_whenGivenValidSizeAndPageNumber_thenReturnsSearchEserviceResponseWithContentNotEmpty() {

		Mockito.when(eserviceViewRepository.findAll(ArgumentMatchers.<Specification<EserviceView>>any(),
				ArgumentMatchers.any(Pageable.class)))
				.thenReturn(new PageImpl<EserviceView>(eservicesViewExpectedList));

		EserviceViewDTO expectedEserviceViewDTO = new EserviceViewDTO();
		expectedEserviceViewDTO.setEserviceName("Eservice-Name");
		expectedEserviceViewDTO.setProducerName("Eservice-Producer-Name");
		expectedEserviceViewDTO.setVersionNumber(1);
		expectedEserviceViewDTO.setState(EserviceState.OFFLINE);

		List<EserviceViewDTO> eservicesViewDTOExpectedList = Arrays.asList(expectedEserviceViewDTO);
		Mockito.when(mapstructMapper.toSearchEserviceResponse(Mockito.any())).thenReturn(eservicesViewDTOExpectedList);

		SearchEserviceResponse searchEserviceResponseResult = service.searchEservices(2, 0, "Eservice-Name",
				"Eservice-Producer-Name", 1, Arrays.asList(EserviceState.OFFLINE));

		assertEquals(eservicesViewDTOExpectedList.size(), searchEserviceResponseResult.getContent().size());
		assertTrue(searchEserviceResponseResult.getTotalElements() > 0);
	}

	@Test
	@DisplayName("when searching for a valid producer name, then return the list of producers")
	void testGetEservicesProducers_whenGivenValidProducerName_thenReturnsSearchProducerNameResponseList() {
		searchProducerNameResponseExpectedList = Arrays.asList(
				new SearchProducerNameResponse("ProducerName-Test-1", "ProducerName-Test-1"),
				new SearchProducerNameResponse("ProducerName-Test-2", "ProducerName-Test-2"));
		Mockito.when(
				eserviceViewRepository.getEservicesProducers("ProducerName-Test".toUpperCase(), PageRequest.of(0, 10)))
				.thenReturn(searchProducerNameResponseExpectedList);
		List<SearchProducerNameResponse> searchProducerNameResponseResultList = service
				.getEservicesProducers("ProducerName-Test");
		assertEquals(searchProducerNameResponseExpectedList.size(), searchProducerNameResponseResultList.size());
	}

	@Test
	@DisplayName("when searching for a producer name, then return an empty list")
	void testGetEservicesProducers_whenGivenValidProducerName_thenReturnsSearchProducerNameResponseListEmpty() {
		searchProducerNameResponseExpectedList = new ArrayList<>();
		Mockito.when(
				eserviceViewRepository.getEservicesProducers("ProducerName-Test".toUpperCase(), PageRequest.of(0, 10)))
				.thenReturn(searchProducerNameResponseExpectedList);
		List<SearchProducerNameResponse> searchProducerNameResponseResultList = service
				.getEservicesProducers("ProducerName-Test");
		assertEquals(searchProducerNameResponseExpectedList.size(), searchProducerNameResponseResultList.size());
	}
}
