package it.pagopa.interop.probing.eservice.operations.unit.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.pagopa.interop.probing.eservice.operations.dtos.ChangeEserviceStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingFrequencyRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceSaveRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceViewDTO;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchEserviceResponse;
import it.pagopa.interop.probing.eservice.operations.exception.EserviceNotFoundException;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceFrequencyDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceProbingStateDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.dto.UpdateEserviceStateDto;
import it.pagopa.interop.probing.eservice.operations.mapstruct.mapper.MapStructMapper;
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

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	MapStructMapper mapstructMapper;

	@MockBean
	private EserviceService service;

	private EserviceSaveRequest eserviceSaveRequest;

	private ChangeEserviceStateRequest changeEserviceStateRequest;

	private ChangeProbingStateRequest changeProbingStateRequest;

	private ChangeProbingFrequencyRequest changeProbingFrequencyRequest;

	private UpdateEserviceStateDto updateEserviceStateDto;

	private UpdateEserviceProbingStateDto updateEserviceProbingStateDto;

	private UpdateEserviceFrequencyDto updateEserviceFrequencyDto;

	private SearchEserviceResponse expectedSearchEserviceResponse;

	private final UUID eServiceId = UUID.randomUUID();
	private final UUID versionId = UUID.randomUUID();

	@BeforeEach
	void setup() {
		changeEserviceStateRequest = ChangeEserviceStateRequest.builder().eServiceState(EserviceState.OFFLINE).build();

		eserviceSaveRequest = EserviceSaveRequest.builder().basePath(new ArrayList<String>())
				.eserviceId(eServiceId.toString()).name("Eservice name test").producerName("Eservice producer test")
				.technology(EserviceTechnology.fromValue("REST")).versionId(versionId.toString()).versionNumber("1")
				.state(EserviceState.ONLINE).build();

		updateEserviceStateDto = UpdateEserviceStateDto.builder().eserviceId(eServiceId).versionId(versionId)
				.newEServiceState(changeEserviceStateRequest.geteServiceState()).build();

		changeProbingStateRequest = ChangeProbingStateRequest.builder().probingEnabled(true).build();

		updateEserviceProbingStateDto = UpdateEserviceProbingStateDto.builder().eserviceId(eServiceId)
				.versionId(versionId).probingEnabled(changeProbingStateRequest.getProbingEnabled()).build();

		changeProbingFrequencyRequest = ChangeProbingFrequencyRequest.builder().frequency(5)
				.startTime(OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC))
				.endTime(OffsetTime.of(20, 0, 0, 0, ZoneOffset.UTC)).build();

		updateEserviceFrequencyDto = UpdateEserviceFrequencyDto.builder().eserviceId(eServiceId).versionId(versionId)
				.newPollingFrequency(changeProbingFrequencyRequest.getFrequency())
				.newPollingStartTime(changeProbingFrequencyRequest.getStartTime())
				.newPollingEndTime(changeProbingFrequencyRequest.getEndTime()).build();

		expectedSearchEserviceResponse = SearchEserviceResponse.builder().limit(2).offset(0).build();

		EserviceViewDTO eserviceViewDTO = EserviceViewDTO.builder().eserviceName("Eservice-Name").versionNumber(1)
				.producerName("Eservice-Producer-Name").state(EserviceState.ONLINE).build();

		List<EserviceViewDTO> eservices = Arrays.asList(eserviceViewDTO);
		expectedSearchEserviceResponse.setContent(eservices);
	}

	@Test
	@DisplayName("e-service state gets saved")
	void testSaveService_whenGivenValidEserviceSaveRequest_thenReturnsId() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(String.format(saveEserviceUrl))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(eserviceSaveRequest));
		Mockito.when(service.saveEservice(eserviceSaveRequest)).thenReturn(1L);
		MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).contains("1");
	}

	@Test
	@DisplayName("e-service state gets updated")
	void testUpdateEserviceState_whenGivenValidEServiceIdAndVersionId_thenEServiceStateIsUpdated() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(String.format(updateEserviceStateUrl, eServiceId, versionId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(changeEserviceStateRequest));
		Mockito.doNothing().when(service).updateEserviceState(updateEserviceStateDto);
		mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("e-service state can't be updated because e-service does not exist")
	void testUpdateEserviceState_whenEserviceDoesNotExist_thenThrows404Exception() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(String.format(updateEserviceStateUrl, eServiceId, versionId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(changeEserviceStateRequest));
		Mockito.doThrow(EserviceNotFoundException.class).when(service).updateEserviceState(updateEserviceStateDto);
		mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("e-service state can't be updated because e-service id request parameter is missing")
	void testUpdateEserviceState_whenEserviceIdParameterIsMissing_thenThrows404Exception() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/eservices/versions/" + versionId + "/updateState")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(changeEserviceStateRequest));
		Mockito.doThrow(EserviceNotFoundException.class).when(service).updateEserviceState(updateEserviceStateDto);
		mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("e-service state can't be updated because e-service versione id request parameter is missing")
	void testUpdateEserviceState_whenVersionIdParameterIsMissing_thenThrows404Exception() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/eservices/" + eServiceId + "/versions/updateState").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(changeEserviceStateRequest));
		Mockito.doThrow(EserviceNotFoundException.class).when(service).updateEserviceState(updateEserviceStateDto);
		mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("e-service state can't be updated because request body is missing")
	void testUpdateEserviceState_whenRequestBodyIsMissing_thenThrows400Exception() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(String.format(updateEserviceStateUrl, eServiceId, versionId))
				.contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(null));
		Mockito.doThrow(EserviceNotFoundException.class).when(service).updateEserviceState(updateEserviceStateDto);
		mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("e-service probing state gets updated")
	void testUpdateEserviceProbingState_whenGivenValidEServiceIdAndVersionId_thenEServiceProbingIsEnabled()
			throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(String.format(updateProbingStateUrl, eServiceId, versionId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(changeProbingStateRequest));
		Mockito.doNothing().when(service).updateEserviceProbingState(updateEserviceProbingStateDto);
		mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("e-service probing state can't be updated because e-service does not exist")
	void testUpdateEserviceProbingState_whenEserviceDoesNotExist_thenThrows404Exception() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(String.format(updateProbingStateUrl, eServiceId, versionId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(changeProbingStateRequest));
		Mockito.doThrow(EserviceNotFoundException.class).when(service)
				.updateEserviceProbingState(updateEserviceProbingStateDto);
		mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("e-service probing state can't be updated because request body is missing")
	void testUpdateEserviceProbingState_whenRequestBodyIsMissing_thenThrows400Exception() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(String.format(updateProbingStateUrl, eServiceId, versionId))
				.contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(null));
		Mockito.doThrow(EserviceNotFoundException.class).when(service)
				.updateEserviceProbingState(updateEserviceProbingStateDto);
		mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("e-service frequency, polling stard date and end date get updated")
	void testUpdateEserviceFrequencyDto_whenGivenValidEServiceIdAndVersionId_thenEserviceFrequencyPollingStartDateAndEndDateAreUpdated()
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(String.format(updateEserviceFrequencyUrl, eServiceId, versionId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(changeProbingFrequencyRequest));
		Mockito.doNothing().when(service).updateEserviceFrequency(updateEserviceFrequencyDto);
		mockMvc.perform(requestBuilder).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("e-service frequency can't be updated because e-service does not exist")
	void testUpdateEserviceFrequencyDto_whenEserviceDoesNotExist_thenThrows404Exception() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(String.format(updateEserviceFrequencyUrl, eServiceId, versionId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(changeProbingFrequencyRequest));
		Mockito.doThrow(EserviceNotFoundException.class).when(service)
				.updateEserviceFrequency(updateEserviceFrequencyDto);
		mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("e-service frequency can't be updated because e-service id request parameter is missing")
	void testUpdateEserviceFrequencyDto_whenEserviceIdParameterIsMissing_thenThrows404Exception() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/eservices/versions/" + versionId + "/updateState")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(changeProbingFrequencyRequest));
		Mockito.doThrow(EserviceNotFoundException.class).when(service)
				.updateEserviceFrequency(updateEserviceFrequencyDto);
		mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("e-service frequency can't be updated because e-service versione id request parameter is missing")
	void testUpdateEserviceFrequencyDto_whenVersionIdParameterIsMissing_thenThrows404Exception() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/eservices/" + eServiceId + "/versions/updateState").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(changeProbingFrequencyRequest));
		Mockito.doThrow(EserviceNotFoundException.class).when(service)
				.updateEserviceFrequency(updateEserviceFrequencyDto);
		mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("e-service frequency can't be updated because request body is missing")
	void testUpdateEserviceFrequencyDto_whenRequestBodyIsMissing_thenThrows400Exception() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(String.format(updateEserviceFrequencyUrl, eServiceId, versionId))
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(null));
		Mockito.doThrow(EserviceNotFoundException.class).when(service)
				.updateEserviceFrequency(updateEserviceFrequencyDto);
		mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("the list of e-services has been retrieved")
	void testSearchEservice_whenGivenValidSizeAndPageNumber_thenReturnsSearchEserviceResponseWithContentNotEmpty()
			throws Exception {

		Mockito.doReturn(expectedSearchEserviceResponse).when(service).searchEservices(2, 0, "Eservice-Name",
				"Eservice-Producer-Name", 1, null);

		MockHttpServletResponse response = mockMvc
				.perform(get(apiSearchEserviceUrl).params(getMockRequestParamsUpdateEserviceState("2", "0",
						"Eservice-Name", "Eservice-Producer-Name", "1", null)))
				.andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).isNotEmpty();
		assertThat(response.getContentAsString()).contains("totalElements");
		assertThat(response.getContentAsString()).contains("content");

		SearchEserviceResponse searchEserviceResponse = new ObjectMapper().readValue(response.getContentAsString(),
				SearchEserviceResponse.class);
		assertThat(searchEserviceResponse.getContent()).isNotEmpty();
		assertEquals(searchEserviceResponse, expectedSearchEserviceResponse);
	}

	@Test
	@DisplayName("the retrieved list of e-services is empty")
	void testSearchEservice_whenGivenValidSizeAndPageNumber_thenReturnsSearchEserviceResponseWithContentEmpty()
			throws Exception {
		List<EserviceState> listEservice = Arrays.asList(EserviceState.ONLINE);
		expectedSearchEserviceResponse.setContent(new ArrayList<>());
		Mockito.doReturn(expectedSearchEserviceResponse).when(service).searchEservices(2, 0, "Eservice-Name",
				"Eservice-Producer-Name", 1, listEservice);

		MockHttpServletResponse response = mockMvc
				.perform(get(apiSearchEserviceUrl).params(getMockRequestParamsUpdateEserviceState("2", "0",
						"Eservice-Name", "Eservice-Producer-Name", "1", "ONLINE")))
				.andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).isNotEmpty();
		assertThat(response.getContentAsString()).contains("totalElements");
		assertThat(response.getContentAsString()).contains("content");

		SearchEserviceResponse searchEserviceResponse = new ObjectMapper().readValue(response.getContentAsString(),
				SearchEserviceResponse.class);
		assertThat(searchEserviceResponse.getContent()).isEmpty();
		assertEquals(searchEserviceResponse, expectedSearchEserviceResponse);
	}

	@Test
	@DisplayName("bad request exception is thrown because size request parameter is missing")
	void testSearchEservice_whenSizeParameterIsMissing_thenThrows400Exception() throws Exception {
		Mockito.doThrow(BadRequest.class).when(service).searchEservices(Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyString(), Mockito.any(), Mockito.anyInt(), Mockito.any());
		mockMvc.perform(get(apiSearchEserviceUrl).params(getMockRequestParamsUpdateEserviceState(null, "0",
				"Eservice-Name", "Eservice-Version", "false", "ONLINE"))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("bad request exception is thrown because pageNumber request parameter is missing")
	void testUpdateEserviceState_whenVersionIdParameterIsMissing_thenThrows400Exception() throws Exception {
		Mockito.doThrow(BadRequest.class).when(service).searchEservices(Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyString(), Mockito.any(), Mockito.anyInt(), Mockito.any());
		mockMvc.perform(get(apiSearchEserviceUrl).params(getMockRequestParamsUpdateEserviceState("2", null,
				"Eservice-Name", "Eservice-Version", "false", "ONLINE"))).andExpect(status().isBadRequest());
	}

	private LinkedMultiValueMap<String, String> getMockRequestParamsUpdateEserviceState(String limit, String offset,
			String eserviceName, String producerName, String versionNumber, String eServiceState) {
		LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
		requestParams.add("offset", offset);
		requestParams.add("limit", limit);
		requestParams.add("eserviceName", eserviceName);
		requestParams.add("eserviceProducerName", producerName);
		requestParams.add("versionNumber", versionNumber);
		requestParams.add("eServiceState", eServiceState);
		return requestParams;
	}

}
