package it.pagopa.interop.probing.eservice.operations.integration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
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
		EserviceView eserviceView = new EserviceView();
		eserviceView.setEserviceId(UUID.randomUUID());
		eserviceView.setVersionId(UUID.randomUUID());
		eserviceView.setEserviceName("e-service Name");
		eserviceView.setProducerName("Producer Name");
		eserviceView.setProbingEnabled(true);
		eserviceView.setVersionNumber(1);
		eserviceView.setState(EserviceState.ONLINE);
		eserviceView.setResponseReceived(OffsetDateTime.parse("2023-03-21T00:00:15.995Z"));
		eserviceView.setId(10L);
		testEntityManager.persistAndFlush(eserviceView);
	}

	@Test
	@DisplayName("the retrieved list of e-services is not empty")
	void testFindAll_whenExistsEservicesOnDatabase_thenReturnTheListNotEmpty() {
		List<EserviceState> listEservice = new ArrayList<>();
		listEservice.add(EserviceState.ONLINE);
		Specification<EserviceView> specs = EserviceViewSpecs.searchSpecBuilder("e-service Name", null, 1,
				listEservice);

		Page<EserviceView> result = repository.findAll(specs,
				new OffsetLimitPageable(0, 2, Sort.by(ProjectConstants.ESERVICE_NAME_FIELD).ascending()));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
	}

	@Test
	@DisplayName("the retrieved list of e-services is empty")
	void testFindAll_whenNotExistsEservicesOnDatabase_thenReturnTheListEmpty() {
		List<EserviceState> listEservice = new ArrayList<>();
		listEservice.add(EserviceState.ONLINE);
		Specification<EserviceView> specs = EserviceViewSpecs.searchSpecBuilder("e-service Name", null, 0,
				listEservice);

		Page<EserviceView> result = repository.findAll(specs,
				new OffsetLimitPageable(0, 2, Sort.by(ProjectConstants.ESERVICE_NAME_FIELD).ascending()));

		assertNotNull(result);
		assertEquals(0, result.getTotalElements());
	}
}
