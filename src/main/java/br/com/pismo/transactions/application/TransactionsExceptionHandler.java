package br.com.pismo.transactions.application;

import br.com.pismo.transactions.external.exceptions.AccountNotFoundException;
import br.com.pismo.transactions.external.exceptions.InsufficientFundsException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class TransactionsExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = AccountNotFoundException.class )
    protected ResponseEntity<Object> handleAccountNotFoundException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = InsufficientFundsException.class )
    protected ResponseEntity<Object> handleInsufficientFundsException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

}
