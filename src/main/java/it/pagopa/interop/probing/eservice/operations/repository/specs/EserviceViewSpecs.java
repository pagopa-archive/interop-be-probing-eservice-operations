package it.pagopa.interop.probing.eservice.operations.repository.specs;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import it.pagopa.interop.probing.eservice.operations.dtos.EserviceState;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;

public class EserviceViewSpecs {

	private EserviceViewSpecs() {
	}

	public static Specification<EserviceView> searchSpecBuilder(String eserviceName, String producerName,
			Integer versionNumber, List<EserviceState> state) {
		return Specification.where(eserviceNameEquals(eserviceName)).and(producerNameEquals(producerName))
				.and(versionNumberEquals(versionNumber)).and(eserviceStateIn(state));
	}

	public static Specification<EserviceView> eserviceNameEquals(String eserviceName) {
		return ((root, query, builder) -> eserviceName == null ? builder.conjunction()
				: builder.equal(builder.upper(root.get("eserviceName")), eserviceName.toUpperCase()));
	}

	public static Specification<EserviceView> producerNameEquals(String producerName) {
		return ((root, query, builder) -> producerName == null ? builder.conjunction()
				: builder.equal(builder.upper(root.get("producerName")), producerName.toUpperCase()));
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
				: builder.equal(root.get("versionNumber"), version);
	}

	public static Specification<EserviceView> eserviceStateIn(List<EserviceState> state) {
		return (root, query, builder) -> state == null || state.isEmpty() ? builder.conjunction()
				: root.get("state").in(state);
	}

}
