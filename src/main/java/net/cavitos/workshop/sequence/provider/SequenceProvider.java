package net.cavitos.workshop.sequence.provider;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import net.cavitos.workshop.domain.exception.BusinessException;
import net.cavitos.workshop.factory.BusinessExceptionFactory;
import net.cavitos.workshop.factory.DateTimeFactory;
import net.cavitos.workshop.sequence.domain.SequenceType;
import net.cavitos.workshop.sequence.model.repository.SequenceRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Component
public class SequenceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceProvider.class);

    private static final String PAD_STR = "0";
    private static final int PAD_SIZE = 5;

    private final SequenceRepository sequenceRepository;

    public SequenceProvider(final SequenceRepository sequenceRepository) {

        this.sequenceRepository = sequenceRepository;
    }

    @Transactional
    public String calculateNext(final SequenceType sequenceType) {

        return Failsafe.with(buildRetryPolicy())
                .get(() -> calculateNextValue(sequenceType, PAD_SIZE));
    }

    @Transactional
    public String calculateNext(final SequenceType sequenceType, final int padSize) {

        return Failsafe.with(buildRetryPolicy())
                .get(() -> calculateNextValue(sequenceType, padSize));
    }

    // ------------------------------------------------------------------------------------

    private String calculateNextValue(final SequenceType sequenceType, final int padSize) {

        try {
            final var entity = sequenceRepository.findByPrefix(sequenceType.getPrefix())
                                    .orElseThrow(() -> BusinessExceptionFactory.createBusinessException("Unable to Generate Sequence"));

            final var value = entity.getValue();
            final var numericValue = Long.parseLong(value);

            final var padValue = StringUtils.leftPad(value, padSize, PAD_STR);
            final var sequenceValue = String.format("%s-%s", sequenceType.getPrefix(), padValue);

            entity.setValue(Long.toString(numericValue + 1));
            entity.setUpdated(DateTimeFactory.getUTCNow());

            sequenceRepository.save(entity);

            return sequenceValue;

        } catch (Exception exception) {

            throw BusinessExceptionFactory.createBusinessException("Unable to generate next sequence value for prefix: %s",
                    sequenceType.getPrefix());
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
