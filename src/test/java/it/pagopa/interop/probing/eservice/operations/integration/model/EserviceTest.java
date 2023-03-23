package it.pagopa.interop.probing.eservice.operations.integration.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.UUID;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.RandomStringUtils;
import org.hsqldb.HsqlException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import it.pagopa.interop.probing.eservice.operations.model.Eservice;
import it.pagopa.interop.probing.eservice.operations.util.EserviceTechnology;

@DataJpaTest
class EserviceTest {

    @Autowired
    private TestEntityManager testEntityManager;

    Eservice eservice;

    @BeforeEach
    void setup(){
        eservice = new Eservice();
        eservice.setState(EserviceState.INACTIVE);
        eservice.setEserviceId(UUID.randomUUID());
        eservice.setVersionId(UUID.randomUUID());
        eservice.setEserviceName("e-service1");
        eservice.setBasePath(new String[] {"test1", "test2"});
        eservice.setTechnology(EserviceTechnology.REST);
        eservice.setProducerName("producer1");
    }

    @Test
    @DisplayName("e-service is saved with default values")
    void testEserviceEntity_whenDefaultValuesAreSet_returnsEservice(){
        Eservice eserviceDuplicate = testEntityManager.persistAndFlush(eservice);
        assertEquals(OffsetTime.of(23, 59, 0, 0, ZoneOffset.UTC),
                eserviceDuplicate.getPollingEndTime(), "Values should be equal");
        assertEquals(OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC),
                eserviceDuplicate.getPollingStartTime(), "Values should be equal");
        assertEquals(5, eserviceDuplicate.getPollingFrequency(), "Values should be equal");
    }

    @Test
    @DisplayName("e-service is saved with non default values")
    void testEserviceEntity_whenDefaultValuesAreCorrectlyOverwritten_returnsEservice(){
        eservice.setPollingEndTime(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC));
        eservice.setPollingStartTime(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC));
        eservice.setProbingEnabled(true);
        eservice.setPollingFrequency(1);
        Eservice eserviceDuplicate = testEntityManager.persistAndFlush(eservice);
        assertEquals(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC),
                eserviceDuplicate.getPollingEndTime(), "Values should be equal");
        assertEquals(OffsetTime.of(1, 0, 0, 0, ZoneOffset.UTC),
                eserviceDuplicate.getPollingStartTime(), "Values should be equal");
        assertEquals(1, eserviceDuplicate.getPollingFrequency(), "Values should be equal");
        assertTrue(eserviceDuplicate.isProbingEnabled(), "Value should be true");
    }

    @Test
    @DisplayName("e-service isn't saved due to missing required data")
    void testEserviceEntity_whenEserviceDataNotProvided_throwsException(){
        Eservice emptyEservice = new Eservice();
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(emptyEservice),
                "e-service should not be saved when missing required data");
    }

    @Test
    @DisplayName("e-service isn't saved due to too long basePath value")
    void testEserviceEntity_whenBasePathIsTooLong_throwsException(){
        eservice.setBasePath(new String[]{RandomStringUtils.randomAlphabetic(2049)});
        assertThrows(HsqlException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when base path data is too long");
    }

    @Test
    @DisplayName("e-service isn't saved due to null basePath value")
    void testEserviceEntity_whenBasePathIsNull_throwsException(){
        eservice.setBasePath(null);
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when base path data is null");
    }

    @Test
    @DisplayName("e-service isn't saved due to too long e-service name value")
    void testEserviceEntity_whenEserviceNameIsTooLong_throwsException(){
        eservice.setEserviceName(RandomStringUtils.randomAlphabetic(256));
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when e-service name is too long");
    }

    @Test
    @DisplayName("e-service isn't saved due to null e-service name value")
    void testEserviceEntity_whenEserviceNameIsNull_throwsException(){
        eservice.setEserviceName(null);
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when e-service name is null");
    }

    @Test
    @DisplayName("e-service isn't saved due to blank e-service name value")
    void testEserviceEntity_whenEserviceNameIsBlank_throwsException(){
        eservice.setEserviceName("");
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when e-service name is blank");
    }

    @Test
    @DisplayName("e-service isn't saved due to null e-serviceId value")
    void testEserviceEntity_whenEserviceIdIsNull_throwsException(){
        eservice.setEserviceId(null);
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when e-service id is null");
    }

    @Test
    @DisplayName("e-service isn't saved due to null polling end time value")
    void testEserviceEntity_whenPollingEndTimeIsNull_throwsException(){
        eservice.setPollingEndTime(null);
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when e-service polling end time is null");
    }

    @Test
    @DisplayName("e-service isn't saved due to null polling frequency value")
    void testEserviceEntity_whenPollingFrequencyIsNull_throwsException(){
        eservice.setPollingFrequency(null);
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when e-service polling frequency is null");
    }

    @Test
    @DisplayName("e-service isn't saved due to polling frequency value equal to zero")
    void testEserviceEntity_whenPollingFrequencyIsZero_throwsException(){
        eservice.setPollingFrequency(0);
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when e-service polling frequency is zero");
    }

    @Test
    @DisplayName("e-service isn't saved due to polling frequency negative value")
    void testEserviceEntity_whenPollingFrequencyIsNegative_throwsException(){
        eservice.setPollingFrequency(-1);
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when e-service polling frequency is negative");
    }

    @Test
    @DisplayName("e-service isn't saved due to null polling start time value")
    void testEserviceEntity_whenPollingStartTimeIsNull_throwsException(){
        eservice.setPollingStartTime(null);
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when e-service polling start time is null");
    }

    @Test
    @DisplayName("e-service isn't saved due to too long producer name value")
    void testEserviceEntity_whenProducerNameIsTooLong_throwsException(){
        eservice.setProducerName(RandomStringUtils.randomAlphabetic(256));
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when producer name is too long");
    }

    @Test
    @DisplayName("e-service isn't saved due to null  producer name value")
    void testEserviceEntity_whenProducerNameIsNull_throwsException(){
        eservice.setProducerName(null);
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when e-service producer name is null");
    }

    @Test
    @DisplayName("e-service isn't saved due to blank producer name value")
    void testEserviceEntity_whenProducerNameIsBlank_throwsException(){
        eservice.setProducerName("");
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when producer name is blank");
    }

    @Test
    @DisplayName("e-service isn't saved due to null state value")
    void testEserviceEntity_whenStateIsNull_throwsException(){
        eservice.setState(null);
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when state is null");
    }

    @Test
    @DisplayName("e-service isn't saved due to null version id value")
    void testEserviceEntity_whenVersionIdIsNull_throwsException(){
        eservice.setVersionId(null);
        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved when version id is null");
    }

    @Test
    @DisplayName("e-service isn't saved due to e-service id and version id already existing")
    void testEserviceEntity_whenGivenDuplicatedEserviceIdAndVersionId_throwsException(){
        testEntityManager.persistAndFlush(eservice);
        Eservice duplicateEservice = new Eservice();
        duplicateEservice.setState(EserviceState.INACTIVE);
        duplicateEservice.setEserviceId(UUID.randomUUID());
        duplicateEservice.setVersionId(UUID.randomUUID());
        duplicateEservice.setEserviceName("e-service2");
        duplicateEservice.setBasePath(new String[] {"test1", "test2"});
        duplicateEservice.setTechnology(EserviceTechnology.REST);
        duplicateEservice.setProducerName("producer2");
        duplicateEservice.setVersionId(eservice.getVersionId());
        duplicateEservice.setEserviceId(eservice.getEserviceId());
        assertThrows(PersistenceException.class, () -> testEntityManager.persistAndFlush(duplicateEservice),
                "e-service should not be saved when e-service id and version id are already existing");
    }

    @Test
    @DisplayName("e-service isn't saved because id can't be manually updated")
    void testEserviceEntity_whenIdIsManuallyUpdated_throwsException(){
        eservice.setId(1L);
        assertThrows(PersistenceException.class, () -> testEntityManager.persistAndFlush(eservice),
                "e-service should not be saved because id shouldn't be manually updatable");
    }
}