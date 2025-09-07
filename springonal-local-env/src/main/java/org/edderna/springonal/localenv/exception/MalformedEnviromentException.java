package org.edderna.springonal.localenv.exception;

import java.util.List;
import java.util.stream.Collectors;

public class MalformedEnviromentException extends RuntimeException {

    public MalformedEnviromentException(String message) {
        super(message);
    }
}
