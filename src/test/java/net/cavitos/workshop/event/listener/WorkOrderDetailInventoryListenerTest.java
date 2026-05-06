package net.cavitos.workshop.event.listener;

import net.cavitos.workshop.event.model.EventType;
import net.cavitos.workshop.event.model.WorkOrderDetailEvent;
import net.cavitos.workshop.event.processor.WorkOrderDetailInventoryProcessor;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.model.entity.WorkOrderEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class WorkOrderDetailInventoryListenerTest {

    @Mock
    private WorkOrderDetailInventoryProcessor workOrderDetailInventoryProcessor;

    @InjectMocks
    private WorkOrderDetailInventoryListener listener;

    @Test
    void handleEventWhenAddThenDelegatesToAddInventoryFor() {

        WorkOrderDetailEntity detailEntity = buildWorkOrderDetailEntity();
        WorkOrderDetailEvent event = buildEvent(EventType.ADD, detailEntity);

        assertThatCode(() -> listener.handleEvent(event)).doesNotThrowAnyException();

        verify(workOrderDetailInventoryProcessor).addInventoryFor(detailEntity);
        verify(workOrderDetailInventoryProcessor, never()).deleteInventoryFor(any(WorkOrderDetailEntity.class));
    }

    @Test
    void handleEventWhenDeleteThenDelegatesToDeleteInventoryFor() {

        WorkOrderDetailEntity detailEntity = buildWorkOrderDetailEntity();
        WorkOrderDetailEvent event = buildEvent(EventType.DELETE, detailEntity);

        assertThatCode(() -> listener.handleEvent(event)).doesNotThrowAnyException();

        verify(workOrderDetailInventoryProcessor).deleteInventoryFor(detailEntity);
        verify(workOrderDetailInventoryProcessor, never()).addInventoryFor(any(WorkOrderDetailEntity.class));
    }

    @Test
    void handleEventWhenUpdateThenDeletesAndAddsInventoryInOrder() {

        WorkOrderDetailEntity detailEntity = buildWorkOrderDetailEntity();
        WorkOrderDetailEvent event = buildEvent(EventType.UPDATE, detailEntity);

        assertThatCode(() -> listener.handleEvent(event)).doesNotThrowAnyException();

        InOrder inOrder = inOrder(workOrderDetailInventoryProcessor);
        inOrder.verify(workOrderDetailInventoryProcessor).deleteInventoryFor(detailEntity);
        inOrder.verify(workOrderDetailInventoryProcessor).addInventoryFor(detailEntity);
    }

    @Test
    void handleEventWhenUnknownThenNoProcessorInteraction() {

        final var detailEntity = buildWorkOrderDetailEntity();
        final var event = buildEvent(EventType.UNKNOWN, detailEntity);

        assertThatCode(() -> listener.handleEvent(event)).doesNotThrowAnyException();

        verifyNoInteractions(workOrderDetailInventoryProcessor);
    }

    private WorkOrderDetailEvent buildEvent(final EventType eventType, final WorkOrderDetailEntity detailEntity) {

        return WorkOrderDetailEvent.builder()
                .eventType(eventType)
                .workOrderDetailEntity(detailEntity)
                .build();
    }

    private WorkOrderDetailEntity buildWorkOrderDetailEntity() {

        WorkOrderEntity workOrderEntity = WorkOrderEntity.builder()
                .id("work-order-1")
                .number("WO-1")
                .tenant("tenant-a")
                .build();

        return WorkOrderDetailEntity.builder()
                .id("detail-1")
                .tenant("tenant-a")
                .workOrderEntity(workOrderEntity)
                .build();
    }
}

