package com.seven.deadly.sin.wrath.common.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

    private T body;
    private String message;

    public static <T> Response<T> of(T body) {
        return Response.<T>builder()
                .body(body)
                .message("success")
                .build();
    }
}
