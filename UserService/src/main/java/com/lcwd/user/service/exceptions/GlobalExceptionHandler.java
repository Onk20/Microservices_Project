package com.lcwd.user.service.exceptions;


import com.lcwd.user.service.payload.ApiResponse;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResouceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResouceNotFoundException e){
        String message = e.getMessage();
        ApiResponse response = ApiResponse.builder().message(message).success(false).status(HttpStatus.NOT_FOUND).build();
        return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({FeignException.class, CallNotPermittedException.class})
    public ResponseEntity<ApiResponse> handleDependencyUnavailable(RuntimeException exception) {
        ApiResponse response = ApiResponse.builder()
                .message("A required service is unavailable. Please retry later.")
                .success(false).status(HttpStatus.SERVICE_UNAVAILABLE).build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
