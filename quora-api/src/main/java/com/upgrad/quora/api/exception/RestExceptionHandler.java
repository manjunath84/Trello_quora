package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    /**
     * This method handles all the SingUpRestrictedException throw by the Rest Controller
     *
     * @param exc SingUpRestrictedException
     * @param request Webrequest
     * @return ResponseEntity
     */
    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(SignUpRestrictedException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.CONFLICT
        );
    }

    /**
     * This method handles all the SingUpRestrictedException throw by the Rest Controller
     *
     * @param exc UserNotFoundException
     * @param request Webrequest
     * @return ResponseEntity
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(UserNotFoundException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * This method handles all the SingUpRestrictedException throw by the Rest Controller
     *
     * @param exc SignOutRestrictedException
     * @param request Webrequest
     * @return ResponseEntity
     */
    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(SignOutRestrictedException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * This method handles all the SingUpRestrictedException throw by the Rest Controller
     *
     * @param exc AuthenticationFailedException
     * @param request Webrequest
     * @return ResponseEntity
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

}
