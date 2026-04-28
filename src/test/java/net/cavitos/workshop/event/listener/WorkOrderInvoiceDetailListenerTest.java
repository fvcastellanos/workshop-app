package net.cavitos.workshop.event.listener;

import net.cavitos.workshop.event.model.EventType;
import net.cavitos.workshop.event.model.InvoiceDetailEvent;
import net.cavitos.workshop.event.processor.WorkOrderInvoiceDetailProcessor;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class WorkOrderInvoiceDetailListenerTest {

    @Mock
    private WorkOrderInvoiceDetailProcessor workOrderInvoiceDetailProcessor;

    @InjectMocks
    private WorkOrderInvoiceDetailListener listener;

    @Test
    void handleEvent_whenAdd_shouldDelegateToProcessor() {
        InvoiceDetailEntity invoiceDetailEntity = detailEntity("detail-1");
        InvoiceDetailEvent event = event(EventType.ADD, "", invoiceDetailEntity);

        listener.handleEvent(event);

        ArgumentCaptor<InvoiceDetailEntity> entityCaptor = ArgumentCaptor.forClass(InvoiceDetailEntity.class);
        verify(workOrderInvoiceDetailProcessor).addWorkOrderDetailFor(entityCaptor.capture());
        assertThat(entityCaptor.getValue()).isSameAs(invoiceDetailEntity);

        verifyNoMoreInteractions(workOrderInvoiceDetailProcessor);
    }

    @Test
    void handleEvent_whenUpdate_shouldDeleteThenAdd() {
        InvoiceDetailEntity invoiceDetailEntity = detailEntity("detail-2");
        InvoiceDetailEvent event = event(EventType.UPDATE, "previous-1", invoiceDetailEntity);

        listener.handleEvent(event);

        InOrder inOrder = inOrder(workOrderInvoiceDetailProcessor);
        inOrder.verify(workOrderInvoiceDetailProcessor)
                .deleteWorkOrderDetailFor("previous-1", invoiceDetailEntity);
        inOrder.verify(workOrderInvoiceDetailProcessor)
                .addWorkOrderDetailFor(invoiceDetailEntity);

        verifyNoMoreInteractions(workOrderInvoiceDetailProcessor);
    }

    @Test
    void handleEvent_whenDelete_shouldDelegateToProcessor() {
        InvoiceDetailEntity invoiceDetailEntity = detailEntity("detail-3");
        InvoiceDetailEvent event = event(EventType.DELETE, "previous-2", invoiceDetailEntity);

        listener.handleEvent(event);

        ArgumentCaptor<String> previousCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<InvoiceDetailEntity> entityCaptor = ArgumentCaptor.forClass(InvoiceDetailEntity.class);
        verify(workOrderInvoiceDetailProcessor).deleteWorkOrderDetailFor(previousCaptor.capture(), entityCaptor.capture());
        assertThat(previousCaptor.getValue()).isEqualTo("previous-2");
        assertThat(entityCaptor.getValue()).isSameAs(invoiceDetailEntity);

        verifyNoMoreInteractions(workOrderInvoiceDetailProcessor);
    }

    private static InvoiceDetailEntity detailEntity(String id) {
        return InvoiceDetailEntity.builder()
                .id(id)
                .build();
    }

    private static InvoiceDetailEvent event(EventType eventType,
                                            String previousInvoiceDetailId,
                                            InvoiceDetailEntity invoiceDetailEntity) {
        return InvoiceDetailEvent.builder()
                .eventType(eventType)
                .previousInvoiceDetailId(previousInvoiceDetailId)
                .invoiceDetailEntity(invoiceDetailEntity)
                .build();
    }
}

