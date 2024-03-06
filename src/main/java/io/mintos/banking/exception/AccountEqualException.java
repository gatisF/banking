package io.mintos.banking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AccountEqualException extends RuntimeException {
    public AccountEqualException(String message) {
        super(message);
    }
}
