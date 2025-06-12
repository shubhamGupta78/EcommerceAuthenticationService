package org.example.ecommerceauthenticationservice.exceptions;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<HandleError> handleUserAlreadyExistException(UserAlreadyExistException e) {

        HandleError handleError = new HandleError();
        handleError.setMessage("User already exists");
        handleError.setErrorDescription(e.getMessage());
        return ResponseEntity.status(409).body(handleError);
    }

    @ExceptionHandler(RoleAlreadyExistException.class)
    public ResponseEntity<HandleError> handleRoleAlreadyExistException(RoleAlreadyExistException e) {

        HandleError handleError = new HandleError();
        handleError.setMessage("Role already exists");
        handleError.setErrorDescription(e.getMessage());
        return ResponseEntity.status(409).body(handleError);
    }

    @ExceptionHandler(UserNotFoundExceptions.class)
    public ResponseEntity<HandleError> handleUserNotFoundException(UserNotFoundExceptions e) {

        HandleError handleError = new HandleError();
        handleError.setMessage("User not found");
        handleError.setErrorDescription(e.getMessage());
        return ResponseEntity.status(404).body(handleError);
    }

    @ExceptionHandler(IncorrectCredentialExceptions.class)
    public ResponseEntity<HandleError> handleIncorrectCredentialException(IncorrectCredentialExceptions e) {

        HandleError handleError = new HandleError();
        handleError.setMessage("Incorrect credentials");
        handleError.setErrorDescription(e.getMessage());
        return ResponseEntity.status(401).body(handleError);
    }
    @ExceptionHandler(otpExpiredException.class)
    public ResponseEntity<HandleError> handleOtpExpiredException(otpExpiredException e) {

        HandleError handleError = new HandleError();
        handleError.setMessage("OTP expired");
        handleError.setErrorDescription(e.getMessage());
        return ResponseEntity.status(410).body(handleError);
    }
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<HandleError> handleInvalidOtpException(InvalidOtpException e) {

        HandleError handleError = new HandleError();
        handleError.setMessage("Invalid OTP");
        handleError.setErrorDescription(e.getMessage());
        return ResponseEntity.status(400).body(handleError);
    }
    @ExceptionHandler(UserNotLoggedInExceptions.class)
    public ResponseEntity<HandleError> handleUserNotLoggedInException(UserNotLoggedInExceptions e) {

        HandleError handleError = new HandleError();
        handleError.setMessage("User not logged in");
        handleError.setErrorDescription(e.getMessage());
        return ResponseEntity.status(403).body(handleError);
    }


    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<HandleError> handleMissingRequestHeaderException(MissingRequestHeaderException e) {

        HandleError handleError = new HandleError();
        handleError.setMessage("Missing authorization token");
        handleError.setErrorDescription(e.getMessage());
        return ResponseEntity.status(400).body(handleError);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<HandleError> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e) {

        HandleError handleError = new HandleError();
        handleError.setMessage("Invalid data access API usage please check your request");
        handleError.setErrorDescription(e.getMessage());
        return ResponseEntity.status(400).body(handleError);
    }





}
