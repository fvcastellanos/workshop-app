package net.cavitos.workshop.sequence.provider;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import net.cavitos.workshop.domain.exception.BusinessException;
import net.cavitos.workshop.factory.BusinessExceptionFactory;
import net.cavitos.workshop.sequence.domain.SequenceType;
import net.cavitos.workshop.sequence.model.repository.SequenceRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;

@Component
public class SequenceGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceGenerator.class);

    private static final String PAD_STR = "0";

    private final SequenceRepository sequenceRepository;
    private final String sequenceFormat;
    private final Clock systemClock;

    public SequenceGenerator(final SequenceRepository sequenceRepository,
                             @Value("${sequence.format:%s%s}") final String sequenceFormat,
                             final Clock systemClock) {

        this.sequenceRepository = sequenceRepository;
        this.sequenceFormat = sequenceFormat;
        this.systemClock = systemClock;
    }

    @Transactional
    public String nextValue(final SequenceType sequenceType, final String tenant) {

        return Failsafe.with(buildRetryPolicy())
                .get(() -> calculateNextValue(sequenceType, tenant));
    }

    @Transactional
    public String nextValue(final String prefix, final String tenant) {

        return Failsafe.with(buildRetryPolicy())
                .get(() -> calculateNextValue(prefix, tenant));
    }

    // ------------------------------------------------------------------------------------

    private String calculateNextValue(final SequenceType sequenceType, final String tenant) {

        return calculateNextValue(sequenceType.getPrefix(), tenant);
    }

    private String calculateNextValue(final String prefix, final String tenant) {

        try {
            final var entity = sequenceRepository.findByPrefixAndTenant(prefix, tenant)
                    .orElseThrow(() -> BusinessExceptionFactory.createBusinessException("Unable to Generate Sequence"));

            final var value = entity.getValue();

            final var padValue = StringUtils.leftPad(Long.toString(value), entity.getPadSize(), PAD_STR);
            final var sequenceValue = String.format(sequenceFormat, prefix, padValue);

            entity.setValue(value + entity.getStepSize());
            entity.setUpdated(systemClock.instant());

            sequenceRepository.save(entity);

            return sequenceValue;

        } catch (Exception exception) {

            throw BusinessExceptionFactory.createBusinessException("Unable to generate next sequence value for prefix: %s",
                    prefix, exception);
        }
    }

    private RetryPolicy<Object> buildRetryPolicy() {

        return RetryPolicy.builder()
                .handle(BusinessException.class)
                .withDelay(Duration.ofSeconds(1L))
                .onFailedAttempt(event -> LOGGER.error("Failed to calculate next sequence - ", event.getLastException()))
                .onRetry(event -> LOGGER.warn("Calculate sequence retry #: {}", event.getAttemptCount()))
                .onRetriesExceeded(event -> LOGGER.error("Maximum number of retries reached generating a sequence"))
                .withMaxRetries(3)
                .build();
    }
}
