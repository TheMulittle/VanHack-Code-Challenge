package com.hackerrank.github.exepctions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFound extends Exception {

    public NotFound(String message) {
        super(message);
    }

}