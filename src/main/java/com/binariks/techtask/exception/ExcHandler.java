//package com.binariks.techtask.exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//@RestControllerAdvice
//public class ExcHandler extends ResponseEntityExceptionHandler {
//    @ExceptionHandler(OAuthExc.class)
//    public ResponseEntity<Object> handleOAuth2AuthenticationException(OAuthExc e) {
//        return buildExceptionBody(e, HttpStatus.FORBIDDEN);
//    }
//
//    private ResponseEntity<Object> buildExceptionBody(Exception exception, HttpStatus httpStatus) {
//        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
//                .status(httpStatus.value())
//                .message(exception.getMessage())
//                .build();
//        return ResponseEntity
//                .status(httpStatus)
//                .body(exceptionResponse);
//    }
//}
