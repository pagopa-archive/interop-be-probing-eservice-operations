package it.pagopa.interop.probing.eservice.operations.util;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.BooleanType;
import org.hibernate.type.TimestampType;

public class FunctionContributor implements MetadataBuilderContributor {

  @Override
  public void contribute(MetadataBuilder metadataBuilder) {
    metadataBuilder.applySqlFunction("make_interval",
        new SQLFunctionTemplate(TimestampType.INSTANCE, "?1 + make_interval(mins => ?2)"));

    metadataBuilder.applySqlFunction("compare_timestamp_interval",
        new SQLFunctionTemplate(BooleanType.INSTANCE, "CURRENT_TIME between ?1 and ?2"));
  }
}
