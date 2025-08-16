package com.thanlinardos.resource_server.aspect;

import com.thanlinardos.resource_server.model.info.ProblemDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Profile("prod")
@RestControllerAdvice
@Slf4j
public class GlobalProdExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleException(Exception ex) {
        ProblemDetails error = new ProblemDetails();
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setDetail("An unexpected internal server error occurred while processing the request.");
        error.setTitle(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
