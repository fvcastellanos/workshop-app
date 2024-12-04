package net.cavitos.workshop.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfiguration {

    @Bean
    public Clock systemClock(@Value("${zone.default:UTC}") String defaultZoneId) {

        final var zoneId = ZoneId.of(defaultZoneId);
        return Clock.system(zoneId);
    }
}
