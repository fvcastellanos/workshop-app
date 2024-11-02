package net.cavitos.workshop.event.model;

import lombok.Builder;
import lombok.Getter;
import net.cavitos.workshop.model.entity.InvoiceEntity;

@Getter
@Builder
public class InvoiceEvent {

    final EventType eventType;
    final InvoiceEntity invoiceEntity;
}
