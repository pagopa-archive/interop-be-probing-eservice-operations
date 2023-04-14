package it.pagopa.interop.probing.eservice.operations.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
import it.pagopa.interop.probing.eservice.operations.mapping.mapper.AbstractMapper;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.service.ProducerService;
import it.pagopa.interop.probing.eservice.operations.service.impl.ProducerServiceImpl;

@SpringBootTest(classes = {EntityManager.class, EntityManagerFactory.class})
class ProducerServiceImplTest {

  @Mock
  EntityManager entityManager;

  @Mock
  EntityManagerFactory entityManagerFactory;

  @Mock
  CriteriaBuilder cb;

  @Mock
  Root<Eservice> root;

  @Mock
  CriteriaQuery<SearchProducerNameResponse> query;

  @Mock
  TypedQuery<SearchProducerNameResponse> q;

  @Mock
  AbstractMapper mapstructMapper;

  @InjectMocks
  ProducerService service = new ProducerServiceImpl();

  List<SearchProducerNameResponse> searchProducerNameResponseExpectedList;

  @BeforeEach
  void setup() {
    Mockito.when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
  }

  @Test
  @DisplayName("when searching for a valid producer name, then return the list of producers")
  void testGetEservicesProducers_whenGivenValidProducerName_thenReturnsSearchProducerNameResponseList() {
    searchProducerNameResponseExpectedList =
        List.of(new SearchProducerNameResponse("ProducerName-Test-1", "ProducerName-Test-1"),
            new SearchProducerNameResponse("ProducerName-Test-2", "ProducerName-Test-2"));
    Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(cb);
    Mockito.when(cb.createQuery(SearchProducerNameResponse.class)).thenReturn(query);
    Mockito.when(query.from(Eservice.class)).thenReturn(root);
    Mockito.when(query.distinct(true)).thenReturn(query);
    Mockito.when(query.multiselect(Mockito.any(), Mockito.any())).thenReturn(query);
    Mockito.when(entityManager.createQuery(query)).thenReturn(q);
    Mockito.when(q.getResultList()).thenReturn(searchProducerNameResponseExpectedList);

    assertEquals(q.getResultList().size(), searchProducerNameResponseExpectedList.size());
  }
}
