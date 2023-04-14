package it.pagopa.interop.probing.eservice.operations.unit.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import it.pagopa.interop.probing.eservice.operations.dtos.ProducerResponse;
import it.pagopa.interop.probing.eservice.operations.service.ProducerService;

@SpringBootTest
@AutoConfigureMockMvc
class ProducerControllerTest {


  @Value("${api.eservices.producers.url}")
  private String apiGetEservicesProducersUrl;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ProducerService service;

  private List<ProducerResponse> producerResponseExpectedList;

  @Test
  @DisplayName("given a valid producer name, then returns a non-empty list")
  void testGetEservicesProducers_whenGivenValidProducerName_thenReturnsSearchProducerNameResponseList()
      throws Exception {
    ProducerResponse producerResponse = ProducerResponse.builder().label("ProducerName-Test-1")
        .value("ProducerName-Test-1").build();

    producerResponseExpectedList = List.of(producerResponse);
    Mockito.when(service.getEservicesProducers("ProducerName-Test"))
        .thenReturn(producerResponseExpectedList);
    MockHttpServletResponse response =
        mockMvc.perform(get(apiGetEservicesProducersUrl).param("producerName", "ProducerName-Test"))
            .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isNotEmpty();
    assertThat(response.getContentAsString()).contains("label");
    assertThat(response.getContentAsString()).contains("value");
  }

  @Test
  @DisplayName("given a valid producer name with no matching records, then returns an empty list")
  void testGetEservicesProducers_whenGivenValidProducerName_thenReturnsSearchProducerNameResponseListEmpty()
      throws Exception {
    Mockito.when(service.getEservicesProducers("ProducerName-Test"))
        .thenReturn(new ArrayList<ProducerResponse>());
    MockHttpServletResponse response =
        mockMvc.perform(get(apiGetEservicesProducersUrl).param("producerName", "ProducerName-Test"))
            .andReturn().getResponse();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
  }

}
