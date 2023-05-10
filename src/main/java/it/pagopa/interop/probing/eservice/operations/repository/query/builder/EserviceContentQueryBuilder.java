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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceContent;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.model.EserviceContentCriteria;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView_;
import it.pagopa.interop.probing.eservice.operations.util.constant.ProjectConstants;

@Repository
public class EserviceContentQueryBuilder {

  @PersistenceContext
  private EntityManager entityManager;

  public Page<EserviceContent> findAllEservicesReadyForPolling(Integer limit, Integer offset) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<EserviceContentCriteria> query = cb.createQuery(EserviceContentCriteria.class);
    Root<EserviceView> root = query.from(EserviceView.class);

    query.distinct(true).multiselect(root.get(EserviceView_.ESERVICE_RECORD_ID),
        root.get(EserviceView_.TECHNOLOGY), root.get(EserviceView_.BASE_PATH));

    Expression<Timestamp> makeInterval = cb.function("make_interval", Timestamp.class,
        root.get(EserviceView_.LAST_REQUEST), root.get(EserviceView_.POLLING_FREQUENCY));

    Expression<Boolean> compareTimestampInterval =
        cb.function("compare_timestamp_interval", Boolean.TYPE,
            root.get(EserviceView_.POLLING_START_TIME), root.get(EserviceView_.POLLING_END_TIME));

    Predicate predicate =
        cb.and(cb.equal(root.get(EserviceView_.STATE), EserviceInteropState.ACTIVE),
            cb.isTrue(root.get(EserviceView_.PROBING_ENABLED)),
            cb.or(
                cb.and(cb.isNull(root.get(EserviceView_.LAST_REQUEST)),
                    cb.isNull(root.get(EserviceView_.RESPONSE_RECEIVED))),
                cb.and(cb.lessThanOrEqualTo(makeInterval, cb.currentTimestamp()),
                    cb.lessThanOrEqualTo(root.get(EserviceView_.LAST_REQUEST),
                        root.get(EserviceView_.RESPONSE_RECEIVED)))),
            cb.isTrue(compareTimestampInterval));

    query.where(predicate);
    TypedQuery<EserviceContentCriteria> q = entityManager.createQuery(query);
    List<EserviceContent> pollingActiveEserviceContent =
        q.getResultList().stream().map(e -> (EserviceContent) e).toList();

    return new PageImpl<>(pollingActiveEserviceContent,
        PageRequest.of(offset, limit,
            Sort.by(ProjectConstants.ESERVICES_RECORD_ID_FIELD).ascending()),
        pollingActiveEserviceContent.size());
  }
}
