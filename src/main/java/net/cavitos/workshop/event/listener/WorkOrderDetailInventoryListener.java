package net.cavitos.workshop.event.listener;

import net.cavitos.workshop.domain.model.web.InventoryMovement;
import net.cavitos.workshop.domain.model.web.common.CommonOperationType;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.factory.BusinessExceptionFactory;
import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.service.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import net.cavitos.workshop.event.model.WorkOrderDetailEvent;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.service.InventoryMovementService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
public class WorkOrderDetailInventoryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderDetailInventoryListener.class);

    private static final String LOCAL_SALE_OPERATION_TYPE = "sale.operation-type";

    private final InventoryMovementService inventoryMovementService;
    private final ConfigurationService configurationService;
    private final ZonedDateTimeFactory zonedDateTimeFactory;

    public WorkOrderDetailInventoryListener(final InventoryMovementService inventoryMovementService,
                                            final ConfigurationService configurationService,
                                            final ZonedDateTimeFactory zonedDateTimeFactory) {
        this.inventoryMovementService = inventoryMovementService;
        this.configurationService = configurationService;
        this.zonedDateTimeFactory = zonedDateTimeFactory;
    }

    @EventListener(WorkOrderDetailEvent.class)
    public void handleEvent(final WorkOrderDetailEvent workOrderDetailEvent) {

        final var workOrderDetailEntity = workOrderDetailEvent.getWorkOrderDetailEntity();
        final var workOrderEntity = workOrderDetailEntity.getWorkOrderEntity();

        LOGGER.info("Work Order Detail Event of type:{} - workOrder:{} for tenant:{}", 
            workOrderDetailEvent.getEventType(), workOrderEntity.getNumber(), workOrderDetailEntity.getTenant());

        final var eventType = workOrderDetailEvent.getEventType();

         switch (eventType) {
             case ADD -> addInventoryFor(workOrderDetailEntity);
             case UPDATE -> updateInventoryFor(workOrderDetailEntity);
             case DELETE -> deleteInventoryFor(workOrderDetailEntity);
         }
    }

    // --------------------------------------------------------------------------------------------------------

    @Transactional
    void deleteInventoryFor(final WorkOrderDetailEntity workOrderDetailEntity) {

        LOGGER.info("Deleting inventory movements for work order detail id={} for tenant={}",
                workOrderDetailEntity.getId(), workOrderDetailEntity.getTenant());

        inventoryMovementService.findByWorkOrderDetailId(workOrderDetailEntity.getId(), workOrderDetailEntity.getTenant())
                .ifPresent(inventoryEntity -> {

                    LOGGER.info("Deleting inventory movement_id={} for work_order_detail_id={} for tenant={}",
                            inventoryEntity.getId(), workOrderDetailEntity.getId(), workOrderDetailEntity.getTenant());

                    inventoryMovementService.delete(inventoryEntity.getId(), inventoryEntity.getTenant());
                });
    }

    @Transactional
    void addInventoryFor(final WorkOrderDetailEntity workOrderDetailEntity) {

        if (Objects.nonNull(workOrderDetailEntity.getProductEntity())) {

            final var now = zonedDateTimeFactory.getSystemNow();
            final var operationDate = zonedDateTimeFactory.buildStringFromInstant(now);

            final var properties = configurationService.getConfiguration(workOrderDetailEntity.getTenant());
            final var operationTypeCode = properties.getProperty(LOCAL_SALE_OPERATION_TYPE, "MI09");

            final var product = workOrderDetailEntity.getProductEntity();

            final var commonProduct = new CommonProduct();
            commonProduct.setCode(product.getCode());
            commonProduct.setName(product.getName());
            commonProduct.setStorable(product.isStorable());

            final var operationType = new CommonOperationType();
            operationType.setCode(operationTypeCode);

            final var movement = new InventoryMovement();
            movement.setOperationDate(operationDate);
            movement.setQuantity(workOrderDetailEntity.getQuantity());
            movement.setUnitPrice(workOrderDetailEntity.getUnitPrice());
            movement.setProduct(commonProduct);
            movement.setOperationType(operationType);
            movement.setWorkOrderDetailId(workOrderDetailEntity.getId());
            movement.setDescription("Inventory automatic movement triggered by work order detail event");

            inventoryMovementService.add(workOrderDetailEntity.getTenant(), movement);

            LOGGER.info("Inventory updated for work order detail id={} with product code={} for tenant={}",
                    workOrderDetailEntity.getId(), product.getCode(), workOrderDetailEntity.getTenant());

            return;
        }

        LOGGER.info("No inventory update for work order detail id={} because it has no product associated",
                workOrderDetailEntity.getId());
    }

    @Transactional
    void updateInventoryFor(final WorkOrderDetailEntity workOrderDetailEntity) {

        LOGGER.info("Updating inventory movements for work order detail id={} for tenant={}",
                workOrderDetailEntity.getId(), workOrderDetailEntity.getTenant());

        deleteInventoryFor(workOrderDetailEntity);
        addInventoryFor(workOrderDetailEntity);
    }
}
