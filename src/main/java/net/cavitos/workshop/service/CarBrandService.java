package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.exception.BusinessException;
import net.cavitos.workshop.domain.model.status.ActiveStatus;
import net.cavitos.workshop.domain.model.web.CarBrand;
import net.cavitos.workshop.model.entity.CarBrandEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.CarBrandRepository;
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
public class CarBrandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarBrandService.class);

    private final CarBrandRepository carBrandRepository;

    public CarBrandService(final CarBrandRepository carBrandRepository) {

        this.carBrandRepository = carBrandRepository;
    }

    public Page<CarBrandEntity> getAllByTenant(final String tenant,
                                         final int active,
                                         final String name,
                                         final int page,
                                         final int size) {

        LOGGER.info("get car brands configured for tenant={}", tenant);
        final var pageable = PageRequest.of(page, size);

        return carBrandRepository.findByTenantAndActiveAndNameContainsIgnoreCase(tenant, active, name, pageable);
    }

    public CarBrandEntity getById(final String tenant, final String id) {

        LOGGER.info("Retrieve car_brand_id={} for tenant={}", id, tenant);

        final var carBrandEntity = carBrandRepository.findById(id)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "car_brand_id=%s not found for tenant=%s",
                        id, tenant)
                );

        if (!carBrandEntity.getTenant().equalsIgnoreCase(tenant)) {

            LOGGER.error("car_brand_id={} is not assigned to tenant={}", id, tenant);
            throw createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Car Brand");
        }

        return carBrandEntity;
    }

    public CarBrandEntity add(final String tenant, final CarBrand carBrand) {

        LOGGER.info("add new car brand with name={} for tenant={}", carBrand.getName(), tenant);

        final var carBrandName = carBrand.getName()
                .toUpperCase();

        final var carBrandHolder = carBrandRepository.findByNameAndTenant(carBrandName, tenant);

        if (carBrandHolder.isPresent()) {

            LOGGER.warn("Car brand name={} already exists for tenant={}", carBrandName, tenant);
            return carBrandHolder.get();
        }

        var entity = CarBrandEntity.builder()
                .id(TimeBasedGenerator.generateTimeBasedId())
                .name(carBrand.getName().toUpperCase())
                .description(carBrand.getDescription())
                .tenant(tenant)
                .created(Instant.now())
                .active(ACTIVE.value())
                .build();

        return carBrandRepository.save(entity);
    }

    public CarBrandEntity update(final String tenant, final String id, final CarBrand carBrand) {

        LOGGER.info("update car brand with id={}", id);

        var carBrandEntityHolder = carBrandRepository.findById(id);

        var carBrandEntity = carBrandEntityHolder.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,
                "Car Brand Not Found"));

        if (!carBrandEntity.getTenant().equalsIgnoreCase(tenant)) {

            LOGGER.error("car_brand_id={} is not assigned to tenant={}", id, tenant);
            throw new BusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Car Brand not found");
        }

        final var active = ActiveStatus.valueOf(carBrand.getActive())
                        .value();

        carBrandEntity.setName(carBrand.getName().toUpperCase());
        carBrandEntity.setDescription(carBrand.getDescription());
        carBrandEntity.setActive(active);
        carBrandRepository.save(carBrandEntity);

        return carBrandEntity;
    }
}
