package net.cavitos.workshop.event.listener;

import net.cavitos.workshop.event.model.WorkOrderDetailEvent;
import net.cavitos.workshop.event.processor.WorkOrderDetailInventoryProcessor;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WorkOrderDetailInventoryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderDetailInventoryListener.class);

    private final WorkOrderDetailInventoryProcessor workOrderDetailInventoryProcessor;

    public WorkOrderDetailInventoryListener(final WorkOrderDetailInventoryProcessor workOrderDetailInventoryProcessor) {

        this.workOrderDetailInventoryProcessor = workOrderDetailInventoryProcessor;
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

        workOrderDetailInventoryProcessor.deleteInventoryFor(workOrderDetailEntity);
    }

    @Transactional
    void addInventoryFor(final WorkOrderDetailEntity workOrderDetailEntity) {

        workOrderDetailInventoryProcessor.addInventoryFor(workOrderDetailEntity);
    }

    @Transactional
    void updateInventoryFor(final WorkOrderDetailEntity workOrderDetailEntity) {

        LOGGER.info("Updating inventory movements for work order detail id={} for tenant={}",
                workOrderDetailEntity.getId(), workOrderDetailEntity.getTenant());

        workOrderDetailInventoryProcessor.deleteInventoryFor(workOrderDetailEntity);
        workOrderDetailInventoryProcessor.addInventoryFor(workOrderDetailEntity);
    }
}
