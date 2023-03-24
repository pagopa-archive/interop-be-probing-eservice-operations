package it.pagopa.interop.probing.eservice.operations.repository.specs;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;

public class EserviceViewSpecs {

	public static Specification<EserviceView> searchSpecBuilder(String eserviceName, String eserviceVersion,
			Integer versionNumber, List<EserviceState> eServiceState) {
		return Specification.where(eserviceNameEquals(eserviceName)).and(eserviceVersionEquals(eserviceVersion))
				.and(versionNumberEquals(versionNumber)).and(eServiceStateIn(eServiceState));
	}

	public static Specification<EserviceView> eserviceNameEquals(String eserviceName) {
		return ((root, query, builder) -> eserviceName == null ? builder.conjunction()
				: builder.equal(root.get("eserviceName"), eserviceName));
	}

	public static Specification<EserviceView> eserviceVersionEquals(String eserviceVersion) {
		return (root, query, builder) -> eserviceVersion == null ? builder.conjunction()
				: builder.equal(root.get("versionId"), eserviceVersion);
	}

	public static Specification<EserviceView> probingEnabledEquals(Boolean probingEnabled) {
		return (root, query, builder) -> probingEnabled == null ? builder.conjunction()
				: builder.equal(root.get("probingEnabled"), probingEnabled);

	}

	public static Specification<EserviceView> versionNumberEquals(Integer version) {
		return (root, query, builder) -> version == null ? builder.conjunction()
				: builder.equal(root.get("version"), version);
	}

	public static Specification<EserviceView> eServiceStateIn(List<EserviceState> eServiceState) {
		return (root, query, builder) -> eServiceState == null || eServiceState.isEmpty() ? builder.conjunction()
				: root.get("state").in(eServiceState);
	}

}
