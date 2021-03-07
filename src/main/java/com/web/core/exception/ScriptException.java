package com.web.core.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScriptException extends FrameworkException {
    private static final Logger log = LogManager.getLogger(ScriptException.class);

    public ScriptException(String message) {
        super (message);
        log.error (message);
    }
}
