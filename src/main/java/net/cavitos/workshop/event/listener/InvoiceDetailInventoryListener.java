package net.cavitos.workshop.event.listener;

import net.cavitos.workshop.event.model.InvoiceDetailEvent;
import net.cavitos.workshop.factory.BusinessExceptionFactory;
import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.InventoryEntity;
import net.cavitos.workshop.model.entity.InventoryMovementTypeEntity;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.InventoryMovementTypeRepository;
import net.cavitos.workshop.model.repository.InventoryRepository;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.nonNull;

@Component
public class InvoiceDetailInventoryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceDetailInventoryListener.class);

    private static final String MOVEMENT_DESCRIPTION = "Inventory automatic movement triggered by invoice detail event";

    private final InventoryRepository inventoryRepository;

    private final InventoryMovementTypeRepository inventoryMovementTypeRepository;

    private final ZonedDateTimeFactory zonedDateTimeFactory;

    private final String inventoryMovementInputTypeCode;
    private final String inventoryMovementOutputTypeCode;

    public InvoiceDetailInventoryListener(@Value("${invoice.detail.inventory.input-type-code}") final String inventoryMovementInputTypeCode,
                                          @Value("${invoice.detail.inventory.output-type-code}") final String inventoryMovementOutputTypeCode,
                                          final InventoryRepository inventoryRepository,
                                          final InventoryMovementTypeRepository inventoryMovementTypeRepository,
                                          final ZonedDateTimeFactory zonedDateTimeFactory) {

        this.inventoryRepository = inventoryRepository;
        this.inventoryMovementTypeRepository = inventoryMovementTypeRepository;
        this.inventoryMovementInputTypeCode = inventoryMovementInputTypeCode;
        this.inventoryMovementOutputTypeCode = inventoryMovementOutputTypeCode;
        this.zonedDateTimeFactory = zonedDateTimeFactory;
    }

    @EventListener(InvoiceDetailEvent.class)
    public void handleEvent(InvoiceDetailEvent invoiceDetailEvent) {

        LOGGER.info("Invoice Detail Event of type={} with invoice_detail_id={}",
                invoiceDetailEvent.getEventType(), invoiceDetailEvent.getInvoiceDetailEntity().getId());

        final var eventType = invoiceDetailEvent.getEventType();
        final var invoiceDetailEntity = invoiceDetailEvent.getInvoiceDetailEntity();

        switch (eventType) {
            case ADD -> addInventoryMovement(invoiceDetailEntity);
            case UPDATE -> updateInventoryMovement(invoiceDetailEntity);
            case DELETE -> deleteInventoryMovement(invoiceDetailEntity);
        }
    }

    // --------------------------------------------------------------------------------------------------

    @Transactional
    void addInventoryMovement(final InvoiceDetailEntity invoiceDetailEntity) {

        final var tenant = invoiceDetailEntity.getTenant();
        final var productEntity = invoiceDetailEntity.getProductEntity();

        LOGGER.info("Adding an inventory movement for tenant={} and invoice_detail_id={}", tenant, invoiceDetailEntity.getId());

        if (productEntity.isStorable()) {

            final var inventoryMovements = inventoryRepository.findInventoryMovementsFor(productEntity.getId(),
                    invoiceDetailEntity.getId(), tenant);

            if (!ListUtils.emptyIfNull(inventoryMovements).isEmpty()) {

                LOGGER.error("Movements already registered for invoice_detail_id={} and tenant={}",
                        invoiceDetailEntity.getId(), tenant);

                return;
            }

            // Recalculate total and unit price to reflect discount into unit price of product in inventory movement
            final var total = (invoiceDetailEntity.getQuantity() * invoiceDetailEntity.getUnitPrice())
                    - invoiceDetailEntity.getDiscountAmount();

            final var unitPrice = total / invoiceDetailEntity.getQuantity();

            final var operationDate = invoiceDetailEntity.getInvoiceEntity()
                    .getInvoiceDate();

            final var movementInputTypeEntity = findBuyLocalMovementType(tenant);

            final var movement = InventoryEntity.builder()
                    .id(TimeBasedGenerator.generateTimeBasedId())
                    .invoiceDetailEntity(invoiceDetailEntity)
                    .productEntity(productEntity)
                    .quantity(invoiceDetailEntity.getQuantity())
                    .unitPrice(unitPrice)
                    .total(total)
                    .inventoryMovementTypeEntity(movementInputTypeEntity)
                    .operationDate(operationDate)
                    .tenant(tenant)
                    .description(MOVEMENT_DESCRIPTION)
                    .created(zonedDateTimeFactory.getSystemNow())
                    .updated(zonedDateTimeFactory.getSystemNow())
                    .build();

            inventoryRepository.save(movement);

            if (nonNull(invoiceDetailEntity.getWorkOrderEntity())) {

                LOGGER.info("Add a output inventory movement for work_order_number={} and tenant={}",
                        invoiceDetailEntity.getWorkOrderEntity().getNumber(), tenant);

                final var movementOutputTypeEntity = findLocalSellMovementType(tenant);

                final var outputMovement = InventoryEntity.builder()
                        .id(TimeBasedGenerator.generateTimeBasedId())
                        .invoiceDetailEntity(invoiceDetailEntity)
                        .productEntity(productEntity)
                        .quantity(invoiceDetailEntity.getQuantity())
                        .unitPrice(unitPrice)
                        .total(total)
                        .inventoryMovementTypeEntity(movementOutputTypeEntity)
                        .operationDate(zonedDateTimeFactory.getSystemNow())
                        .tenant(tenant)
                        .description(MOVEMENT_DESCRIPTION)
                        .created(zonedDateTimeFactory.getSystemNow())
                        .updated(zonedDateTimeFactory.getSystemNow())
                        .build();

                inventoryRepository.save(outputMovement);
            }

            return;
        }

        LOGGER.info("product_id={} of invoice_detail_id={} and tenant={} is not candidate for inventory", productEntity.getId(),
                invoiceDetailEntity.getId(), tenant);
    }

    @Transactional
    void deleteInventoryMovement(final InvoiceDetailEntity invoiceDetailEntity) {

        final var tenant = invoiceDetailEntity.getTenant();
        final var productEntity = invoiceDetailEntity.getProductEntity();

        LOGGER.info("Deleting inventory movement for tenant={} and invoice_detail_id={}", tenant, invoiceDetailEntity.getId());

        if (productEntity.isStorable()) {

            final var inventoryMovements = inventoryRepository.findInventoryMovementsFor(productEntity.getId(),
                    invoiceDetailEntity.getId(), tenant);

            if (!ListUtils.emptyIfNull(inventoryMovements).isEmpty()) {

                LOGGER.info("Deleting inventory movements for productId={} and invoiceDetailId={} for tenant={}",
                        productEntity.getId(), invoiceDetailEntity.getId(), tenant);

                inventoryRepository.deleteAll(inventoryMovements);
            }

            return;
        }

        LOGGER.info("product_id={} of invoice_detail_id={} and tenant={} is not candidate for inventory", productEntity.getId(),
                invoiceDetailEntity.getId(), tenant);
    }

    @Transactional
    void updateInventoryMovement(final InvoiceDetailEntity invoiceDetailEntity) {

        deleteInventoryMovement(invoiceDetailEntity);
        addInventoryMovement(invoiceDetailEntity);
    }

    // --------------------------------------------------------------------------------------------------

    private InventoryMovementTypeEntity findBuyLocalMovementType(final String tenant) {

        return inventoryMovementTypeRepository.findByCodeAndTenant(inventoryMovementInputTypeCode, tenant)
                .orElseThrow(() -> BusinessExceptionFactory.createBusinessException("Buy Local Inventory Movement Type not found"));
    }

    private InventoryMovementTypeEntity findLocalSellMovementType(final String tenant) {

        return inventoryMovementTypeRepository.findByCodeAndTenant(inventoryMovementOutputTypeCode, tenant)
                .orElseThrow(() -> BusinessExceptionFactory.createBusinessException("Output Inventory Movement Type not found"));
    }

}
