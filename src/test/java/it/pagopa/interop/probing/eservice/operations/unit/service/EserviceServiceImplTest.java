package it.pagopa.interop.probing.eservice.operations.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.data.jpa.domain.Specification;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStateBE;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStateFE;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceContent;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
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
import it.pagopa.interop.probing.eservice.operations.service.impl.EserviceServiceImpl;
import it.pagopa.interop.probing.eservice.operations.util.EnumUtilities;
import it.pagopa.interop.probing.eservice.operations.util.OffsetLimitPageable;

@SpringBootTest
class EserviceServiceImplTest {
  @Mock
  EserviceRepository eserviceRepository;

  @Mock
  EserviceViewRepository eserviceViewRepository;

  @Mock
  EnumUtilities enumUtilities;

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

  List<SearchProducerNameResponse> searchProducerNameResponseExpectedList;

  @BeforeEach
  void setup() {
    testService = Eservice.builder().state(EserviceStateBE.ACTIVE).lockVersion(1).id(1L).build();

    saveEserviceDto = SaveEserviceDto.builder().basePath(new String[] {"test-1"})
        .eserviceId(eServiceId).name("Eservice name test").producerName("Eservice producer test")
        .technology(EserviceTechnology.fromValue("REST")).versionId(versionId).versionNumber(1)
        .state(EserviceStateBE.fromValue("INACTIVE")).build();

    updateEserviceStateDto = UpdateEserviceStateDto.builder().eserviceId(eServiceId)
        .versionId(versionId).newEServiceState(EserviceStateBE.fromValue("INACTIVE")).build();

    updateEserviceProbingStateDto = UpdateEserviceProbingStateDto.builder().probingEnabled(false)
        .eserviceId(eServiceId).versionId(versionId).build();

    updateEserviceFrequencyDto =
        UpdateEserviceFrequencyDto.builder().eserviceId(eServiceId).versionId(versionId)
            .newPollingFrequency(5).newPollingStartTime(OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC))
            .newPollingEndTime(OffsetTime.of(20, 0, 0, 0, ZoneOffset.UTC)).build();


  }

  @Test
  @DisplayName("e-service correctly updated")
  void testSaveEservice_whenEserviceIsFoundGivenCorrectEserviceSaveRequest_thenEserviceIsUpdated() {
    Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
        .thenReturn(Optional.of(testService));
    Mockito.when(eserviceRepository.save(Mockito.any(Eservice.class))).thenReturn(testService);
    service.saveEservice(saveEserviceDto);
    assertEquals(testService.id(), service.saveEservice(saveEserviceDto),
        "e-service has been updated");
  }

  @Test
  @DisplayName("e-service to save when no eservice was found")
  void testSaveEservice_whenNoEserviceIsFoundGivenCorrectEserviceSaveRequest_thenEserviceIsSaved() {
    Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
        .thenReturn(Optional.empty());
    Mockito.when(eserviceRepository.save(Mockito.any(Eservice.class))).thenReturn(testService);
    service.saveEservice(saveEserviceDto);
    verify(eserviceRepository).save(Mockito.any(Eservice.class));
    assertEquals(testService.id(), service.saveEservice(saveEserviceDto),
        "e-service has been saved");
  }

  @Test
  @DisplayName("e-service state correctly updated with new state")
  void testUpdateEserviceState_whenGivenCorrectEserviceIdAndVersionIdAndState_thenEserviceStateIsUpdated()
      throws EserviceNotFoundException {
    Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
        .thenReturn(Optional.of(testService));
    Mockito.when(eserviceRepository.save(Mockito.any(Eservice.class))).thenReturn(testService);
    service.updateEserviceState(updateEserviceStateDto);
    assertEquals(EserviceStateBE.INACTIVE, testService.state(),
        "e-service state should be INACTIVE");
  }

  @Test
  @DisplayName("e-service to update state of not found")
  void testUpdateEserviceState_whenNoEServiceIsFound_thenThrowsException() {
    Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
        .thenReturn(Optional.empty());
    assertThrows(EserviceNotFoundException.class,
        () -> service.updateEserviceState(updateEserviceStateDto),
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
    assertTrue(testService.probingEnabled(), "e-service probing should be enabled");
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
    assertEquals(5, testService.pollingFrequency(), "e-service frequency should be 5");
  }

  @Test
  @DisplayName("e-service to update frequency of not found")
  void testUpdateEserviceFrequencyDto_whenNoEServiceIsFound_thenThrowsException() {
    Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
        .thenReturn(Optional.empty());
    assertThrows(EserviceNotFoundException.class,
        () -> service.updateEserviceFrequency(updateEserviceFrequencyDto),
        "e-service frequency should not be found and an EserviceNotFoundException should be thrown");
  }

  @Test
  @DisplayName("service returns SearchEserviceResponse object with content empty")
  void testSearchEservice_whenGivenValidSizeAndPageNumber_thenReturnsSearchEserviceResponseWithContentEmpty() {
    Mockito
        .when(eserviceViewRepository.findAll(ArgumentMatchers.<Specification<EserviceView>>any(),
            ArgumentMatchers.any(OffsetLimitPageable.class)))
        .thenReturn(new PageImpl<EserviceView>(List.of()));

    Mockito.when(mapstructMapper.toSearchEserviceContent(Mockito.any()))
        .thenReturn(SearchEserviceContent.builder().build());

    SearchEserviceResponse searchEserviceResponse =
        service.searchEservices(2, 0, "Eservice-Name", "Eservice-Producer-Name", 1, null);

    assertTrue(searchEserviceResponse.getContent().isEmpty());
  }

  @Test
  @DisplayName("given status n/d as parameter, service returns SearchEserviceResponse object with content empty")
  void testSearchEservice_whenGivenValidSizeAndPageNumberAndStatusND_thenReturnsSearchEserviceResponseWithContentEmpty() {
    Mockito
        .when(eserviceViewRepository.findAllWithNDState(eq("Eservice-Name"),
            eq("Eservice-Producer-Name"), eq(1), eq(List.of(EserviceStateBE.INACTIVE.getValue())),
            ArgumentMatchers.anyInt(), ArgumentMatchers.any(OffsetLimitPageable.class)))
        .thenReturn(new PageImpl<EserviceView>(List.of()));

    Mockito.when(enumUtilities.convertListFromFEtoBE(ArgumentMatchers.any()))
        .thenReturn(List.of(EserviceStateBE.INACTIVE.getValue()));

    Mockito.when(mapstructMapper.toSearchEserviceContent(Mockito.any()))
        .thenReturn(SearchEserviceContent.builder().build());

    SearchEserviceResponse searchEserviceResponse = service.searchEservices(2, 0, "Eservice-Name",
        "Eservice-Producer-Name", 1, List.of(EserviceStateFE.N_D));

    assertTrue(searchEserviceResponse.getContent().isEmpty());
  }

  @Test
  @DisplayName("given status online as parameter, service returns SearchEserviceResponse object with content empty")
  void testSearchEservice_whenGivenValidSizeAndPageNumberAndStatusONLINE_thenReturnsSearchEserviceResponseWithContentEmpty() {
    Mockito
        .when(eserviceViewRepository.findAllWithoutNDState(eq("Eservice-Name"),
            eq("Eservice-Producer-Name"), eq(1), eq(List.of(EserviceStateBE.ACTIVE.getValue())),
            Mockito.anyInt(), ArgumentMatchers.any(OffsetLimitPageable.class)))
        .thenReturn(new PageImpl<EserviceView>(List.of()));

    Mockito.when(mapstructMapper.toSearchEserviceContent(Mockito.any()))
        .thenReturn(SearchEserviceContent.builder().build());

    Mockito.when(enumUtilities.convertListFromFEtoBE(ArgumentMatchers.any()))
        .thenReturn(List.of(EserviceStateBE.ACTIVE.getValue()));

    SearchEserviceResponse searchEserviceResponse = service.searchEservices(2, 0, "Eservice-Name",
        "Eservice-Producer-Name", 1, List.of(EserviceStateFE.ONLINE));

    assertTrue(searchEserviceResponse.getContent().isEmpty());
  }

  @Test
  @DisplayName("when searching for a valid producer name, then return the list of producers")
  void testGetEservicesProducers_whenGivenValidProducerName_thenReturnsSearchProducerNameResponseList() {
    searchProducerNameResponseExpectedList =
        List.of(new SearchProducerNameResponse("ProducerName-Test-1", "ProducerName-Test-1"),
            new SearchProducerNameResponse("ProducerName-Test-2", "ProducerName-Test-2"));
    Mockito
        .when(eserviceViewRepository.getEservicesProducers(
            ArgumentMatchers.eq("ProducerName-Test".toUpperCase()),
            ArgumentMatchers.any(OffsetLimitPageable.class)))
        .thenReturn(searchProducerNameResponseExpectedList);

    List<SearchProducerNameResponse> searchProducerNameResponseResultList =
        service.getEservicesProducers("ProducerName-Test");

    assertEquals(searchProducerNameResponseExpectedList.size(),
        searchProducerNameResponseResultList.size());
  }

  @Test
  @DisplayName("when searching for a producer name, then return an empty list")
  void testGetEservicesProducers_whenGivenValidProducerName_thenReturnsSearchProducerNameResponseListEmpty() {
    Mockito.when(eserviceViewRepository.getEservicesProducers(
        ArgumentMatchers.eq("ProducerName-Test".toUpperCase()),
        ArgumentMatchers.any(OffsetLimitPageable.class))).thenReturn(List.of());
    List<SearchProducerNameResponse> searchProducerNameResponseResultList =
        service.getEservicesProducers("ProducerName-Test");
    assertEquals(0, searchProducerNameResponseResultList.size());
  }
}
