package by.own.sharedsources.exception.handler;

import by.own.sharedsources.exception.InternalServerError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<ErrorResponseDto> handleInternalServerException(InternalServerError e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorResponseDto(e.getMessage()));
    }
}