package com.seven.deadly.sin.wrath.logging.interceptor;

import com.seven.deadly.sin.wrath.logging.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class OutboundLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(OutboundLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String traceId = MDC.get("traceId");

        long start = System.currentTimeMillis();

        log.info("OUTBOUND_REQUEST traceId={} method={} uri={} body={}",
                traceId,
                request.getMethod(),
                request.getURI(),
                LoggingUtil.mask(new String(body)));

        // propagate traceId
        request.getHeaders().add("X-Trace-Id", traceId);

        ClientHttpResponse response = execution.execute(request, body);

        long duration = System.currentTimeMillis() - start;

        String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);

        log.info("OUTBOUND_RESPONSE traceId={} status={} duration={}ms body={}",
                traceId,
                response.getStatusCode(),
                duration,
                LoggingUtil.mask(responseBody));

        return response;
    }
}
