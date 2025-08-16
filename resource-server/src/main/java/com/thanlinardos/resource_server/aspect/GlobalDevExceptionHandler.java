package com.thanlinardos.resource_server.aspect;

import com.thanlinardos.resource_server.model.info.ProblemDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Profile("dev")
@RestControllerAdvice
@Slf4j
public class GlobalDevExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleException(Exception ex) throws Exception {
        if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null) {
            throw ex;
        }

        String method = null;
        String uri = null;
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest request = attributes.getRequest();
            method = request.getMethod();
            uri = request.getRequestURI();
        }

        ProblemDetails error = new ProblemDetails();
        if (ex.getCause() != null) {
            error.setCause(ex.getCause().getMessage());
        }
        error.setTitle("[" + ex.getClass() + "] An unexpected internal server error occurred while processing the " + method + " request.");
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setDetail(ex.getMessage());
        error.setType(uri);

        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
