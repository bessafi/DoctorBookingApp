package shujaa.authentication_with_spring.security.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(AuthException.UserAlreadyExistsException.class)
    public ResponseEntity<Error> handleUserAlreadyExistsException(AuthException.UserAlreadyExistsException ex) {
        return new ResponseEntity<>(new Error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthException.UsernameAlreadyExistsException.class)
    public ResponseEntity<Error> handleUsernameAlreadyExistsException(AuthException.UsernameAlreadyExistsException ex) {
        return new ResponseEntity<>(new Error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthException.UserNotFoundException.class)
    public ResponseEntity<Error> handleUserNotFoundException(AuthException.UserNotFoundException ex) {
        return new ResponseEntity<>(new Error(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthException.AccountNotVerifiedException.class)
    public ResponseEntity<Error> handleAccountNotVerifiedException(AuthException.AccountNotVerifiedException ex) {
        return new ResponseEntity<>(new Error(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthException.VerificationCodeExpiredException.class)
    public ResponseEntity<Error> handleVerificationCodeExpiredException(AuthException.VerificationCodeExpiredException ex) {
        return new ResponseEntity<>(new Error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthException.InvalidVerificationCodeException.class)
    public ResponseEntity<Error> handleInvalidVerificationCodeException(AuthException.InvalidVerificationCodeException ex) {
        return new ResponseEntity<>(new Error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGlobalException(Exception ex) {
        return new ResponseEntity<>(new Error("An unexpected error occurred: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
