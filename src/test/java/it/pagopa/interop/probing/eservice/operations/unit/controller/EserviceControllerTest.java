package it.pagopa.interop.probing.eservice.operations.unit.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetTime;
import java.time.ZoneOffset;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.pagopa.interop.probing.eservice.operations.dtos.ChangeEserviceStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingFrequencyRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.ChangeProbingStateRequest;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
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

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	MapStructMapper mapstructMapper;

	@MockBean
	private EserviceService service;

	private ChangeEserviceStateRequest changeEserviceStateRequest;

	private ChangeProbingStateRequest changeProbingStateRequest;

	private ChangeProbingFrequencyRequest changeProbingFrequencyRequest;
	
	private UpdateEserviceStateDto updateEserviceStateDto;

	private UpdateEserviceProbingStateDto updateEserviceProbingStateDto;
	
	private UpdateEserviceFrequencyDto updateEserviceFrequencyDto;

	private final UUID eServiceId = UUID.randomUUID();
	private final UUID versionId = UUID.randomUUID();

	@BeforeEach
	void setup() {
		changeEserviceStateRequest = new ChangeEserviceStateRequest();
		changeEserviceStateRequest.seteServiceState(EserviceState.INACTIVE);
		updateEserviceStateDto = new UpdateEserviceStateDto();
		updateEserviceStateDto.setEserviceId(eServiceId);
		updateEserviceStateDto.setVersionId(versionId);
		updateEserviceStateDto.setNewEServiceState(changeEserviceStateRequest.geteServiceState());

		changeProbingStateRequest = new ChangeProbingStateRequest();
		changeProbingStateRequest.setProbingEnabled(true);
		updateEserviceProbingStateDto = new UpdateEserviceProbingStateDto();
		updateEserviceProbingStateDto.setEserviceId(eServiceId);
		updateEserviceProbingStateDto.setVersionId(versionId);
		updateEserviceProbingStateDto.setProbingEnabled(changeProbingStateRequest.getProbingEnabled());

		changeProbingFrequencyRequest = new ChangeProbingFrequencyRequest();
		changeProbingFrequencyRequest.setFrequency(5);
		changeProbingFrequencyRequest.setStartTime(OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC));
		changeProbingFrequencyRequest.setEndTime(OffsetTime.of(20, 0, 0, 0, ZoneOffset.UTC));

		updateEserviceFrequencyDto = new UpdateEserviceFrequencyDto();
		updateEserviceFrequencyDto.setEserviceId(eServiceId);
		updateEserviceFrequencyDto.setVersionId(versionId);
		updateEserviceFrequencyDto.setNewPollingFrequency(changeProbingFrequencyRequest.getFrequency());
		updateEserviceFrequencyDto.setNewPollingStartTime(changeProbingFrequencyRequest.getStartTime());
		updateEserviceFrequencyDto.setNewPollingEndTime(changeProbingFrequencyRequest.getEndTime());
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
}
