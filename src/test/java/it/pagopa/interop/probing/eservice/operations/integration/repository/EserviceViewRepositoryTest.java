package it.pagopa.interop.probing.eservice.operations.integration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import it.pagopa.interop.probing.eservice.operations.dtos.SearchProducerNameResponse;
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

		Specification<EserviceView> specs = EserviceViewSpecs.searchSpecBuilder("e-service Name", null, 1,
				Arrays.asList(EserviceState.ONLINE));

		Page<EserviceView> resultFindAll = repository.findAll(specs,
				new OffsetLimitPageable(0, 2, Sort.by(ProjectConstants.ESERVICE_NAME_FIELD).ascending()));

		assertNotNull(resultFindAll);
		assertEquals(1, resultFindAll.getTotalElements());
	}

	@Test
	@DisplayName("the retrieved list of e-services is empty")
	void testFindAll_whenNotExistsEservicesOnDatabase_thenReturnTheListEmpty() {

		Specification<EserviceView> specs = EserviceViewSpecs.searchSpecBuilder("e-service Name", null, 0,
				Arrays.asList(EserviceState.ONLINE));

		Page<EserviceView> resultFindAll = repository.findAll(specs,
				new OffsetLimitPageable(0, 2, Sort.by(ProjectConstants.ESERVICE_NAME_FIELD).ascending()));

		assertNotNull(resultFindAll);
		assertEquals(0, resultFindAll.getTotalElements());
	}

	@Test
	@DisplayName("when a valid producer name is provided, then the method should return a non-empty list")
	void testGetEservicesProducers_whenGivenValidProducerName_thenReturnsEmptyList() {
		List<SearchProducerNameResponse> resultGetEservicesProducers = repository
				.getEservicesProducers("Producer Name".toUpperCase(), PageRequest.of(0, 10));
		assertEquals(1, resultGetEservicesProducers.size(), "the method should return a non-empty list");
	}

	@Test
	@DisplayName("when a producer name not saved on db is provided, then the method should return an empty list")
	void testGetEservicesProducers_whenGivenProducerNameNotStored_thenReturnsEmptyList() {
		List<SearchProducerNameResponse> resultGetEservicesProducers = repository
				.getEservicesProducers("Producer-To-Not-Found".toUpperCase(), PageRequest.of(0, 10));
		assertEquals(0, resultGetEservicesProducers.size(), "the method should return an empty list");
	}

	@Test
	@DisplayName("when a substring of a valid producer name is provided, then the method should return a non-empty list")
	void testGetEservicesProducers_whenGivenPartialProducerName_thenReturnsNonEmptyList() {
		List<SearchProducerNameResponse> resultGetEservicesProducers = repository
				.getEservicesProducers("pro".toUpperCase(), PageRequest.of(0, 10));
		assertEquals(1, resultGetEservicesProducers.size(), "the method should return a non-empty list");
	}

}
