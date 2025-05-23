package net.cavitos.workshop.sequence.service;

import net.cavitos.workshop.domain.model.web.Sequence;
import net.cavitos.workshop.factory.BusinessExceptionFactory;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.sequence.model.entity.SequenceEntity;
import net.cavitos.workshop.sequence.model.repository.SequenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

@Service
public class SequenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceService.class);

    private final SequenceRepository sequenceRepository;
    private final Clock systemClock;

    public SequenceService(final SequenceRepository sequenceRepository,
                           final Clock systemClock) {

        this.sequenceRepository = sequenceRepository;
        this.systemClock = systemClock;
    }

    public Page<SequenceEntity> search(final String text,
                                       final String tenant,
                                       final int page,
                                       final int size) {

        LOGGER.info("Searching sequences for tenant={} with text={}", tenant, text);

        final var pageable = PageRequest.of(page, size);

        return sequenceRepository.search("%" + text + "%", tenant, pageable);
    }

    public Optional<SequenceEntity> getById(final String id,
                                            final String tenant) {

        LOGGER.info("Retrieve sequence_id={} for tenant={}", id, tenant);

        return sequenceRepository.findByIdAndTenant(id, tenant);
    }

    public SequenceEntity add(final Sequence sequence, final String tenant) {

        LOGGER.info("Adding sequence for tenant={} with prefix={}", tenant, sequence.getPrefix());

        final var prefix = sequence.getPrefix()
                .toUpperCase();

        sequenceRepository.findByPrefixAndTenant(sequence.getPrefix(), tenant)
                .ifPresent(entity -> {
                    throw BusinessExceptionFactory.createBusinessException("Sequence with prefix %s already exists for tenant %s",
                            sequence.getPrefix(), tenant);
                });

        final var entity = SequenceEntity.builder()
                .id(TimeBasedGenerator.generateTimeBasedId())
                .prefix(prefix)
                .padSize(sequence.getPadSize())
                .stepSize(sequence.getStepSize())
                .description(sequence.getDescription())
                .tenant(tenant)
                .value(1L)
                .created(systemClock.instant())
                .build();

        return sequenceRepository.save(entity);
    }

    public SequenceEntity update(final String id, final Sequence sequence, final String tenant) {

        LOGGER.info("Updating sequence for tenant={} with prefix={}", tenant, sequence.getPrefix());

        final var prefix = sequence.getPrefix()
                .toUpperCase();

        sequenceRepository.findByPrefixAndTenant(sequence.getPrefix(), tenant)
            .ifPresent(entity -> {
                if (!entity.getId().equals(id)) {
                    throw BusinessExceptionFactory.createBusinessException("Sequence with prefix %s already exists for tenant %s",
                            sequence.getPrefix(), tenant);
                }
            });

        final var entity = sequenceRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> BusinessExceptionFactory.createBusinessException("Sequence not found"));

        entity.setPrefix(prefix);
        entity.setPadSize(sequence.getPadSize());
        entity.setStepSize(sequence.getStepSize());
        entity.setDescription(sequence.getDescription());
        entity.setUpdated(systemClock.instant());

        return sequenceRepository.save(entity);
    }

    public List<SequenceEntity> getSequences(final String tenant) {

        LOGGER.info("Retrieve all sequences for tenant={}", tenant);

        return sequenceRepository.findByTenant(tenant);
    }
}
