package it.pagopa.interop.probing.eservice.operations.repository.specs;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;

public class EserviceViewSpecs {

  private EserviceViewSpecs() {}

  public static Specification<EserviceView> searchSpecBuilder(String eserviceName,
      String eserviceVersion, Integer versionNumber, List<String> stateList) {
    return Specification.where(eserviceNameEquals(eserviceName))
        .and(producerNameEquals(eserviceVersion)).and(versionNumberEquals(versionNumber))
        .and(eserviceStateIn(stateList));
  }

  public static Specification<EserviceView> eserviceNameEquals(String eserviceName) {
    return ((root, query, builder) -> eserviceName == null ? builder.conjunction()
        : builder.equal(root.get("eserviceName"), eserviceName));
  }

  public static Specification<EserviceView> producerNameEquals(String producerName) {
    return (root, query, builder) -> producerName == null ? builder.conjunction()
        : builder.equal(root.get("producerName"), producerName);
  }

  public static Specification<EserviceView> versionNumberEquals(Integer version) {
    return (root, query, builder) -> version == null ? builder.conjunction()
        : builder.equal(root.get("versionNumber"), version);
  }

  public static Specification<EserviceView> eserviceStateIn(List<String> state) {
    return (root, query, builder) -> state == null || state.isEmpty() ? builder.conjunction()
        : root.get("state").in(state);
  }

}
