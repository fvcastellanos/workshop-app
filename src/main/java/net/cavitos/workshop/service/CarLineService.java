package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.model.web.CarLine;
import net.cavitos.workshop.model.entity.CarBrandEntity;
import net.cavitos.workshop.model.entity.CarLineEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.CarBrandRepository;
import net.cavitos.workshop.model.repository.CarLineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static net.cavitos.workshop.domain.model.status.ActiveStatus.ACTIVE;
import static net.cavitos.workshop.factory.BusinessExceptionFactory.createBusinessException;

@Service
public class CarLineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarLineService.class);

    private final CarLineRepository carLineRepository;
    private final CarBrandRepository carBrandRepository;

    public CarLineService(final CarLineRepository carLineRepository,
                          final CarBrandRepository carBrandRepository) {

        this.carLineRepository = carLineRepository;
        this.carBrandRepository = carBrandRepository;
    }

    public Page<CarLineEntity> findAll(final String tenant,
                                       final String carBrandId,
                                       final int active,
                                       final String name,
                                       final int page,
                                       final int size) {

        LOGGER.info("Retrieve all car lines associated with tenant={}", tenant);

        final var pageable = PageRequest.of(page, size);

        final var carBrandEntity = carBrandRepository.findById(carBrandId)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Car Brand Id: %s, not found", carBrandId));

        return carLineRepository.findByCarBrandAndTenantAndActiveAndNameContainsIgnoreCase(carBrandEntity, tenant, active, name, pageable);
    }

    public Page<CarLineEntity> search(final String tenant,
                                      final String text,
                                      final int active,
                                      final int page,
                                      final int size) {

        LOGGER.info("Retrieve all car lines associated with tenant={}, active = {} and text={}", tenant, active, text);

        final var pageable = PageRequest.of(page, size);
        return carLineRepository.search(tenant, "%" + text + "%", active, pageable);
    }

    public CarLineEntity findById(final String tenant,
                                  final String carBrandId,
                                  final String id) {

        LOGGER.info("Trying to retrieve car_line_id={} for tenant={}", id, tenant);

        final var carBrandEntity = findCarBrandEntity(carBrandId);

        final var carLineEntity = carLineRepository.findById(id)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Car Line not found for brand: %s", carBrandEntity.getName()));

        if (!carLineEntity.getTenant().equalsIgnoreCase(tenant)) {

            LOGGER.error("car_line_id={} is not associated to tenant={}", id, tenant);
            throw  createBusinessException(HttpStatus.NOT_FOUND, "Car Line not found for brand: %s", carBrandEntity.getName());
        }

        return carLineEntity;
    }

    public CarLineEntity add(final String tenant, final String carBrandId, final CarLine carLine) {

        LOGGER.info("Adding a new car line with name={} for tenant={}", carLine.getName(), tenant);

        final var carLineName = carLine.getName()
                .toUpperCase();

        final var carBrandEntity = findCarBrandEntity(carBrandId);

        final var existingCarLineHolder = carLineRepository.findByCarBrandAndNameAndTenant(carBrandEntity,
                carLineName, tenant);

        if (existingCarLineHolder.isPresent()) {
            LOGGER.warn("Car line with name={} already exists for brand={} - tenant={}", carLineName,
                    carBrandEntity.getName(), tenant);

            return existingCarLineHolder.get();
        }

        final var entity = CarLineEntity.builder()
                .id(TimeBasedGenerator.generateTimeBasedId())
                .carBrand(carBrandEntity)
                .name(carLineName)
                .description(carLine.getDescription())
                .active(ACTIVE.value())
                .tenant(tenant)
                .created(Instant.now())
                .build();

        carLineRepository.save(entity);

        return entity;
    }

    public CarLineEntity update(final String tenant,
                                final String carBrandId,
                                final String carLineId,
                                final CarLine carLine) {

        LOGGER.info("Trying to update car_line_id={} for tenant={}", carLineId, tenant);

        final var carLineEntity = carLineRepository.findById(carLineId)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Car line not found"));

        if (!carLineEntity.getTenant().equalsIgnoreCase(tenant)) {

            LOGGER.error("car_line_id={} is not associated to tenant={}", carLineId, tenant);
            throw createBusinessException(HttpStatus.NOT_FOUND, "Car line not found");
        }

        findCarBrandEntity(carBrandId);

        carLineEntity.setActive(carLine.getActive());
        carLineEntity.setName(carLine.getName());
        carLineEntity.setDescription(carLine.getDescription());

        carLineRepository.save(carLineEntity);

        return carLineEntity;
    }

    // ------------------------------------------------------------------------------------------------------

    private CarBrandEntity findCarBrandEntity(final String carBrandId) {

        return carBrandRepository.findById(carBrandId)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Car Brand Id: %s, not found", carBrandId));
    }
}
