package it.pagopa.interop.probing.eservice.operations.repository.query.builder;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceContent;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.model.EserviceContentCriteria;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView_;

@Repository
public class EserviceContentQueryBuilder {

  @PersistenceContext
  private EntityManager entityManager;

  public List<EserviceContent> findAllEservicesReadyForPolling(Integer limit, Integer offset) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<EserviceContentCriteria> query = cb.createQuery(EserviceContentCriteria.class);
    Root<EserviceView> root = query.from(EserviceView.class);

    query.distinct(true).multiselect(root.get(EserviceView_.ESERVICE_RECORD_ID),
        root.get(EserviceView_.TECHNOLOGY), root.get(EserviceView_.BASE_PATH),
        root.get(EserviceView_.AUDIENCE));

    Predicate predicate = buildPredicate(cb, root);

    query.where(predicate).orderBy(cb.asc(root.get(EserviceView_.ESERVICE_RECORD_ID)));

    TypedQuery<EserviceContentCriteria> q =
        entityManager.createQuery(query).setFirstResult(offset).setMaxResults(limit);

    return q.getResultList().stream().map(e -> (EserviceContent) e).toList();
  }

  public Long getTotalCount() {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
    Root<EserviceView> root = criteriaQuery.from(EserviceView.class);

    Predicate predicate = buildPredicate(cb, root);

    criteriaQuery.select(cb.count(root.get(EserviceView_.ESERVICE_RECORD_ID))).where(predicate);

    return entityManager.createQuery(criteriaQuery).getSingleResult();
  }

  private Predicate buildPredicate(CriteriaBuilder cb, Root<EserviceView> root) {
    Expression<Timestamp> makeInterval = cb.function("make_interval", Timestamp.class,
        root.get(EserviceView_.LAST_REQUEST), root.get(EserviceView_.POLLING_FREQUENCY));

    Expression<Boolean> compareTimestampInterval =
        cb.function("compare_timestamp_interval", Boolean.TYPE,
            root.get(EserviceView_.POLLING_START_TIME), root.get(EserviceView_.POLLING_END_TIME));

    return cb.and(cb.equal(root.get(EserviceView_.STATE), EserviceInteropState.ACTIVE),
        cb.isTrue(root.get(EserviceView_.PROBING_ENABLED)),
        cb.or(
            cb.and(cb.isNull(root.get(EserviceView_.LAST_REQUEST)),
                cb.isNull(root.get(EserviceView_.RESPONSE_RECEIVED))),
            cb.and(cb.lessThanOrEqualTo(makeInterval, cb.currentTimestamp()),
                cb.lessThanOrEqualTo(root.get(EserviceView_.LAST_REQUEST),
                    root.get(EserviceView_.RESPONSE_RECEIVED)))),
        cb.isTrue(compareTimestampInterval));

  }

}
