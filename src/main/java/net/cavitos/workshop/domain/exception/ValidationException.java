package net.cavitos.workshop.domain.exception;

import net.cavitos.workshop.domain.model.error.FieldError;

import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<FieldError> fieldErrors;

    public ValidationException(final List<FieldError> fieldErrors) {

        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() {

        return fieldErrors;
    }
}
