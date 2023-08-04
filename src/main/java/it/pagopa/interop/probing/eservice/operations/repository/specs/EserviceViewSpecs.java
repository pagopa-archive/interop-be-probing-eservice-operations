package it.pagopa.interop.probing.eservice.operations.repository.specs;

import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView;
import it.pagopa.interop.probing.eservice.operations.model.view.EserviceView_;

public class EserviceViewSpecs {

  private EserviceViewSpecs() {

  }

  public static Specification<EserviceView> searchSpecBuilder(String eserviceName,
      String eserviceVersion, Integer versionNumber) {
    return Specification.where(eserviceNameEquals(eserviceName))
        .and(producerNameEquals(eserviceVersion)).and(versionNumberEquals(versionNumber));
  }

  public static Specification<EserviceView> eserviceNameEquals(String eserviceName) {
    return ((root, query, builder) -> Objects.isNull(eserviceName) ? builder.conjunction()
        : builder.like(builder.upper(root.get(EserviceView_.ESERVICE_NAME)),
            "%" + eserviceName.toUpperCase() + "%"));
  }

  public static Specification<EserviceView> producerNameEquals(String producerName) {
    return (root, query, builder) -> Objects.isNull(producerName) ? builder.conjunction()
        : builder.equal(builder.upper(root.get(EserviceView_.PRODUCER_NAME)),
            producerName.toUpperCase());
  }

  public static Specification<EserviceView> versionNumberEquals(Integer version) {
    return (root, query, builder) -> Objects.isNull(version) ? builder.conjunction()
        : builder.equal(root.get(EserviceView_.VERSION_NUMBER), version);
  }



}
