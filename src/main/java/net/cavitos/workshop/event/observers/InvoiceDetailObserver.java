package net.cavitos.workshop.event.observers;

import net.cavitos.workshop.event.model.EventType;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;

public interface InvoiceDetailObserver {

    void update(EventType eventType, InvoiceDetailEntity invoiceDetailEntity);
    String getName();
}
