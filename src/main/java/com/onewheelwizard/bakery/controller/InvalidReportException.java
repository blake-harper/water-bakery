package com.onewheelwizard.bakery.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidReportException extends RuntimeException {
    public InvalidReportException(String reportType) {
        super("invalid " + reportType);
    }
}
