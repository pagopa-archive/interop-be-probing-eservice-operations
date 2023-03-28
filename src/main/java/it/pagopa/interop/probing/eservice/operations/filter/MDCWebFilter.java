package it.pagopa.interop.probing.eservice.operations.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import it.pagopa.interop.probing.eservice.operations.util.constant.LoggingPlaceholders;

@Component
public class MDCWebFilter extends OncePerRequestFilter {

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			response.addHeader("Access-Control-Allow-Origin", "*");
			response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
			response.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with");
			response.addHeader("Access-Control-Max-Age", "3600");
			if ("OPTIONS".equals(request.getMethod())) {
				response.setStatus(HttpServletResponse.SC_OK);
			}
			MDC.put(LoggingPlaceholders.TRACE_ID_PLACEHOLDER,
					"- [CID= " + UUID.randomUUID().toString().toLowerCase() + "]");

			filterChain.doFilter(request, response);
		} finally {
			MDC.remove(LoggingPlaceholders.TRACE_ID_PLACEHOLDER);
		}
	}
}
