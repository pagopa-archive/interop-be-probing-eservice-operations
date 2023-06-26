package it.pagopa.interop.probing.eservice.operations.util;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.TimestampType;

public class FunctionContributorTest implements MetadataBuilderContributor {

  @Override
  public void contribute(MetadataBuilder metadataBuilder) {
    metadataBuilder.applySqlFunction("make_interval", new SQLFunctionTemplate(
        TimestampType.INSTANCE, "DATE_TRUNC('minute',?1) + MAKE_INTERVAL(mins => ?2)"));

    metadataBuilder.applySqlFunction("compare_timestamp_interval",
        new SQLFunctionTemplate(BooleanType.INSTANCE, "CURRENT_TIME between ?1 and ?2"));

    metadataBuilder.applySqlFunction("extract_minute",
        new SQLFunctionTemplate(IntegerType.INSTANCE, "TRUNC(DATEDIFF(mi,CURRENT_TIMESTAMP,?1))"));
  }
}
