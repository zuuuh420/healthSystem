package com.health.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        if (e.getMessage() != null && e.getMessage().contains("权限不足")) {
            return Result.error(403, e.getMessage());
        }
        return Result.error(400, e.getMessage());
    }
}
