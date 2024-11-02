package net.cavitos.workshop.domain.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    private final String message;
    private final String field;
    private final HttpStatus httpStatus;

    public BusinessException() {

        super("Internal Server Error");
        this.message = "Internal Server Error";
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.field = "";
    }

    public BusinessException(final String message) {

        super(message);
        this.message = message;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.field = "";
    }

    public BusinessException(final HttpStatus httpStatus, final String message) {

        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
        this.field = "";
    }

    public BusinessException(final HttpStatus httpStatus, 
                             final String message, 
                             final String field) {

        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
        this.field = field;
    }


    public String getMessage() {

        return message;
    }

    public String getField() {

        return field;
    }

    public HttpStatus getHttpStatus() {

        return httpStatus;
    }    
}
