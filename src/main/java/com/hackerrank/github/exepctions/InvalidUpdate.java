package com.hackerrank.github.exepctions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidUpdate extends Exception {

    public InvalidUpdate(String message) {
        super(message);
    }

}
