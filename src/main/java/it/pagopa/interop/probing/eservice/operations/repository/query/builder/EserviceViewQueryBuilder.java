package it.pagopa.interop.probing.eservice.operations.repository.query.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceMonitorState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceStatus;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView_;

@Repository
public class EserviceViewQueryBuilder {

  @PersistenceContext
  private EntityManager entityManager;

  public List<EserviceView> findAllWithoutNDState(Integer limit, Integer offset,
      String eserviceName, String producerName, Integer versionNumber,
      List<EserviceMonitorState> stateList, int minOfTolleranceMultiplier) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<EserviceView> query = cb.createQuery(EserviceView.class);
    Root<EserviceView> root = query.from(EserviceView.class);
    query.distinct(true).select(root);

    Predicate predicate = buildQueryWithoutNDState(cb, root, eserviceName, producerName,
        versionNumber, stateList, minOfTolleranceMultiplier);

    query.where(predicate).orderBy(cb.asc(root.get(EserviceView_.ESERVICE_RECORD_ID)));

    TypedQuery<EserviceView> q =
        entityManager.createQuery(query).setFirstResult(offset).setMaxResults(limit);

    return q.getResultList();
  }

  public Predicate buildQueryEserviceNameProducerNameVersionNumberEquals(CriteriaBuilder cb,
      Root<EserviceView> root, String eserviceName, String producerName, Integer versionNumber) {
    List<Predicate> predicates = new ArrayList<>();
    if (Objects.nonNull(eserviceName)) {
      predicates.add(cb.like(root.get(EserviceView_.ESERVICE_NAME), "%" + eserviceName + "%"));
    }
    if (Objects.nonNull(producerName)) {
      predicates.add(cb.equal(root.get(EserviceView_.PRODUCER_NAME), producerName));
    }
    if (Objects.nonNull(versionNumber)) {
      predicates.add(cb.equal(root.get(EserviceView_.VERSION_NUMBER), versionNumber));
    }
    return cb.and(predicates.toArray(new Predicate[] {}));
  }

  private Predicate buildQueryWithoutNDState(CriteriaBuilder cb, Root<EserviceView> root,
      String eserviceName, String producerName, Integer versionNumber,
      List<EserviceMonitorState> stateList, int minOfTolleranceMultiplier) {
    Expression<Integer> extractMinute =
        cb.function("extract_minute", Integer.class, root.get(EserviceView_.LAST_REQUEST));
    return cb.and(
        buildQueryEserviceNameProducerNameVersionNumberEquals(cb, root, eserviceName, producerName,
            versionNumber),
        buildProbingEnabledPredicate(cb, root, stateList, extractMinute,
            minOfTolleranceMultiplier));
  }

  public Long getTotalCountWithoutNDState(String eserviceName, String producerName,
      Integer versionNumber, List<EserviceMonitorState> stateList, int minOfTolleranceMultiplier) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
    Root<EserviceView> root = criteriaQuery.from(EserviceView.class);

    Predicate predicate = buildQueryWithoutNDState(cb, root, eserviceName, producerName,
        versionNumber, stateList, minOfTolleranceMultiplier);

    criteriaQuery.select(cb.count(root.get(EserviceView_.ESERVICE_RECORD_ID))).where(predicate);

    return entityManager.createQuery(criteriaQuery).getSingleResult();
  }

  public List<EserviceView> findAllWithNDState(Integer limit, Integer offset, String eserviceName,
      String producerName, Integer versionNumber, List<EserviceMonitorState> stateList,
      int minOfTolleranceMultiplier) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<EserviceView> query = cb.createQuery(EserviceView.class);
    Root<EserviceView> root = query.from(EserviceView.class);

    Predicate predicate = buildPredicate(cb, root, eserviceName, producerName, versionNumber,
        stateList, minOfTolleranceMultiplier);

    query.where(cb.or(predicate)).orderBy(cb.asc(root.get(EserviceView_.ESERVICE_RECORD_ID)));
    TypedQuery<EserviceView> q =
        entityManager.createQuery(query).setFirstResult(offset).setMaxResults(limit);
    return q.getResultList();
  }

  private Predicate buildPredicate(CriteriaBuilder cb, Root<EserviceView> root, String eserviceName,
      String producerName, Integer versionNumber, List<EserviceMonitorState> stateList,
      int minOfTolleranceMultiplier) {
    Expression<Integer> extractMinute =
        cb.function("extract_minute", Integer.class, root.get(EserviceView_.LAST_REQUEST));
    return cb.and(
        buildQueryEserviceNameProducerNameVersionNumberEquals(cb, root, eserviceName, producerName,
            versionNumber),
        cb.or(
            buildProbingEnabledPredicate(cb, root, stateList, extractMinute,
                minOfTolleranceMultiplier),
            buildProbingDisabledPredicate(cb, root, extractMinute)));
  }

  private Predicate buildProbingEnabledPredicate(CriteriaBuilder cb, Root<EserviceView> root,
      List<EserviceMonitorState> stateList, Expression<Integer> extractMinute,
      int minOfTolleranceMultiplier) {

    List<Predicate> predicates = new ArrayList<>();
    if (stateList.contains(EserviceMonitorState.OFFLINE)) {
      predicates.add(cb.or(cb.equal(root.get(EserviceView_.STATE), EserviceInteropState.INACTIVE),
          cb.equal(root.get(EserviceView_.RESPONSE_STATUS), EserviceStatus.KO)));
    }
    if (stateList.contains(EserviceMonitorState.ONLINE)) {
      predicates.add(cb.and(cb.equal(root.get(EserviceView_.STATE), EserviceInteropState.ACTIVE),
          cb.equal(root.get(EserviceView_.RESPONSE_STATUS), EserviceStatus.OK)));
    }
    return cb.and(cb.or(predicates.toArray(new Predicate[] {})),
        cb.isTrue(root.get(EserviceView_.PROBING_ENABLED)),
        cb.isNotNull(root.get(EserviceView_.LAST_REQUEST)),
        cb.or(
            cb.lessThan(extractMinute,
                cb.prod(root.get(EserviceView_.POLLING_FREQUENCY), minOfTolleranceMultiplier)),
            cb.greaterThan(root.get(EserviceView_.RESPONSE_RECEIVED),
                root.get(EserviceView_.LAST_REQUEST))),
        cb.isNotNull(root.get(EserviceView_.RESPONSE_RECEIVED)));
  }

  private Predicate buildProbingDisabledPredicate(CriteriaBuilder cb, Root<EserviceView> root,
      Expression<Integer> extractMinute) {
    return cb.or(cb.isFalse(root.get(EserviceView_.PROBING_ENABLED)),
        cb.isNull(root.get(EserviceView_.LAST_REQUEST)),
        cb.and(cb.greaterThan(extractMinute, root.get(EserviceView_.POLLING_FREQUENCY)),
            cb.lessThan(root.get(EserviceView_.RESPONSE_RECEIVED),
                root.get(EserviceView_.LAST_REQUEST))),
        cb.isNull(root.get(EserviceView_.RESPONSE_RECEIVED)));
  }

  public Long getTotalCountWithNDState(String eserviceName, String producerName,
      Integer versionNumber, List<EserviceMonitorState> stateList, int minOfTolleranceMultiplier) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
    Root<EserviceView> root = criteriaQuery.from(EserviceView.class);

    Predicate predicate = buildPredicate(cb, root, eserviceName, producerName, versionNumber,
        stateList, minOfTolleranceMultiplier);

    criteriaQuery.select(cb.count(root.get(EserviceView_.ESERVICE_RECORD_ID))).where(predicate);

    return entityManager.createQuery(criteriaQuery).getSingleResult();
  }
}
