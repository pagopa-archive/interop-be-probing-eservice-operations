package it.pagopa.interop.probing.eservice.operations.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.time.OffsetTime;
import java.time.ZoneOffset;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceViewDTO;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.SaveEserviceDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.mapper.MapperImpl;
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
	MapperImpl mapstructMapper;

	@InjectMocks
	EserviceService service = new EserviceServiceImpl();

	private final UUID eServiceId = UUID.randomUUID();
	private final UUID versionId = UUID.randomUUID();
	private Eservice testService;
	private UpdateEserviceStateDto updateEserviceStateDto;

	private UpdateEserviceProbingStateDto updateEserviceProbingStateDto;

	private UpdateEserviceFrequencyDto updateEserviceFrequencyDto;

	private SaveEserviceDto saveEserviceDto;

	private List<EserviceView> eservicesView;

	@BeforeEach
	void setup() {
		testService = Eservice.builder().state(EserviceState.ACTIVE).lockVersion(1).id(1L).build();

		saveEserviceDto = SaveEserviceDto.builder().basePath(new String[] {"test-1"})
				.eserviceId(eServiceId.toString()).name("Eservice name test").producerName("Eservice producer test")
				.technology(EserviceTechnology.fromValue("REST")).versionId(versionId.toString()).versionNumber("1")
				.state(EserviceState.fromValue("INACTIVE")).build();

		updateEserviceStateDto = UpdateEserviceStateDto.builder().eserviceId(eServiceId).versionId(versionId)
				.newEServiceState(EserviceState.fromValue("INACTIVE")).build();

		updateEserviceProbingStateDto = UpdateEserviceProbingStateDto.builder().probingEnabled(false)
				.eserviceId(eServiceId).versionId(versionId).build();

		updateEserviceFrequencyDto = UpdateEserviceFrequencyDto.builder().eserviceId(eServiceId).versionId(versionId)
				.newPollingFrequency(5).newPollingStartTime(OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC))
				.newPollingEndTime(OffsetTime.of(20, 0, 0, 0, ZoneOffset.UTC)).build();

		EserviceView eserviceView = EserviceView.builder().eserviceName("Eservice-Name")
				.producerName("Eservice-Producer-Name").versionNumber(1).state(EserviceState.ACTIVE).build();
		
		eservicesView = List.of(eserviceView);
	}

	@Test
	@DisplayName("e-service correctly updated")
	void testSaveEservice_whenEserviceIsFoundGivenCorrectEserviceSaveRequest_thenEserviceIsUpdated() {
		Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
				.thenReturn(Optional.of(testService));
		Mockito.when(eserviceRepository.save(Mockito.any(Eservice.class))).thenReturn(testService);
		service.saveEservice(saveEserviceDto);
		assertEquals(testService.getId(), service.saveEservice(saveEserviceDto), "e-service has been updated");
	}

	@Test
	@DisplayName("e-service to save when no eservice was found")
	void testSaveEservice_whenNoEserviceIsFoundGivenCorrectEserviceSaveRequest_thenEserviceIsSaved() {
		Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
				.thenReturn(Optional.empty());
		Mockito.when(eserviceRepository.save(Mockito.any(Eservice.class))).thenReturn(testService);
		service.saveEservice(saveEserviceDto);
		verify(eserviceRepository).save(Mockito.any(Eservice.class));
		assertEquals(testService.getId(), service.saveEservice(saveEserviceDto), "e-service has been saved");
	}

	@Test
	@DisplayName("e-service state correctly updated with new state")
	void testUpdateEserviceState_whenGivenCorrectEserviceIdAndVersionIdAndState_thenEserviceStateIsUpdated()
			throws EserviceNotFoundException {
		Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
				.thenReturn(Optional.of(testService));
		Mockito.when(eserviceRepository.save(Mockito.any(Eservice.class))).thenReturn(testService);
		service.updateEserviceState(updateEserviceStateDto);
		assertEquals(EserviceState.INACTIVE, testService.getState(), "e-service state should be INACTIVE");
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

		List<EserviceState> listEservice = List.of(EserviceState.INACTIVE);
		Mockito.when(eserviceViewRepository.findAll(ArgumentMatchers.<Specification<EserviceView>>any(),
				ArgumentMatchers.any(Pageable.class))).thenReturn(new PageImpl<EserviceView>(eservicesView));

		EserviceViewDTO eserviceViewDTO = EserviceViewDTO.builder().eserviceName("Eservice-Name")
				.producerName("Eservice-Producer-Name").versionNumber(1).state(EserviceState.ACTIVE).build();

		List<EserviceViewDTO> eservicesViewDTO = List.of(eserviceViewDTO);
		Mockito.when(mapstructMapper.toSearchEserviceResponse(Mockito.any())).thenReturn(eservicesViewDTO);

		SearchEserviceResponse searchEserviceResponse = service.searchEservices(2, 0, "Eservice-Name",
				"Eservice-Producer-Name", 1, listEservice);

		assertEquals(eservicesViewDTO.size(), searchEserviceResponse.getContent().size());
		assertTrue(searchEserviceResponse.getTotalElements() > 0);
	}
}
