package com.piche.task.handler;

import com.piche.task.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class GlobalBadRequestExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<Object> defaultErrorHandler(HttpServletRequest req, Exception e) {
        return ResponseEntity.badRequest().body(new JSONObject()
                .put("statusCode", HttpStatus.BAD_REQUEST.value())
                .put("message", e.getMessage())
                .toMap());
    }
}