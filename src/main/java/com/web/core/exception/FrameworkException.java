package com.web.core.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FrameworkException extends Exception {
    private static final Logger log = LogManager.getLogger(FrameworkException.class);

    public FrameworkException(String message) {
        super(message);
        log.error(message);
    }
}
