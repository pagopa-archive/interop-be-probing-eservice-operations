package it.pagopa.interop.probing.eservice.operations.util.logging;

public class LoggingPlaceholders {
  private LoggingPlaceholders() {}

  public static final String TRACE_ID_PLACEHOLDER = "trace_id";
  public static final String TRACE_ID_XRAY_PLACEHOLDER = "AWS-XRAY-TRACE-ID";
  public static final String TRACE_ID_XRAY_MDC_PREFIX = "- [TRACE_ID= ";
  public static final String AURORA_SUBSEGMENT_NAME = "Aurora Operation";
}
