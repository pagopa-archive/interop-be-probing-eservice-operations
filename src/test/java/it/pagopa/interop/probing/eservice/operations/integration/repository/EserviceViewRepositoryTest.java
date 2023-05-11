package it.pagopa.interop.probing.eservice.operations.integration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.pagopa.interop.probing.eservice.operations.model.Eservice_;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceInteropState;
import it.pagopa.interop.probing.eservice.operations.dtos.EserviceTechnology;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.repository.EserviceViewRepository;
import it.pagopa.interop.probing.eservice.operations.repository.specs.EserviceViewSpecs;
import it.pagopa.interop.probing.eservice.operations.util.OffsetLimitPageable;
import it.pagopa.interop.probing.eservice.operations.util.constant.ProjectConstants;

@DataJpaTest
class EserviceViewRepositoryTest {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private EserviceViewRepository repository;

  @BeforeEach
  void setup() {
    EserviceView eserviceView = EserviceView.builder().eserviceId(UUID.randomUUID())
        .versionId(UUID.randomUUID()).eserviceName("e-service Name").producerName("Producer Name")
        .probingEnabled(true).versionNumber(1).state(EserviceInteropState.ACTIVE)
        .responseReceived(OffsetDateTime.parse("2023-03-21T00:00:05.995Z"))
        .lastRequest(OffsetDateTime.parse("2023-03-21T00:00:15.995Z"))
        .technology(EserviceTechnology.REST).basePath(new String[] {"base_path_test"})
        .eserviceRecordId(10L).build();
    testEntityManager.persistAndFlush(eserviceView);
  }

  @Test
  @DisplayName("the retrieved list of e-services is not empty")
  void testFindAll_whenExistsEservicesOnDatabase_thenReturnTheListNotEmpty() {
    Specification<EserviceView> specs =
        EserviceViewSpecs.searchSpecBuilder("e-service Name", null, 1);

    Page<EserviceView> resultFindAll = repository.findAll(specs,
        new OffsetLimitPageable(0, 2, Sort.by(Eservice_.ESERVICE_NAME).ascending()));

    assertNotNull(resultFindAll);
    assertEquals(1, resultFindAll.getTotalElements());
  }

  @Test
  @DisplayName("the retrieved list of e-services is empty")
  void testFindAll_whenNotExistsEservicesOnDatabase_thenReturnTheListEmpty() {

    Specification<EserviceView> specs =
        EserviceViewSpecs.searchSpecBuilder("e-service Name", null, 0);

    Page<EserviceView> resultFindAll = repository.findAll(specs,
        new OffsetLimitPageable(0, 2, Sort.by(Eservice_.ESERVICE_NAME).ascending()));

    assertNotNull(resultFindAll);
    assertEquals(0, resultFindAll.getTotalElements());
  }

}
