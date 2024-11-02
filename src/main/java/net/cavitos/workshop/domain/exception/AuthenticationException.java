package net.cavitos.workshop.domain.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {

    private final String message;

    public AuthenticationException(final String message) {

        this.message = message;
    }
}
