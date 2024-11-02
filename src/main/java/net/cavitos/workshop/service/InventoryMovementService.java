package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.model.web.InventoryMovement;
import net.cavitos.workshop.model.entity.InventoryEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.InventoryMovementTypeRepository;
import net.cavitos.workshop.model.repository.InventoryRepository;
import net.cavitos.workshop.model.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static net.cavitos.workshop.factory.BusinessExceptionFactory.createBusinessException;

@Service
public class InventoryMovementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryMovementService.class);

    private final InventoryRepository inventoryRepository;

    private final ProductRepository productRepository;

    private final InventoryMovementTypeRepository inventoryMovementTypeRepository;

    public InventoryMovementService(final InventoryRepository inventoryRepository,
                                    final ProductRepository productRepository,
                                    final InventoryMovementTypeRepository inventoryMovementTypeRepository) {

        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.inventoryMovementTypeRepository = inventoryMovementTypeRepository;
    }

    public Page<InventoryEntity> search(final String operationType,
                                        final String operationTypeCode,
                                        final Instant initialDate,
                                        final Instant finalDate,
                                        final String tenant,
                                        final int page,
                                        final int size) {

        LOGGER.info("Search inventory movements from: {} to: {} for operationType: {} and tenant: {}",
                initialDate, finalDate, operationType, tenant);

        final var pageable = PageRequest.of(page, size);

        return inventoryRepository.search(operationType, operationTypeCode, initialDate, finalDate, tenant, pageable);
    }

    public InventoryEntity findById(final String id, final String tenant) {

        return inventoryRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Inventory movement not found"));
    }

    @Transactional
    public InventoryEntity add(final String tenant, final InventoryMovement movement) {

        LOGGER.info("Add inventory movement for tenant: {}", tenant);

        final var product = movement.getProduct();

        final var productEntity = productRepository.findByCodeEqualsIgnoreCaseAndTenant(product.getCode(), tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Product not found"));

        final var operationTypeCode = movement.getOperationType()
                .getCode();

        final var operationType = inventoryMovementTypeRepository.findByCodeAndTenant(operationTypeCode, tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Inventory Movement Type not found"));

        final var inventoryMovementHolder =
        inventoryRepository.findByProductEntityAndInvoiceDetailEntityAndInventoryMovementTypeEntityAndTenant(productEntity,
                null, operationType, tenant);

        if (inventoryMovementHolder.isPresent()) {
            throw createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Inventory Movement already exists");
        }

        final var total = (movement.getUnitPrice() * movement.getQuantity()) - movement.getDiscountAmount();

        final var operationDate = LocalDate.parse(movement.getOperationDate())
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);

        final var entity = InventoryEntity.builder()
                .id(TimeBasedGenerator.generateTimeBasedId())
                .tenant(tenant)
                .productEntity(productEntity)
                .description(movement.getDescription())
                .quantity(movement.getQuantity())
                .unitPrice(movement.getUnitPrice())
                .discountAmount(movement.getDiscountAmount())
                .operationDate(operationDate)
                .inventoryMovementTypeEntity(operationType)
                .total(total)
                .created(Instant.now())
                .updated(Instant.now())
                .build();

        return inventoryRepository.save(entity);
    }

    public InventoryEntity update(final String tenant, final String id, final InventoryMovement movement) {

        LOGGER.info("Update inventory movement for tenant: {}", tenant);

        final var entity = inventoryRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Inventory Movement not found"));

        final var product = movement.getProduct();

        final var productEntity = productRepository.findByCodeEqualsIgnoreCaseAndTenant(product.getCode(), tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Product not found"));

        final var operationTypeCode = movement.getOperationType()
                .getCode();

        final var operationType = inventoryMovementTypeRepository.findByCodeAndTenant(operationTypeCode, tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Inventory Movement Type not found"));

        entity.setUpdated(Instant.now());
        entity.setDescription(movement.getDescription());
        entity.setQuantity(movement.getQuantity());
        entity.setDiscountAmount(movement.getDiscountAmount());
        entity.setUnitPrice(movement.getUnitPrice());
        entity.setProductEntity(productEntity);
        entity.setInventoryMovementTypeEntity(operationType);
//        entity.setInvoiceDetailEntity(null);

        return inventoryRepository.save(entity);
    }

    @Transactional
    public void delete(final String id, final String tenant) {

        LOGGER.info("Delete inventory movement with id: {} for tenant: {}", id, tenant);

        final var movementHolder = inventoryRepository.findByIdAndTenant(id, tenant);

        if (movementHolder.isEmpty()) {

            LOGGER.info("Inventory movement with id: {} not found for tenant: {}", id, tenant);
            return;
        }

        inventoryRepository.delete(movementHolder.get());
    }
}
