package it.pagopa.interop.probing.eservice.operations.tracing.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import com.amazonaws.xray.AWSXRay;
import it.pagopa.interop.probing.eservice.operations.util.logging.LoggingPlaceholders;

@Aspect
@Component
public class XRayAopConfig {

  @Before("execution(* it.pagopa.interop.probing.eservice.operations.rest..*(..))")
  public void beforeController(JoinPoint joinPoint) {
    MDC.put(LoggingPlaceholders.TRACE_ID_XRAY_PLACEHOLDER,
        LoggingPlaceholders.TRACE_ID_XRAY_MDC_PREFIX
            + AWSXRay.getCurrentSegment().getTraceId().toString() + "]");
  }

  @Before("execution(* it.pagopa.interop.probing.eservice.operations.repository.query.builder..*(..))")
  public void beforeQueryOperation(JoinPoint joinPoint) {
    AWSXRay.beginSubsegment(LoggingPlaceholders.AURORA_SUBSEGMENT_NAME);
  }

  @After("execution(* it.pagopa.interop.probing.eservice.operations.repository.query.builder..*(..))")
  public void afterQueryOperation(JoinPoint joinPoint) {
    AWSXRay.endSubsegment();
  }
}
