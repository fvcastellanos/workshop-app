package net.cavitos.workshop.event.subject;

import com.google.common.collect.Lists;
import net.cavitos.workshop.event.model.EventType;
import net.cavitos.workshop.event.observers.InvoiceDetailObserver;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class InvoiceDetailObservable implements Subject<InvoiceDetailObserver> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceDetailObservable.class);

    private final List<InvoiceDetailObserver> observers;

    private EventType eventType;
    private InvoiceDetailEntity invoiceDetailEntity;

    public InvoiceDetailObservable() {

        observers = Lists.newArrayList();
    }

    @Override
    public void addObserver(final InvoiceDetailObserver observer) {

        LOGGER.info("Adding observer={} to InvoiceDetailObservable", observer.getName());
        observers.add(observer);
    }

    @Override
    public void removeObserver(final InvoiceDetailObserver observer) {

        LOGGER.info("Removing observer={} to InvoiceDetailObservable", observer.getName());
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {

        if (Objects.nonNull(eventType) && Objects.nonNull(invoiceDetailEntity)) {

            observers.forEach(observer -> {

                LOGGER.info("Notifying observer={} for a new event of type={}", observer.getName(), eventType);
                observer.update(eventType, invoiceDetailEntity);
            });

            eventType = null;
            invoiceDetailEntity = null;

            return;
        }

        LOGGER.info("Nothing to notify");
    }

    public void createEvent(final EventType eventType, final InvoiceDetailEntity invoiceDetailEntity) {

        this.eventType = eventType;
        this.invoiceDetailEntity = invoiceDetailEntity;
    }
}
