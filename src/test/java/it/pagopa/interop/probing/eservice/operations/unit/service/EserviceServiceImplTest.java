package it.pagopa.interop.probing.eservice.operations.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.mapper.MapStructMapper;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceRepository;
import it.pagopa.interop.probing.eservice.operations.service.EserviceService;
import it.pagopa.interop.probing.eservice.operations.service.EserviceServiceImpl;

@SpringBootTest
 class EserviceServiceImplTest {
    @Mock
    EserviceRepository eserviceRepository;
	
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
    
    @BeforeEach
    void setup(){
        testService = new Eservice();
        testService.setState(EserviceState.ACTIVE);
        updateEserviceStateDto = new UpdateEserviceStateDto();
        updateEserviceStateDto.setEserviceId(eServiceId);
        updateEserviceStateDto.setVersionId(versionId);
        updateEserviceStateDto.setNewEServiceState(EserviceState.fromValue("INACTIVE"));

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
    }

    @Test
    @DisplayName("e-service state correctly updated with new state")
    void testUpdateEserviceState_whenGivenCorrectEserviceIdAndVersionIdAndState_thenEserviceStateIsUpdated() throws EserviceNotFoundException {
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
    void testEserviceProbingState_whenGivenCorrectEserviceIdAndVersionId_thenEserviceProbingIsEnabled() throws EserviceNotFoundException {
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
}
