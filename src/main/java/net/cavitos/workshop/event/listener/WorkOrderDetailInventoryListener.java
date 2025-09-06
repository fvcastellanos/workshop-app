package net.cavitos.workshop.event.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import net.cavitos.workshop.event.model.WorkOrderDetailEvent;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.service.InventoryMovementService;

@Component
public class WorkOrderDetailInventoryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderDetailInventoryListener.class);

    private final InventoryMovementService inventoryMovementService;

    public WorkOrderDetailInventoryListener(final InventoryMovementService inventoryMovementService) {
        this.inventoryMovementService = inventoryMovementService;
    }

    @EventListener(WorkOrderDetailEvent.class)
    public void handleEvent(final WorkOrderDetailEvent workOrderDetailEvent) {

        final var workOrderDetailEntity = workOrderDetailEvent.getWorkOrderDetailEntity();
        final var workOrderEntity = workOrderDetailEntity.getWorkOrderEntity();

        LOGGER.info("Work Order Detail Event of type:{} - workOrder:{} for tenant:{}", 
            workOrderDetailEvent.getEventType(), workOrderEntity.getNumber(), workOrderDetailEntity.getTenant());

        // LOGGER.info("Work Order Detail Event of type={} with work_order_detail_id={}",
        //         workOrderDetailEvent.getEventType(), workOrderDetailEvent.getWorkOrderDetailEntity().getId());

        // final var eventType = workOrderDetailEvent.getEventType();
        // final var workOrderDetailEntity = workOrderDetailEvent.getWorkOrderDetailEntity();

        // switch (eventType) {
        //     case ADD -> addInventoryFor(workOrderDetailEntity);
        //     case UPDATE -> updateInventoryFor(workOrderDetailEntity);
        //     case DELETE -> deleteInventoryFor(workOrderDetailEntity);
        // }
    }

    // --------------------------------------------------------------------------------------------------------

    void deleteInventoryFor(final WorkOrderDetailEntity workOrderDetailEntity) {

        // inventoryService.removeInventory(workOrderDetailEntity.getProductEntity().getId(), workOrderDetailEntity.getQuantity());
    }

    void addInventoryFor(final WorkOrderDetailEntity workOrderDetailEntity) {

        // inventoryService.addInventory(workOrderDetailEntity.getProductEntity().getId(), workOrderDetailEntity.getQuantity());
    }

    void updateInventoryFor(final WorkOrderDetailEntity workOrderDetailEntity) {

        // inventoryService.updateInventory(workOrderDetailEntity.getProductEntity().getId(), workOrderDetailEntity.getQuantity());
    }
}
