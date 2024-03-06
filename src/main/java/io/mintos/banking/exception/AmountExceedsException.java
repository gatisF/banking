package io.mintos.banking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AmountExceedsException extends RuntimeException{

    public AmountExceedsException(String message) {
        super(message);
    }
}
