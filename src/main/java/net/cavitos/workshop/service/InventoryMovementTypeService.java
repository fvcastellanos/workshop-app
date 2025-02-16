package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.model.web.InventoryMovementType;
import net.cavitos.workshop.factory.BusinessExceptionFactory;
import net.cavitos.workshop.model.entity.InventoryMovementTypeEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.InventoryMovementTypeRepository;
import net.cavitos.workshop.sequence.domain.SequenceType;
import net.cavitos.workshop.sequence.provider.SequenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static net.cavitos.workshop.factory.BusinessExceptionFactory.createBusinessException;

@Service
public class InventoryMovementTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryMovementTypeService.class);

    private final InventoryMovementTypeRepository inventoryMovementTypeRepository;
    private final SequenceProvider sequenceProvider;

    public InventoryMovementTypeService(final InventoryMovementTypeRepository inventoryMovementTypeRepository,
                                        final SequenceProvider sequenceProvider) {

        this.inventoryMovementTypeRepository = inventoryMovementTypeRepository;
        this.sequenceProvider = sequenceProvider;
    }

    public Page<InventoryMovementTypeEntity> search(final int active,
                                                    final String type,
                                                    final String text,
                                                    final String tenant,
                                                    final int page,
                                                    final int size) {

        LOGGER.info("Search for inventory movement types with text: {} for tenant: {}", text, tenant);

        final var pageable = PageRequest.of(page, size);

        return inventoryMovementTypeRepository.search(active, type, "%" + text + "%", tenant, pageable);
    }

    public InventoryMovementTypeEntity getById(final String id,
                                               final String tenant) {

        return inventoryMovementTypeRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Inventory movement type not found"));
    }

    public InventoryMovementTypeEntity add(final InventoryMovementType inventoryMovementType, final String tenant) {

        LOGGER.info("Add inventory movement type with name: {} for tenant: {}", inventoryMovementType.getName(),
                tenant);

        inventoryMovementTypeRepository.findByNameAndTenant(inventoryMovementType.getName(), tenant)
                .ifPresent(movement -> {

                    throw BusinessExceptionFactory.createBusinessException("Movement Type already exists");
                });

        final var code = sequenceProvider.calculateNext(SequenceType.INVENTORY_MOVEMENT, 2, tenant);

        final var entity = InventoryMovementTypeEntity.builder()
                .id(TimeBasedGenerator.generateTimeBasedId())
                .code(code)
                .active(inventoryMovementType.getActive())
                .type(inventoryMovementType.getType())
                .name(inventoryMovementType.getName())
                .description(inventoryMovementType.getDescription())
                .tenant(tenant)
                .created(Instant.now())
                .build();

        return inventoryMovementTypeRepository.save(entity);
    }

    public InventoryMovementTypeEntity update(final String id,
                                              final InventoryMovementType inventoryMovementType,
                                              final String tenant) {

        LOGGER.info("Update inventory movement type with name: {} for tenant: {}", inventoryMovementType.getName(),
                tenant);

        final var entity = inventoryMovementTypeRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> BusinessExceptionFactory.createBusinessException(HttpStatus.NOT_FOUND,
                        "Inventory Movement Type not found"));

        if (!inventoryMovementType.getName().equalsIgnoreCase(entity.getName())) {

            inventoryMovementTypeRepository.findByNameAndTenant(inventoryMovementType.getName(), tenant)
                    .ifPresent(movement -> {

                        if (movement.getId().equalsIgnoreCase(entity.getId())) {

                            throw BusinessExceptionFactory.createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY,
                                    "Movement Type already exists");
                        }
                    });
        }

        entity.setType(inventoryMovementType.getType());
        entity.setName(inventoryMovementType.getName());
        entity.setType(inventoryMovementType.getType());
        entity.setDescription(inventoryMovementType.getDescription());
        entity.setActive(inventoryMovementType.getActive());

        return inventoryMovementTypeRepository.save(entity);
    }
}
