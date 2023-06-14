package it.pagopa.interop.probing.eservice.operations.tracing.config;

import javax.servlet.Filter;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.proxies.apache.http.HttpClientBuilder;

@Configuration
public class TracingConfig {

  @Value("${spring.application.name}")
  private String awsXraySegmentName;
  @Value("${spring.datasource.username}")
  private String username;
  @Value("${spring.datasource.password}")
  private String password;
  @Value("${spring.datasource.url}")
  private String datasourceUrl;

  @Bean
  public Filter tracingFilter() {
    return new AWSXRayServletFilter(awsXraySegmentName);
  }

  @Bean
  public HttpClientBuilder xrayHttpClientBuilder() {

    return HttpClientBuilder.create();
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource dataSource() {
    return DataSourceBuilder.create().driverClassName("org.postgresql.Driver").url(datasourceUrl)
        .username(username).password(password).build();
  }
}
