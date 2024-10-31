package com.piche.task.handler;

import com.piche.task.exception.BadRequestException;
import com.piche.task.exception.UnknownAccountIdException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class GlobalBadRequestExceptionHandler {

    @ExceptionHandler(value = { BadRequestException.class, UnknownAccountIdException.class })
    public ResponseEntity<Object> defaultErrorHandler(HttpServletRequest req, Exception e) {
        return ResponseEntity.badRequest().body(ErrorMessage.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage()));
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class ErrorMessage {

        private int statusCode;

        private String message;

    }
}