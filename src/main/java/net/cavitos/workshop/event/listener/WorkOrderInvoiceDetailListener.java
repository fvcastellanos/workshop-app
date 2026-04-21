package net.cavitos.workshop.event.listener;

import net.cavitos.workshop.event.model.InvoiceDetailEvent;
import net.cavitos.workshop.event.processor.WorkOrderInvoiceDetailProcessor;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WorkOrderInvoiceDetailListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderInvoiceDetailListener.class);

    private final WorkOrderInvoiceDetailProcessor workOrderInvoiceDetailProcessor;

    public WorkOrderInvoiceDetailListener(final WorkOrderInvoiceDetailProcessor workOrderInvoiceDetailProcessor) {

        this.workOrderInvoiceDetailProcessor = workOrderInvoiceDetailProcessor;
    }

    @EventListener(InvoiceDetailEvent.class)
    public void handleEvent(final InvoiceDetailEvent invoiceDetailEvent) {

        LOGGER.info("Invoice Detail Event of type={} with invoice_detail_id={}",
                invoiceDetailEvent.getEventType(), invoiceDetailEvent.getInvoiceDetailEntity().getId());

        final var eventType = invoiceDetailEvent.getEventType();
        final var invoiceDetailEntity = invoiceDetailEvent.getInvoiceDetailEntity();

        switch (eventType) {
            case ADD -> addWorkOrderDetailFor(invoiceDetailEntity);
            case UPDATE -> updateWorkOrderDetailFor(invoiceDetailEvent.getPreviousInvoiceDetailId(), invoiceDetailEntity);
            case DELETE -> deleteWorkOrderDetailFor(invoiceDetailEvent.getPreviousInvoiceDetailId(), invoiceDetailEntity);
        }
    }

    // --------------------------------------------------------------------------------------------------------

    @Transactional
    void deleteWorkOrderDetailFor(final String previousInvoiceDetailId, final InvoiceDetailEntity invoiceDetailEntity) {

        workOrderInvoiceDetailProcessor.deleteWorkOrderDetailFor(previousInvoiceDetailId, invoiceDetailEntity);
    }

    @Transactional
    void addWorkOrderDetailFor(final InvoiceDetailEntity invoiceDetailEntity) {

        workOrderInvoiceDetailProcessor.addWorkOrderDetailFor(invoiceDetailEntity);
    }

    @Transactional
    void updateWorkOrderDetailFor(final String previousInvoiceDetailId, final InvoiceDetailEntity invoiceDetailEntity) {

        workOrderInvoiceDetailProcessor.deleteWorkOrderDetailFor(previousInvoiceDetailId, invoiceDetailEntity);
        workOrderInvoiceDetailProcessor.addWorkOrderDetailFor(invoiceDetailEntity);
    }
}
