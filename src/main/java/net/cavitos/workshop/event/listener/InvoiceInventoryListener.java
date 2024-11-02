package net.cavitos.workshop.event.listener;

import net.cavitos.workshop.event.model.InvoiceEvent;
import net.cavitos.workshop.model.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

public class InvoiceInventoryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceInventoryListener.class);

    private final InventoryRepository inventoryRepository;

    public InvoiceInventoryListener(final InventoryRepository inventoryRepository) {

        this.inventoryRepository = inventoryRepository;
    }

    @EventListener(InvoiceEvent.class)
    public void handleEvent(final InvoiceEvent invoiceEvent) {


    }
}
