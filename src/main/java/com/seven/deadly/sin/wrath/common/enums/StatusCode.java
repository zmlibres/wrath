package com.seven.deadly.sin.wrath.common.enums;

import org.springframework.http.HttpStatus;

public enum StatusCode {

    SUCCESS(200, "Success."),
    CREATED(201, "created"),
    NOT_FOUND(404, "Not found."),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    BAD_REQUEST(400, "Bad Request"),
    INTERNAL_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
