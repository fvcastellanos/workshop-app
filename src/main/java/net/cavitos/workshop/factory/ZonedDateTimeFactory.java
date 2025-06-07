package net.cavitos.workshop.factory;

import org.apache.commons.lang3.StringUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.isNull;

public class ZonedDateTimeFactory {

    private static final Clock UTC_CLOCK = Clock.systemUTC();

    private final Clock systemClock;

    public ZonedDateTimeFactory(final Clock systemClock) {

        this.systemClock = systemClock;
    }

    public Instant buildInstantFrom(final String date) {

        if (StringUtils.isBlank(date)) {

            return null;
        }

        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
                .atStartOfDay(systemClock.getZone())
                .toInstant();
    }

    public String buildStringFromInstant(final Instant instant) {

        if (isNull(instant)) {

            return null;
        }

        final var formatter = DateTimeFormatter.ISO_LOCAL_DATE
                .withZone(systemClock.getZone());

        return formatter.format(instant);
    }

    public Instant buildInstantFromLocalDate(final LocalDate localDate) {

        if (isNull(localDate)) {

            return null;
        }

        return localDate.atStartOfDay(systemClock.getZone())
                .toInstant();
    }

    public Instant getUTCNow() {

        return Instant.now(UTC_CLOCK);
    }

    public Instant getSystemNow() {

        return Instant.now(systemClock);
    }

    public ZoneOffset getZoneOffset(final LocalDateTime localDateTime) {

        return systemClock.getZone()
                .getRules()
                .getOffset(localDateTime);
    }
}
