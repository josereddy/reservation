package com.example.ReservationTimings.Exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;


@ControllerAdvice
public class Custom_ResponseEntityExceptionalHandler extends ResponseEntityExceptionHandler {


    private static final Logger log = LogManager.getLogger(Custom_ResponseEntityExceptionalHandler.class.getName());



    @ExceptionHandler( BookingExceedException.class)
    public final ResponseEntity<Object> handleBookingExceedException( BookingExceedException ex, WebRequest request) {
        log.error("Exception : BookingExceedException");
        ExceptionResponse er = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }




    @ExceptionHandler(DateFormatException.class)
    public final ResponseEntity<Object> handleDateFormatException(DateFormatException ex, WebRequest request) {
        log.error("Exception :INVALIDDateFormatException");
        ExceptionResponse er = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(InvalidTimeFormatException.class)
    public final ResponseEntity<Object> handleInvalidTimeFormatException(InvalidTimeFormatException ex, WebRequest request) {
        log.error("Exception :INVALIDTIMEFormatException");
        ExceptionResponse er = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorisedException.class)
    public final ResponseEntity<Object> handleUserDataNotCorrect(UnauthorisedException ex, WebRequest request) {
        log.error("Exception :UserDataIncorrectFormatException");
        ExceptionResponse er = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserDataIncorrectFormatException.class)
    public final ResponseEntity<Object> handleUserDataNotCorrect(UserNotFoundException ex, WebRequest request) {
        log.error("Exception :UserDataIncorrectFormatException");
        ExceptionResponse er = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateUserFoundException.class)
    public final ResponseEntity<Object> handleUserDataNotCorrect(DuplicateUserFoundException ex, WebRequest request) {
        log.error("Exception :DuplicateUserFoundException");
        ExceptionResponse er = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public final ResponseEntity<Object> handleUsernameaNotfound(UsernameNotFoundException ex, WebRequest request) {
        log.error("Exception :UsernamenotFoundException");
        ExceptionResponse er = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NoFieldPresentException.class)
    public final ResponseEntity<Object> handleNOFieldPresentException(NoFieldPresentException ex, WebRequest request) {
        log.error("Exception :NoFieldPresentException");
        ExceptionResponse er = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(er, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(DuplicateLocationCodeFoundException.class)
    public final ResponseEntity<Object> handleException(DuplicateLocationCodeFoundException ex, WebRequest request) {
        log.error("Exception :DuplicateLocationCodeFoundException");
        ExceptionResponse er = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        log.error("Exception :UserNotFoundException");
        ExceptionResponse er = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(er, HttpStatus.NOT_FOUND);
    }

    @Override
    protected final ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Exception: handleHttpRequestMethodNotSupported");
        ExceptionResponse er = new ExceptionResponse(new Date(), "Please check Are you using the exact Http CAll  ", request.getDescription(false));
        return new ResponseEntity<Object>(er, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Exception: handleMethodArgumentNotValid");
        ExceptionResponse er = new ExceptionResponse(new Date(), "Size exceeded for the order status  ", request.getDescription(false));
        return new ResponseEntity<Object>(er, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("EXCEPTION:handleHttpMediaTypeNotAcceptable");
        ExceptionResponse er = new ExceptionResponse(new Date(), "Do not enter Duplicate" + ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(er, HttpStatus.NOT_FOUND);
    }


}