package net.cavitos.workshop.event.listener;

import net.cavitos.workshop.event.model.EventType;
import net.cavitos.workshop.event.model.InvoiceEvent;
import net.cavitos.workshop.model.entity.InvoiceEntity;
import net.cavitos.workshop.service.InvoiceDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class InvoiceEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceEventListener.class);

    private final InvoiceDetailService invoiceDetailService;

    public InvoiceEventListener(final InvoiceDetailService invoiceDetailService) {
        this.invoiceDetailService = invoiceDetailService;
    }

    @EventListener(InvoiceEvent.class)
    public void handleEvent(final InvoiceEvent invoiceEvent) {

        final var invoiceEntity = invoiceEvent.getInvoiceEntity();
        final var eventType = invoiceEvent.getEventType();

        LOGGER.info("Invoice Event of type={} with invoice_id={}", eventType, invoiceEntity.getId());

        if (Objects.requireNonNull(eventType) == EventType.UPDATE) {

            handleInvoiceUpdateEvent(invoiceEntity);
            return;
        }

        LOGGER.info("Event type {} does not require any action", eventType);
    }

    private void handleInvoiceUpdateEvent(final InvoiceEntity invoiceEntity) {

        LOGGER.info("Update invoice date for invoice_id={}", invoiceEntity.getId());
        invoiceDetailService.updateInvoiceDate(invoiceEntity);
    }
}
