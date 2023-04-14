package it.pagopa.interop.probing.eservice.operations.service.impl;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.service.ProducerService;
import it.pagopa.interop.probing.eservice.operations.util.constant.ProjectConstants;

@Service
public class ProducerServiceImpl implements ProducerService {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<SearchProducerNameResponse> getEservicesProducers(String producerName) {

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<SearchProducerNameResponse> query =
        cb.createQuery(SearchProducerNameResponse.class);
    Root<Eservice> root = query.from(Eservice.class);
    query.distinct(true).multiselect(root.get(ProjectConstants.PRODUCER_NAME_FIELD),
        root.get(ProjectConstants.PRODUCER_NAME_FIELD));

    Predicate predicate = cb.like(cb.upper(root.get(ProjectConstants.PRODUCER_NAME_FIELD)),
        "%" + producerName.toUpperCase() + "%");

    query.where(predicate);
    TypedQuery<SearchProducerNameResponse> q = entityManager.createQuery(query);

    return q.getResultList();
  }

}
