package net.cavitos.workshop.factory;

import org.apache.commons.lang3.StringUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.time.ZoneOffset.UTC;

public final class DateTimeFactory {

    private DateTimeFactory() {
    }

    public static Instant buildInstantFrom(final String date) {

        if (StringUtils.isBlank(date)) {

            return null;
        }

        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
                .atStartOfDay()
                .toInstant(UTC);
    }

    public static String buildStringFromInstant(final Instant instant) {

        if (Objects.isNull(instant)) {

            return null;
        }

        final var formatter = DateTimeFormatter.ISO_LOCAL_DATE
                .withZone(ZoneId.of(UTC.getId()));

        return formatter.format(instant);
    }

    public static Instant getUTCNow() {

        return Instant.now(Clock.systemUTC());
    }
}
