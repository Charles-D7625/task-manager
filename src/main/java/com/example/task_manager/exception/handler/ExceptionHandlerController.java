package com.example.task_manager.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.task_manager.exception.EntityNotFoundException;
import com.example.task_manager.exception.ExistingEntityException;
import com.example.task_manager.exception.AccessDeniedException;
import com.example.task_manager.exception.IllegalArgumentException;
import com.example.task_manager.exception.dto.ErrorDto;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> entityNotFoundException(EntityNotFoundException e) {

        return createResponseEntity(
            HttpStatus.NOT_FOUND, 
            createErrorDto(e, HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(ExistingEntityException.class)
    public ResponseEntity<ErrorDto> existingEntityException(ExistingEntityException e) {

        return createResponseEntity(
            HttpStatus.NOT_FOUND, 
            createErrorDto(e, HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> accessDeniedException(AccessDeniedException e) {

        return createResponseEntity(
            HttpStatus.FORBIDDEN, 
            createErrorDto(e, HttpStatus.FORBIDDEN, e.getMessage()));
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> illegalArgumentException(IllegalArgumentException e) {

        return createResponseEntity(
            HttpStatus.BAD_REQUEST, 
            createErrorDto(e, HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    private <T> ResponseEntity<T> createResponseEntity(HttpStatus status, T body) {
        return ResponseEntity.status(status)
                .header("Content-Type", "application/json")
                .body(body);
    }

    private ErrorDto createErrorDto(Exception e, HttpStatus httpStatus, String errorMessage) {
        if (e.getStackTrace().length == 0) {
            return ErrorDto.builder()
                    .errorCode(String.valueOf(httpStatus))
                    .errorMessage(errorMessage)
                    .build();
        }
        StackTraceElement element = e.getStackTrace()[2];
        String[] splitArray = element.getClassName().split("\\.");
        int arrayLength = splitArray.length;

        return ErrorDto.builder()
                .errorCode(String.valueOf(httpStatus))
                .errorMessage(errorMessage)
                .errorClass(splitArray[arrayLength - 1])
                .errorMethod(element.getMethodName())
                .build();
    }
}
