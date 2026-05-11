package com.seven.deadly.sin.wrath.logging.filter;

import com.seven.deadly.sin.wrath.logging.util.LoggingUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class InboundLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(InboundLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);

        long start = System.currentTimeMillis();

        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper res = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(req, res);

        long duration = System.currentTimeMillis() - start;

        String requestBody = new String(req.getContentAsByteArray(), StandardCharsets.UTF_8);

        log.info("INBOUND_REQUEST traceId={} method={} uri={} body={}",
                traceId,
                request.getMethod(),
                request.getRequestURI(),
                LoggingUtil.mask(requestBody)
        );

        String responseBody = new String(res.getContentAsByteArray(), StandardCharsets.UTF_8);

        log.info("INBOUND_RESPONSE traceId={} status={} duration={}ms body={}",
                traceId,
                response.getStatus(),
                duration,
                LoggingUtil.mask(responseBody)
        );

        res.copyBodyToResponse();
        MDC.clear();
    }
}
