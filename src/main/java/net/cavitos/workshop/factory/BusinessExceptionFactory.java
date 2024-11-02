package net.cavitos.workshop.factory;

import net.cavitos.workshop.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;

import static java.lang.String.format;

public final class BusinessExceptionFactory {

    public static BusinessException createBusinessException(final HttpStatus httpStatus,
                                                            final String message,
                                                            final Object ... values) {

        return new BusinessException(httpStatus, format(message, values));
    }

    public static BusinessException createBusinessException(final String message,
                                                      final Object ... values) {

        return createBusinessException(HttpStatus.INTERNAL_SERVER_ERROR, message, values);
    }
}
