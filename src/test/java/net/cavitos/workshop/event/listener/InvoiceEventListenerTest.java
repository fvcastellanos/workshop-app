package net.cavitos.workshop.event.listener;

import net.cavitos.workshop.event.model.EventType;
import net.cavitos.workshop.event.model.InvoiceEvent;
import net.cavitos.workshop.model.entity.InvoiceEntity;
import net.cavitos.workshop.service.InvoiceDetailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;


@ExtendWith(MockitoExtension.class)
class InvoiceEventListenerTest {

    @Mock
    private InvoiceDetailService invoiceDetailService;

    @InjectMocks
    private InvoiceEventListener invoiceEventListener;

    @Test
    void handleEvent_whenEventTypeIsUpdate_shouldCallUpdateInvoiceDate() {
        // Arrange
        InvoiceEntity invoiceEntity = InvoiceEntity.builder().id("inv-1").build();
        InvoiceEvent event = InvoiceEvent.builder()
                .eventType(EventType.UPDATE)
                .invoiceEntity(invoiceEntity)
                .build();

        // Act
        invoiceEventListener.handleEvent(event);

        // Assert
        verify(invoiceDetailService).updateInvoiceDate(any(InvoiceEntity.class));
    }

    @Test
    void handleEvent_whenEventTypeIsNotUpdate_shouldNotCallUpdateInvoiceDate() {
        // Arrange
        InvoiceEntity invoiceEntity = InvoiceEntity.builder().id("inv-2").build();
        InvoiceEvent event = InvoiceEvent.builder()
                .eventType(EventType.ADD)
                .invoiceEntity(invoiceEntity)
                .build();

        // Act
        invoiceEventListener.handleEvent(event);

        // Assert
        verifyNoInteractions(invoiceDetailService);
    }
}
