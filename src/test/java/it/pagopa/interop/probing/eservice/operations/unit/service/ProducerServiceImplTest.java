package it.pagopa.interop.probing.eservice.operations.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
import it.pagopa.interop.probing.eservice.operations.mapping.mapper.MapperImpl;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceRepository;
import it.pagopa.interop.probing.eservice.operations.service.ProducerService;
import it.pagopa.interop.probing.eservice.operations.service.impl.ProducerServiceImpl;
import it.pagopa.interop.probing.eservice.operations.util.OffsetLimitPageable;

@SpringBootTest
class ProducerServiceImplTest {
  @Mock
  EserviceRepository eserviceRepository;

  @Mock
  MapperImpl mapstructMapper;

  @InjectMocks
  ProducerService service = new ProducerServiceImpl();

  List<SearchProducerNameResponse> searchProducerNameResponseExpectedList;

  @Test
  @DisplayName("when searching for a valid producer name, then return the list of producers")
  void testGetEservicesProducers_whenGivenValidProducerName_thenReturnsSearchProducerNameResponseList() {
    searchProducerNameResponseExpectedList =
        List.of(new SearchProducerNameResponse("ProducerName-Test-1", "ProducerName-Test-1"),
            new SearchProducerNameResponse("ProducerName-Test-2", "ProducerName-Test-2"));
    Mockito
        .when(eserviceRepository.getEservicesProducers(
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
    Mockito.when(eserviceRepository.getEservicesProducers(
        ArgumentMatchers.eq("ProducerName-Test".toUpperCase()),
        ArgumentMatchers.any(OffsetLimitPageable.class))).thenReturn(List.of());
    List<SearchProducerNameResponse> searchProducerNameResponseResultList =
        service.getEservicesProducers("ProducerName-Test");
    assertEquals(0, searchProducerNameResponseResultList.size());
  }
}
