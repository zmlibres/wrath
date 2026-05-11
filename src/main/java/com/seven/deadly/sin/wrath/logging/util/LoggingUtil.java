package com.seven.deadly.sin.wrath.logging.util;

import org.slf4j.MDC;

public class LoggingUtil {

    public static String getTraceId() {
        return MDC.get("traceId");
    }

    public static String mask(String body) {
        if (body == null) return null;
        return body.replaceAll("\"password\":\".*?\"", "\"password\":\"***\"");
    }
}
