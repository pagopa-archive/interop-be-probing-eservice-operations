package it.pagopa.interop.probing.eservice.operations.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import java.time.OffsetDateTime;
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
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceContent;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStatus;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.dtos.MainDataEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.dtos.PollingEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.dtos.ProbingDataEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.SaveEserviceDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceLastRequestDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceResponseReceivedDto;
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.mapper.AbstractMapper;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.model.EserviceProbingRequest;
import it.pagopa.interop.probing.eservice.operations.model.EserviceProbingResponse;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceProbingRequestRepository;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceProbingResponseRepository;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceRepository;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceViewRepository;
import it.pagopa.interop.probing.eservice.operations.repository.query.builder.EserviceContentQueryBuilder;
import it.pagopa.interop.probing.eservice.operations.repository.query.builder.EserviceViewQueryBuilder;
import it.pagopa.interop.probing.eservice.operations.service.EserviceService;
import it.pagopa.interop.probing.eservice.operations.service.impl.EserviceServiceImpl;
import it.pagopa.interop.probing.eservice.operations.util.EnumUtilities;
import it.pagopa.interop.probing.eservice.operations.util.OffsetLimitPageable;
import it.pagopa.interop.probing.eservice.operations.util.logging.Logger;

@SpringBootTest
class EserviceServiceImplTest {

  @Mock
  EserviceRepository eserviceRepository;

  @Mock
  EserviceViewRepository eserviceViewRepository;

  @Mock
  EserviceViewQueryBuilder eserviceViewQueryBuilder;

  @Mock
  EserviceContentQueryBuilder eserviceContentQueryBuilder;

  @Mock
  EserviceProbingRequestRepository eserviceProbingRequestRepository;

  @Mock
  EserviceProbingResponseRepository eserviceProbingResponseRepository;

  @Mock
  EnumUtilities enumUtilities;

  @Spy
  AbstractMapper mapstructMapper;

  @Mock
  Logger logger;

  @InjectMocks
  EserviceService service = new EserviceServiceImpl();

  private final UUID eServiceId = UUID.randomUUID();
  private final UUID versionId = UUID.randomUUID();
  private final Long eserviceRecordId = 1L;
  private Eservice testService;

  private EserviceProbingRequest testEserviceProbingRequest;

  private UpdateEserviceStateDto updateEserviceStateDto;

  private UpdateEserviceProbingStateDto updateEserviceProbingStateDto;

  private UpdateEserviceFrequencyDto updateEserviceFrequencyDto;

  private UpdateEserviceLastRequestDto updateEserviceLastRequestDto;

  private UpdateEserviceResponseReceivedDto updateEserviceResponseReceivedDto;

  private SaveEserviceDto saveEserviceDto;

  private EserviceView eserviceView;

  private EserviceContent eserviceContent;

  private MainDataEserviceResponse mainDataEserviceResponse;

  private ProbingDataEserviceResponse probingDataEserviceResponse;

  private EserviceProbingResponse eserviceProbingResponse;

  @BeforeEach
  void setup() {
    testService = Eservice.builder().state(EserviceInteropState.ACTIVE).lockVersion(1)
        .eserviceRecordId(1L).probingEnabled(true).build();

    saveEserviceDto = SaveEserviceDto.builder().basePath(new String[] {"test-1"})
        .eserviceId(eServiceId).name("Eservice name test").producerName("Eservice producer test")
        .technology(EserviceTechnology.fromValue("REST")).versionId(versionId).versionNumber(1)
        .state(EserviceInteropState.fromValue("INACTIVE")).audience(new String[] {"test-1"})
        .build();

    updateEserviceStateDto = UpdateEserviceStateDto.builder().eserviceId(eServiceId)
        .versionId(versionId).newEServiceState(EserviceInteropState.fromValue("INACTIVE")).build();

    updateEserviceProbingStateDto = UpdateEserviceProbingStateDto.builder().probingEnabled(false)
        .eserviceId(eServiceId).versionId(versionId).build();

    updateEserviceFrequencyDto =
        UpdateEserviceFrequencyDto.builder().eserviceId(eServiceId).versionId(versionId)
            .newPollingFrequency(5).newPollingStartTime(OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC))
            .newPollingEndTime(OffsetTime.of(20, 0, 0, 0, ZoneOffset.UTC)).build();

    updateEserviceLastRequestDto =
        UpdateEserviceLastRequestDto.builder().eserviceRecordId(eserviceRecordId)
            .lastRequest(OffsetDateTime.of(2023, 5, 8, 10, 0, 0, 0, ZoneOffset.UTC)).build();

    updateEserviceResponseReceivedDto = UpdateEserviceResponseReceivedDto.builder()
        .eserviceRecordId(eserviceRecordId).status(EserviceStatus.OK)
        .responseReceived(OffsetDateTime.of(2023, 5, 8, 10, 0, 0, 0, ZoneOffset.UTC)).build();

    eserviceView = EserviceView.builder().basePath(new String[] {"test-1"}).eserviceId(eServiceId)
        .eserviceName("Eservice name test").producerName("Eservice producer test")
        .technology(EserviceTechnology.REST).versionId(versionId).versionNumber(1)
        .state(EserviceInteropState.INACTIVE)
        .lastRequest(OffsetDateTime.of(2023, 5, 8, 10, 0, 0, 0, ZoneOffset.UTC))
        .responseReceived(OffsetDateTime.of(2023, 5, 8, 10, 0, 0, 0, ZoneOffset.UTC))
        .probingEnabled(true).pollingFrequency(5)
        .pollingStartTime(OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC))
        .pollingEndTime(OffsetTime.of(20, 0, 0, 0, ZoneOffset.UTC)).build();

    eserviceContent = EserviceContent.builder().basePath(List.of("base-path")).eserviceRecordId(1L)
        .technology(EserviceTechnology.REST).audience(List.of("audience")).build();
    testEserviceProbingRequest = EserviceProbingRequest.builder().eserviceRecordId(eserviceRecordId)
        .lastRequest(OffsetDateTime.of(2023, 5, 8, 10, 0, 0, 0, ZoneOffset.UTC))
        .eservice(testService).build();

    mainDataEserviceResponse = MainDataEserviceResponse.builder().eserviceName("service1")
        .producerName("producer1").versionNumber(1).build();

    probingDataEserviceResponse = ProbingDataEserviceResponse.builder()
        .state(EserviceInteropState.INACTIVE).probingEnabled(true).pollingFrequency(5).build();

    eserviceProbingResponse = EserviceProbingResponse.builder().eserviceRecordId(eserviceRecordId)
        .responseStatus(EserviceStatus.OK)
        .responseReceived(OffsetDateTime.of(2023, 5, 8, 10, 0, 0, 0, ZoneOffset.UTC))
        .eservice(testService).build();

  }

  @Test
  @DisplayName("e-service correctly updated")
  void testSaveEservice_whenEserviceIsFoundGivenCorrectEserviceSaveRequest_thenEserviceIsUpdated() {
    Mockito.when(eserviceRepository.findByEserviceIdAndVersionId(eServiceId, versionId))
        .thenReturn(Optional.of(testService));
    Mockito.when(eserviceRepository.save(Mockito.any(Eservice.class))).thenReturn(testService);
    service.saveEservice(saveEserviceDto);
    assertEquals(testService.eserviceRecordId(), service.saveEservice(saveEserviceDto),
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
    assertEquals(testService.eserviceRecordId(), service.saveEservice(saveEserviceDto),
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
    assertEquals(EserviceInteropState.INACTIVE, testService.state(),
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
        .thenReturn(EserviceContent.builder().build());

    SearchEserviceResponse searchEserviceResponse =
        service.searchEservices(2, 0, "Eservice-Name", "Eservice-Producer-Name", 1, null);

    assertTrue(searchEserviceResponse.getContent().isEmpty());
  }

  @Test
  @DisplayName("given a list of all state values as parameter, service returns SearchEserviceResponse object with content not empty")
  void testSearchEservice_whenGivenValidSizeAndPageNumberAndStatusValues_thenReturnsSearchEserviceResponseWithContentNotEmpty() {
    Mockito
        .when(eserviceViewRepository.findAll(ArgumentMatchers.<Specification<EserviceView>>any(),
            ArgumentMatchers.any(OffsetLimitPageable.class)))
        .thenReturn(new PageImpl<EserviceView>(List.of(eserviceView)));

    Mockito.when(mapstructMapper.toSearchEserviceContent(Mockito.any()))
        .thenReturn(EserviceContent.builder().eserviceName(eserviceView.getEserviceName())
            .producerName(eserviceView.getProducerName())
            .basePath(List.of(eserviceView.getBasePath())).technology(eserviceView.getTechnology())
            .responseReceived(eserviceView.getResponseReceived()).state(eserviceView.getState())
            .build());

    Mockito.when(enumUtilities.convertListFromMonitorToPdnd(List.of(EserviceMonitorState.values())))
        .thenReturn(List.of(EserviceInteropState.ACTIVE.getValue(),
            EserviceInteropState.INACTIVE.getValue()));

    SearchEserviceResponse searchEserviceResponse = service.searchEservices(2, 0, "Eservice-Name",
        "Eservice-Producer-Name", 1, List.of(EserviceMonitorState.values()));

    assertTrue(eserviceView.getState().equals(EserviceInteropState.ACTIVE)
        ? searchEserviceResponse.getContent().get(0).getState().equals(EserviceInteropState.ACTIVE)
        : searchEserviceResponse.getContent().get(0).getState()
            .equals(EserviceInteropState.INACTIVE));
  }

  @Test
  @DisplayName("given status n/d as parameter, service returns SearchEserviceResponse object with content empty")
  void testSearchEservice_whenGivenValidSizeAndPageNumberAndStatusND_thenReturnsSearchEserviceResponseWithContentEmpty() {
    Mockito
        .when(eserviceViewQueryBuilder.findAllWithNDState(ArgumentMatchers.eq(2),
            ArgumentMatchers.eq(0), ArgumentMatchers.eq("Eservice-Name"),
            ArgumentMatchers.eq("Eservice-Producer-Name"), ArgumentMatchers.eq(1),
            ArgumentMatchers.eq(List.of()), ArgumentMatchers.anyInt()))
        .thenReturn(new PageImpl<EserviceView>(List.of()));

    Mockito.when(enumUtilities.convertListFromMonitorToPdnd(List.of(EserviceMonitorState.N_D)))
        .thenReturn(List.of());

    Mockito.when(mapstructMapper.toSearchEserviceContent(Mockito.any()))
        .thenReturn(EserviceContent.builder().build());

    SearchEserviceResponse searchEserviceResponse = service.searchEservices(2, 0, "Eservice-Name",
        "Eservice-Producer-Name", 1, List.of(EserviceMonitorState.N_D));

    assertTrue(searchEserviceResponse.getContent().isEmpty());
  }

  @Test
  @DisplayName("given status online as parameter, service returns SearchEserviceResponse object with content empty")
  void testSearchEservice_whenGivenValidSizeAndPageNumberAndStatusONLINE_thenReturnsSearchEserviceResponseWithContentEmpty() {
    Mockito
        .when(eserviceViewQueryBuilder.findAllWithoutNDState(ArgumentMatchers.eq(2),
            ArgumentMatchers.eq(0), ArgumentMatchers.eq("Eservice-Name"),
            ArgumentMatchers.eq("Eservice-Producer-Name"), ArgumentMatchers.eq(1),
            ArgumentMatchers.eq(List.of(EserviceInteropState.ACTIVE.getValue())),
            ArgumentMatchers.anyInt()))
        .thenReturn(new PageImpl<EserviceView>(List.of(eserviceView)));

    Mockito.when(enumUtilities.convertListFromMonitorToPdnd(List.of(EserviceMonitorState.ONLINE)))
        .thenReturn(List.of(EserviceInteropState.ACTIVE.getValue()));

    Mockito.when(mapstructMapper.toSearchEserviceContent(Mockito.any()))
        .thenReturn(EserviceContent.builder().eserviceName(eserviceView.getEserviceName())
            .producerName(eserviceView.getProducerName())
            .basePath(List.of(eserviceView.getBasePath())).technology(eserviceView.getTechnology())
            .responseReceived(eserviceView.getResponseReceived()).state(eserviceView.getState())
            .build());

    SearchEserviceResponse searchEserviceResponse = service.searchEservices(2, 0, "Eservice-Name",
        "Eservice-Producer-Name", 1, List.of(EserviceMonitorState.ONLINE));

    assertTrue(eserviceView.getState().equals(EserviceInteropState.ACTIVE)
        ? searchEserviceResponse.getContent().get(0).getState().equals(EserviceInteropState.ACTIVE)
        : searchEserviceResponse.getContent().get(0).getState()
            .equals(EserviceInteropState.INACTIVE));
  }

  @Test
  @DisplayName("service returns PollingEserviceResponse object with content not empty")
  void testGetEservicesReadyForPolling_whenGivenValidLimitAndOffset_thenReturnsPollingEserviceResponse() {
    List<EserviceContent> eserviceContentList = List.of(eserviceContent);
    Mockito.when(eserviceContentQueryBuilder.findAllEservicesReadyForPolling(2, 0))
        .thenReturn(new PageImpl<EserviceContent>(eserviceContentList));

    PollingEserviceResponse pollingEserviceResponseExpected =
        service.getEservicesReadyForPolling(2, 0);

    assertEquals(eserviceContentList, pollingEserviceResponseExpected.getContent());
    assertEquals(eserviceContentList.size(), pollingEserviceResponseExpected.getTotalElements());
  }

  @Test
  @DisplayName("e-service last request has correctly updated")
  void testUpdateLastRequest_whenEserviceIsFoundGivenCorrectEserviceRecordId_thenLastRequestIsUpdated()
      throws EserviceNotFoundException {
    Mockito.when(eserviceProbingRequestRepository.findById(eserviceRecordId))
        .thenReturn(Optional.of(testEserviceProbingRequest));
    Mockito.when(eserviceRepository.findById(eserviceRecordId)).thenReturn(Optional.empty());
    service.updateLastRequest(updateEserviceLastRequestDto);
    Mockito.when(eserviceProbingRequestRepository.save(Mockito.any(EserviceProbingRequest.class)))
        .thenReturn(testEserviceProbingRequest);
    assertEquals((OffsetDateTime.of(2023, 5, 8, 10, 0, 0, 0, ZoneOffset.UTC)),
        testEserviceProbingRequest.lastRequest());
  }

  @Test
  @DisplayName("eserviceProbingRequest has been created")
  void testUpdateLastRequest_whenEserviceIsFoundGivenCorrectEserviceRecordId_thenEserviceProbingRequestIsCreated()
      throws EserviceNotFoundException {
    Mockito.when(eserviceProbingRequestRepository.findById(eserviceRecordId))
        .thenReturn(Optional.empty());
    Mockito.when(eserviceRepository.findById(eserviceRecordId))
        .thenReturn(Optional.of(testService));
    Mockito.when(eserviceProbingRequestRepository.save(Mockito.any(EserviceProbingRequest.class)))
        .thenReturn(testEserviceProbingRequest);
    service.updateLastRequest(updateEserviceLastRequestDto);
    verify(eserviceProbingRequestRepository).save(Mockito.any(EserviceProbingRequest.class));

  }

  @Test
  @DisplayName("eService main data has been retrieved and MainDataEserviceResponse is created")
  void testGetEserviceMainData_whenEserviceIsFoundGivenCorrectEserviceRecordId_thenMainDataEserviceResponseIsCreated()
      throws EserviceNotFoundException {
    Mockito.when(eserviceRepository.findById(eserviceRecordId))
        .thenReturn(Optional.of(testService));
    mainDataEserviceResponse = service.getEserviceMainData(eserviceRecordId);
    assertEquals(mainDataEserviceResponse.getEserviceName(), testService.eserviceName());
  }

  @Test
  @DisplayName("e-service to obtain main data is not found")
  void testGetEserviceMainData_whenNoEServiceIsFound_thenThrowsException() {
    Mockito.when(eserviceRepository.findById(eserviceRecordId)).thenReturn(Optional.empty());
    assertThrows(EserviceNotFoundException.class,
        () -> service.getEserviceMainData(eserviceRecordId),
        "e-service should not be found and an EserviceNotFoundException should be thrown");
  }

  @Test
  @DisplayName("eService probing data has been retrieved and MainDataEserviceResponse is created")
  void testGetEserviceProbingData_whenEserviceIsFoundGivenCorrectEserviceRecordId_thenProbingDataEserviceResponseIsCreated()
      throws EserviceNotFoundException {
    Mockito.when(eserviceViewRepository.findById(eserviceRecordId))
        .thenReturn(Optional.of(eserviceView));
    Mockito.when(mapstructMapper.toProbingDataEserviceResponse(eserviceView))
        .thenReturn(probingDataEserviceResponse);
    probingDataEserviceResponse = service.getEserviceProbingData(eserviceRecordId);
    assertEquals(probingDataEserviceResponse.getState(), eserviceView.getState());
    assertEquals(probingDataEserviceResponse.getProbingEnabled(), eserviceView.isProbingEnabled());
  }

  @Test
  @DisplayName("e-service to obtain probing data is not found")
  void testGetEserviceProbingData_whenNoEServiceIsFound_thenThrowsException() {
    Mockito.when(eserviceViewRepository.findById(eserviceRecordId)).thenReturn(Optional.empty());
    assertThrows(EserviceNotFoundException.class,
        () -> service.getEserviceProbingData(eserviceRecordId),
        "e-service should not be found and an EserviceNotFoundException should be thrown");
  }

  @DisplayName("e-service reponse received has correctly updated")
  void testUpdateResponseReceived_whenEserviceIsFoundGivenCorrectEserviceRecordId_thenResponseReceivedIsUpdated()
      throws EserviceNotFoundException {
    Mockito.when(eserviceProbingResponseRepository.findById(eserviceRecordId))
        .thenReturn(Optional.of(eserviceProbingResponse));
    Mockito.when(eserviceProbingResponseRepository.save(Mockito.any(EserviceProbingResponse.class)))
        .thenReturn(eserviceProbingResponse);

    service.updateResponseReceived(updateEserviceResponseReceivedDto);

    assertEquals((OffsetDateTime.of(2023, 5, 8, 10, 0, 0, 0, ZoneOffset.UTC)),
        eserviceProbingResponse.responseReceived());
  }

  @Test
  @DisplayName("eservice probing response object has been created")
  void testUpdateResponseReceived_whenEserviceIsFoundGivenCorrectEserviceRecordId_thenEserviceProbingResponseIsCreated()
      throws EserviceNotFoundException {
    Mockito.when(eserviceProbingResponseRepository.findById(eserviceRecordId))
        .thenReturn(Optional.empty());
    Mockito.when(eserviceRepository.findById(eserviceRecordId))
        .thenReturn(Optional.of(testService));
    Mockito.when(eserviceProbingResponseRepository.save(Mockito.any(EserviceProbingResponse.class)))
        .thenReturn(eserviceProbingResponse);

    service.updateResponseReceived(updateEserviceResponseReceivedDto);
    verify(eserviceProbingResponseRepository).save(Mockito.any(EserviceProbingResponse.class));
  }
}
