package net.cavitos.workshop.event.model;

import lombok.Builder;
import lombok.Getter;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;

@Getter
@Builder
public class InvoiceDetailEvent {

    private EventType eventType;
    private InvoiceDetailEntity invoiceDetailEntity;
}
