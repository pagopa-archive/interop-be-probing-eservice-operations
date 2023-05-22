package it.pagopa.interop.probing.eservice.operations.repository.query.builder;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import it.pagopa.interop.probing.eservice.operations.dtos.Producer;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.model.Eservice_;

@Repository
public class ProducerQueryBuilder {
  @PersistenceContext
  private EntityManager entityManager;

  public List<Producer> findAllProducersByProducerName(Integer limit, Integer offset,
      String producerName) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Producer> query = cb.createQuery(Producer.class);
    Root<Eservice> root = query.from(Eservice.class);
    query.distinct(true).multiselect(root.get(Eservice_.PRODUCER_NAME));

    Predicate predicate = cb.like(cb.upper(root.get(Eservice_.PRODUCER_NAME)),
        "%" + producerName.toUpperCase() + "%");

    query.where(predicate);
    TypedQuery<Producer> q =
        entityManager.createQuery(query).setFirstResult(offset).setMaxResults(limit);

    return q.getResultList();
  }

}
