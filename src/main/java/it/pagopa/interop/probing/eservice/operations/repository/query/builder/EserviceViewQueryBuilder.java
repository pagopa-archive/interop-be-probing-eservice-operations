package it.pagopa.interop.probing.eservice.operations.repository.query.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import it.pagopa.interop.probing.eservice.operations.mapping.mapper.AbstractMapper;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView_;
import it.pagopa.interop.probing.eservice.operations.util.constant.ProjectConstants;

@Component
public class EserviceViewQueryBuilder {

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  AbstractMapper mapper;

  public Page<EserviceView> findAllWithoutNDState(Integer limit, Integer offset,
      String eserviceName, String producerName, Integer versionNumber, List<String> stateList,
      int minOfTolleranceMultiplier) {

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<EserviceView> query = cb.createQuery(EserviceView.class);
    Root<EserviceView> root = query.from(EserviceView.class);
    query.distinct(true).select(root);

    Predicate predicate = buildQuery(cb, root, eserviceName, producerName, versionNumber, stateList,
        minOfTolleranceMultiplier);

    query.where(predicate);
    TypedQuery<EserviceView> q = entityManager.createQuery(query);


    List<EserviceView> content = q.getResultList();

    return new PageImpl<>(content.stream().collect(Collectors.toList()), PageRequest.of(offset,
        limit, Sort.by(ProjectConstants.ESERVICE_NAME_COLUMN_NAME).ascending()), content.size());
  }

  public Page<EserviceView> findAllWithNDState(Integer limit, Integer offset, String eserviceName,
      String producerName, Integer versionNumber, List<String> stateList,
      int minOfTolleranceMultiplier) {

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<EserviceView> query = cb.createQuery(EserviceView.class);
    Root<EserviceView> root = query.from(EserviceView.class);

    Predicate predicate = buildQuery(cb, root, eserviceName, producerName, versionNumber, stateList,
        minOfTolleranceMultiplier);

    Expression<Integer> extractMinute =
        cb.function("extract_minute", Integer.class, root.get(EserviceView_.LAST_REQUEST));

    Predicate predicateNotDefinedCondition =
        cb.or(cb.isFalse(root.get(EserviceView_.PROBING_ENABLED)),
            cb.isNotNull(root.get(EserviceView_.LAST_REQUEST)), cb
                .and(
                    cb.greaterThan(extractMinute,
                        cb.prod(root.get(EserviceView_.POLLING_FREQUENCY),
                            minOfTolleranceMultiplier)),
                    cb.lessThan(root.get(EserviceView_.RESPONSE_RECEIVED),
                        root.get(EserviceView_.LAST_REQUEST))),
            cb.isNotNull(root.get(EserviceView_.RESPONSE_RECEIVED)));

    query.where(cb.or(predicate, predicateNotDefinedCondition));
    TypedQuery<EserviceView> q = entityManager.createQuery(query);


    List<EserviceView> content = q.getResultList();

    return new PageImpl<>(content.stream().collect(Collectors.toList()), PageRequest.of(offset,
        limit, Sort.by(ProjectConstants.ESERVICE_NAME_COLUMN_NAME).ascending()), content.size());
  }

  public Predicate buildQuery(CriteriaBuilder cb, Root<EserviceView> root, String eserviceName,
      String producerName, Integer versionNumber, List<String> stateList,
      int minOfTolleranceMultiplier) {

    Expression<Integer> extractMinute =
        cb.function("extract_minute", Integer.class, root.get(EserviceView_.LAST_REQUEST));

    List<Predicate> predicates = new ArrayList<>();

    if (Objects.nonNull(eserviceName)) {
      predicates.add(cb.equal(root.get(EserviceView_.ESERVICE_NAME), eserviceName));
    }

    if (Objects.nonNull(producerName)) {
      predicates.add(cb.equal(root.get(EserviceView_.PRODUCER_NAME), producerName));
    }

    if (Objects.nonNull(versionNumber)) {
      predicates.add(cb.equal(root.get(EserviceView_.VERSION_NUMBER), versionNumber));
    }

    Predicate predicateEserviceName = cb.and(predicates.toArray(new Predicate[] {}));

    return cb.and(predicateEserviceName,
        root.get(EserviceView_.STATE).as(String.class).in(stateList),
        cb.isTrue(root.get(EserviceView_.PROBING_ENABLED)),
        cb.isNotNull(root.get(EserviceView_.LAST_REQUEST)),
        cb.or(
            cb.lessThan(extractMinute,
                cb.prod(root.get(EserviceView_.POLLING_FREQUENCY), minOfTolleranceMultiplier)),
            cb.greaterThan(root.get(EserviceView_.RESPONSE_RECEIVED),
                root.get(EserviceView_.LAST_REQUEST))),
        cb.isNotNull(root.get(EserviceView_.RESPONSE_RECEIVED)));
  }
}
