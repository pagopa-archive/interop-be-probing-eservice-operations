package it.pagopa.interop.probing.eservice.operations.unit.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeEserviceStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeLastRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingFrequencyRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceContent;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceSaveRequest;
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
import it.pagopa.interop.probing.eservice.operations.mapping.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.mapping.mapper.AbstractMapper;
import it.pagopa.interop.probing.eservice.operations.service.EserviceService;

@SpringBootTest
@AutoConfigureMockMvc
class EserviceControllerTest {
  @Value("${api.updateEserviceState.url}")
  private String updateEserviceStateUrl;

  @Value("${api.updateProbingState.url}")
  private String updateProbingStateUrl;

  @Value("${api.updateEserviceFrequency.url}")
  private String updateEserviceFrequencyUrl;

  @Value("${api.searchEservice.url}")
  private String apiSearchEserviceUrl;

  @Value("${api.saveEservice.url}")
  private String saveEserviceUrl;

  @Value("${api.eservicesActive.url}")
  private String apiGetEservicesActiveUrl;

  @Value("${api.updateLastRequest.url}")
  private String apiUpdateLastRequestUrl;

  @Value("${api.mainDataEservice.url}")
  private String apiGetMainDataEserviceUrl;

  @Value("${api.probingDataEservice.url}")
  private String apiGetProbingDataEserviceUrl;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  AbstractMapper mapstructMapper;

  @MockBean
  private EserviceService service;

  private EserviceSaveRequest eserviceSaveRequest;

  private ChangeEserviceStateRequest changeEserviceStateRequest;

  private ChangeProbingStateRequest changeProbingStateRequest;

  private ChangeProbingFrequencyRequest changeProbingFrequencyRequest;

  private ChangeLastRequest changeEserviceLastRequest;

  private UpdateEserviceStateDto updateEserviceStateDto;

  private UpdateEserviceProbingStateDto updateEserviceProbingStateDto;

  private UpdateEserviceFrequencyDto updateEserviceFrequencyDto;

  private UpdateEserviceLastRequestDto updateEserviceLastRequestDto;

  private SaveEserviceDto saveEserviceDto;

  private SearchEserviceResponse expectedSearchEserviceResponse;

  private PollingEserviceResponse pollingActiveEserviceResponse;

  private MainDataEserviceResponse mainDataEserviceResponse;

  private ProbingDataEserviceResponse probingDataEserviceResponse;

  private final UUID eServiceId = UUID.randomUUID();
  private final UUID versionId = UUID.randomUUID();
  private final Long eservicesRecordId = 1L;

  @BeforeEach
  void setup() {
    changeEserviceStateRequest =
        ChangeEserviceStateRequest.builder().eServiceState(EserviceInteropState.INACTIVE).build();

    updateEserviceStateDto =
        UpdateEserviceStateDto.builder().eserviceId(eServiceId).versionId(versionId)
            .newEServiceState(changeEserviceStateRequest.geteServiceState()).build();

    changeProbingStateRequest = ChangeProbingStateRequest.builder().probingEnabled(true).build();

    updateEserviceProbingStateDto = UpdateEserviceProbingStateDto.builder().eserviceId(eServiceId)
        .versionId(versionId).probingEnabled(changeProbingStateRequest.getProbingEnabled()).build();

    changeProbingFrequencyRequest = ChangeProbingFrequencyRequest.builder().frequency(5)
        .startTime(OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC))
        .endTime(OffsetTime.of(20, 0, 0, 0, ZoneOffset.UTC)).build();

    updateEserviceFrequencyDto = UpdateEserviceFrequencyDto.builder().eserviceId(eServiceId)
        .versionId(versionId).newPollingFrequency(changeProbingFrequencyRequest.getFrequency())
        .newPollingStartTime(changeProbingFrequencyRequest.getStartTime())
        .newPollingEndTime(changeProbingFrequencyRequest.getEndTime()).build();

    changeEserviceLastRequest = ChangeLastRequest.builder()
        .lastRequest(OffsetDateTime.of(2023, 5, 8, 10, 0, 0, 0, ZoneOffset.UTC)).build();

    updateEserviceLastRequestDto =
        UpdateEserviceLastRequestDto.builder().eserviceRecordId(eservicesRecordId)
            .lastRequest(OffsetDateTime.of(2023, 5, 8, 10, 0, 0, 0, ZoneOffset.UTC)).build();

    saveEserviceDto = SaveEserviceDto.builder().basePath(new String[] {"test-1"})
        .eserviceId(eServiceId).name("Eservice name test").producerName("Eservice producer test")
        .technology(EserviceTechnology.fromValue("REST")).versionId(versionId).versionNumber(1)
        .state(EserviceInteropState.fromValue("INACTIVE")).build();

    eserviceSaveRequest =
        EserviceSaveRequest.builder().basePath(List.of("test-1")).name("Eservice name test")
            .producerName("Eservice producer test").technology(EserviceTechnology.fromValue("REST"))
            .versionNumber(1).state(EserviceInteropState.INACTIVE).build();

    expectedSearchEserviceResponse = SearchEserviceResponse.builder().limit(2).offset(0).build();

    EserviceContent eserviceViewDTO =
        EserviceContent.builder().eserviceName("Eservice-Name").versionNumber(1)
            .producerName("Eservice-Producer-Name").state(EserviceInteropState.ACTIVE).build();

    List<EserviceContent> eservices = List.of(eserviceViewDTO);
    expectedSearchEserviceResponse.setContent(eservices);

    pollingActiveEserviceResponse = PollingEserviceResponse.builder()
        .content(List.of(EserviceContent.builder().basePath(List.of("base_path_test"))
            .eserviceRecordId(1L).technology(EserviceTechnology.REST).build()))
        .totalElements(1L).build();

    mainDataEserviceResponse = MainDataEserviceResponse.builder().eserviceName("service1")
        .producerName("producer1").versionNumber(1).build();

    probingDataEserviceResponse = ProbingDataEserviceResponse.builder()
        .state(EserviceInteropState.ACTIVE).probingEnabled(true).pollingFrequency(5).build();
  }

  @Test
  @DisplayName("e-service state gets saved")
  void testSaveService_whenGivenValidEserviceSaveRequest_thenReturnsId() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.put(String.format(saveEserviceUrl, eServiceId, versionId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(eserviceSaveRequest));
    Mockito.when(service.saveEservice(saveEserviceDto)).thenReturn(1L);
    MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).contains("1");
  }

  @Test
  @DisplayName("e-service state gets updated")
  void testUpdateEserviceState_whenGivenValidEServiceIdAndVersionId_thenEServiceStateIsUpdated()
      throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post(String.format(updateEserviceStateUrl, eServiceId, versionId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(changeEserviceStateRequest));
    Mockito.doNothing().when(service).updateEserviceState(updateEserviceStateDto);
    mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("e-service state can't be updated because e-service does not exist")
  void testUpdateEserviceState_whenEserviceDoesNotExist_thenThrows404Exception() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post(String.format(updateEserviceStateUrl, eServiceId, versionId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(changeEserviceStateRequest));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .updateEserviceState(updateEserviceStateDto);
    mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("e-service state can't be updated because e-service id request parameter is missing")
  void testUpdateEserviceState_whenEserviceIdParameterIsMissing_thenThrows404Exception()
      throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/eservices/versions/" + versionId + "/updateState")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(changeEserviceStateRequest));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .updateEserviceState(updateEserviceStateDto);
    mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("e-service state can't be updated because e-service versione id request parameter ismissing")
  void testUpdateEserviceState_whenVersionIdParameterIsMissing_thenThrows404Exception()
      throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/eservices/" + eServiceId + "/versions/updateState")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(changeEserviceStateRequest));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .updateEserviceState(updateEserviceStateDto);
    mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("e-service state can't be updated because request body is missing")
  void testUpdateEserviceState_whenRequestBodyIsMissing_thenThrows400Exception() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post(String.format(updateEserviceStateUrl, eServiceId, versionId))
            .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(null));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .updateEserviceState(updateEserviceStateDto);
    mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("e-service probing state gets updated")
  void testUpdateEserviceProbingState_whenGivenValidEServiceIdAndVersionId_thenEServiceProbingIsEnabled()
      throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post(String.format(updateProbingStateUrl, eServiceId, versionId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(changeProbingStateRequest));
    Mockito.doNothing().when(service).updateEserviceProbingState(updateEserviceProbingStateDto);
    mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("e-service probing state can't be updated because e-service does not exist")
  void testUpdateEserviceProbingState_whenEserviceDoesNotExist_thenThrows404Exception()
      throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post(String.format(updateProbingStateUrl, eServiceId, versionId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(changeProbingStateRequest));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .updateEserviceProbingState(updateEserviceProbingStateDto);
    mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("e-service probing state can't be updated because request body is missing")
  void testUpdateEserviceProbingState_whenRequestBodyIsMissing_thenThrows400Exception()
      throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post(String.format(updateProbingStateUrl, eServiceId, versionId))
            .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(null));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .updateEserviceProbingState(updateEserviceProbingStateDto);
    mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("e-service frequency, polling stard date and end date get updated")
  void testUpdateEserviceFrequencyDto_whenGivenValidEServiceIdAndVersionId_thenEserviceFrequencyPollingStartDateAndEndDateAreUpdated()
      throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post(String.format(updateEserviceFrequencyUrl, eServiceId, versionId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(changeProbingFrequencyRequest));
    Mockito.doNothing().when(service).updateEserviceFrequency(updateEserviceFrequencyDto);
    mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("e-service frequency can't be updated because e-service does not exist")
  void testUpdateEserviceFrequencyDto_whenEserviceDoesNotExist_thenThrows404Exception()
      throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post(String.format(updateEserviceFrequencyUrl, eServiceId, versionId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(changeProbingFrequencyRequest));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .updateEserviceFrequency(updateEserviceFrequencyDto);
    mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("e-service frequency can't be updated because e-service id request parameter ismissing")
  void testUpdateEserviceFrequencyDto_whenEserviceIdParameterIsMissing_thenThrows404Exception()
      throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/eservices/versions/" + versionId + "/updateState")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(changeProbingFrequencyRequest));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .updateEserviceFrequency(updateEserviceFrequencyDto);
    mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("e-service frequency can't be updated because e-service versione id requestparameter is missing")

  void testUpdateEserviceFrequencyDto_whenVersionIdParameterIsMissing_thenThrows404Exception()
      throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/eservices/" + eServiceId + "/versions/updateState")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(changeProbingFrequencyRequest));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .updateEserviceFrequency(updateEserviceFrequencyDto);
    mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("e-service frequency can't be updated because request body is missing")
  void testUpdateEserviceFrequencyDto_whenRequestBodyIsMissing_thenThrows400Exception()
      throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post(String.format(updateEserviceFrequencyUrl, eServiceId, versionId))
        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(null));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .updateEserviceFrequency(updateEserviceFrequencyDto);
    mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("bad request exception is thrown because pageNumber request parameter is missing")
  void testUpdateEserviceState_whenVersionIdParameterIsMissing_thenThrows400Exception()
      throws Exception {
    Mockito.doThrow(BadRequest.class).when(service).searchEservices(Mockito.anyInt(),
        Mockito.anyInt(), Mockito.anyString(), Mockito.any(), Mockito.anyInt(), Mockito.any());
    mockMvc
        .perform(get(apiSearchEserviceUrl).params(getMockRequestParamsUpdateEserviceState("2", null,
            "Eservice-Name", "Eservice-Version", "false", "ACTIVE")))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("the list of e-services has been retrieved")
  void testSearchEservice_whenGivenValidSizeAndPageNumber_thenReturnsSearchEserviceResponseWithContentEmpty()
      throws Exception {

    Mockito.when(service.searchEservices(2, 0, "Eservice-Name", "Eservice-Producer-Name", 1, null))
        .thenReturn(expectedSearchEserviceResponse);

    MockHttpServletResponse response =
        mockMvc
            .perform(get(apiSearchEserviceUrl).params(getMockRequestParamsUpdateEserviceState("2",
                "0", "Eservice-Name", "Eservice-Producer-Name", "1", null)))
            .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isNotEmpty();
    assertThat(response.getContentAsString()).contains("totalElements");
    assertThat(response.getContentAsString()).contains("content");

    SearchEserviceResponse searchEserviceResponse =
        mapper.readValue(response.getContentAsString(), SearchEserviceResponse.class);
    assertThat(searchEserviceResponse.getContent()).isNotEmpty();
    assertEquals(expectedSearchEserviceResponse, searchEserviceResponse);
  }

  @Test
  @DisplayName("the retrieved list of e-services is empty")
  void testSearchEservice_whenGivenValidSizeAndPageNumber_thenReturnsSearchEserviceResponseWithContentNotEmpty()
      throws Exception {
    List<EserviceMonitorState> listEservice = List.of(EserviceMonitorState.ONLINE);
    expectedSearchEserviceResponse.setContent(List.of());
    Mockito.doReturn(expectedSearchEserviceResponse).when(service).searchEservices(2, 0,
        "Eservice-Name", "Eservice-Producer-Name", 1, listEservice);

    MockHttpServletResponse response =
        mockMvc
            .perform(get(apiSearchEserviceUrl).params(getMockRequestParamsUpdateEserviceState("2",
                "0", "Eservice-Name", "Eservice-Producer-Name", "1", "ONLINE")))
            .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isNotEmpty();
    assertThat(response.getContentAsString()).contains("totalElements");
    assertThat(response.getContentAsString()).contains("content");

    SearchEserviceResponse searchEserviceResponse =
        mapper.readValue(response.getContentAsString(), SearchEserviceResponse.class);
    assertThat(searchEserviceResponse.getContent()).isEmpty();
    assertEquals(searchEserviceResponse, expectedSearchEserviceResponse);
  }

  @Test
  @DisplayName("bad request exception is thrown because size request parameter is missing")
  void testSearchEservice_whenSizeParameterIsMissing_thenThrows400Exception() throws Exception {
    Mockito.doThrow(BadRequest.class).when(service).searchEservices(Mockito.anyInt(),
        Mockito.anyInt(), Mockito.anyString(), Mockito.any(), Mockito.anyInt(), Mockito.any());
    mockMvc
        .perform(get(apiSearchEserviceUrl).params(getMockRequestParamsUpdateEserviceState(null, "0",
            "Eservice-Name", "Eservice-Version", "false", "ACTIVE")))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("the retrieve of e-services ready for polling is successful")
  void testGetEservicesReadyForPolling_whenEservicesAreReadyGivenValidLimitAndOffset_thenReturns2xxSuccessful()
      throws Exception {
    Integer limit = 10;
    Integer offset = 0;

    Mockito.when(service.getEservicesReadyForPolling(limit, offset))
        .thenReturn(pollingActiveEserviceResponse);

    MockHttpServletResponse response =
        mockMvc.perform(get(apiGetEservicesActiveUrl).param("limit", String.valueOf(limit))
            .param("offset", String.valueOf(offset))).andReturn().getResponse();
    assertEquals(response.getStatus(), HttpStatus.OK.value());
    assertTrue(response.getContentAsString().contains("content"));
    assertTrue(response.getContentAsString().contains("totalElements"));
  }

  @Test
  @DisplayName("the retrieve of e-services ready for polling is successful when there are no ready e-services")
  void testGetEservicesReadyForPolling_whenNoEservicesAreReadyGivenValidLimitAndOffset_thenReturns2xxSuccessful()
      throws Exception {
    Integer limit = 10;
    Integer offset = 0;

    Mockito.when(service.getEservicesReadyForPolling(limit, offset)).thenReturn(null);
    MockHttpServletResponse response =
        mockMvc.perform(get(apiGetEservicesActiveUrl).param("limit", String.valueOf(limit))
            .param("offset", String.valueOf(offset))).andReturn().getResponse();

    verify(service).getEservicesReadyForPolling(limit, offset);

    assertEquals(response.getStatus(), HttpStatus.OK.value());
    assertTrue(response.getContentAsString().isEmpty());
  }

  @Test
  @DisplayName("e-service last request gets updated")
  void testUpdateLastRequest_whenGivenValidEservicesRecordIdAndLastRequest_thenEserviceProbingRequestIsUpdated()
      throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post(String.format(apiUpdateLastRequestUrl, eservicesRecordId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(changeEserviceLastRequest));
    Mockito.doNothing().when(service).updateLastRequest(updateEserviceLastRequestDto);
    mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("e-service last request can't be updated because e-service record id request parameter is missing")
  void testUpdateLastRequest_whenEservicesRecordIdParameterIsMissing_thenThrows404Exception()
      throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/eservices/updateLastRequest")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(changeEserviceLastRequest));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .updateLastRequest(updateEserviceLastRequestDto);
    mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("e-service last request can't be updated because request body is missing")
  void testUpdateLastRequest_whenRequestBodyIsMissing_thenThrows400Exception() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post(String.format(apiUpdateLastRequestUrl, eservicesRecordId))
            .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(null));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .updateLastRequest(updateEserviceLastRequestDto);
    mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("e-service last request can't be updated because e-service does not exist")
  void testUpdateLastRequest_whenEserviceDoesNotExist_thenThrows404Exception() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post(String.format(apiUpdateLastRequestUrl, eservicesRecordId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(changeEserviceLastRequest));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .updateLastRequest(updateEserviceLastRequestDto);
    mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("e-service main data cant be retrieved because e-service does not exist")
  void testgetEserviceMainData_whenEserviceDoesNotExist_thenThrows404Exception() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(String.format(apiGetMainDataEserviceUrl, eservicesRecordId));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .getEserviceMainData(eservicesRecordId);
    mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("e-service main data are retrieved successfully")
  void testgetEserviceMainData_whenEserviceEcist_thenMainDataAreReturned() throws Exception {
    Mockito.doReturn(mainDataEserviceResponse).when(service).getEserviceMainData(eservicesRecordId);
    MockHttpServletResponse response =
        mockMvc.perform(get(String.format(apiGetMainDataEserviceUrl, eservicesRecordId)))
            .andReturn().getResponse();

    assertEquals(response.getStatus(), HttpStatus.OK.value());
    assertTrue(response.getContentAsString().contains("eserviceName"));
  }

  @Test
  @DisplayName("e-service probing data cant be retrieved because e-service does not exist")
  void testgetEserviceProbingData_whenEserviceDoesNotExist_thenThrows404Exception()
      throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(String.format(apiGetProbingDataEserviceUrl, eservicesRecordId));
    Mockito.doThrow(EserviceNotFoundException.class).when(service)
        .getEserviceProbingData(eservicesRecordId);
    mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("e-service probing data are retrieved successfully")
  void testgetEserviceProbingData_whenEserviceEcist_thenMainDataAreReturned() throws Exception {
    Mockito.doReturn(probingDataEserviceResponse).when(service)
        .getEserviceProbingData(eservicesRecordId);
    MockHttpServletResponse response =
        mockMvc.perform(get(String.format(apiGetProbingDataEserviceUrl, eservicesRecordId)))
            .andReturn().getResponse();

    assertEquals(response.getStatus(), HttpStatus.OK.value());
    assertTrue(response.getContentAsString().contains("probingEnabled"));
    assertTrue(response.getContentAsString().contains("state"));
  }

  private LinkedMultiValueMap<String, String> getMockRequestParamsUpdateEserviceState(String limit,
      String offset, String eserviceName, String producerName, String versionNumber, String state) {
    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("offset", offset);
    requestParams.add("limit", limit);
    requestParams.add("eserviceName", eserviceName);
    requestParams.add("producerName", producerName);
    requestParams.add("versionNumber", versionNumber);
    requestParams.add("state", state);
    return requestParams;
  }

}
