package net.cavitos.workshop.event.listener;

import net.cavitos.workshop.event.model.InvoiceDetailEvent;
import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.WorkOrderDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.nonNull;

@Component
public class WorkOrderInvoiceDetailListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderInvoiceDetailListener.class);

    private final WorkOrderDetailRepository workOrderDetailRepository;
    private final ZonedDateTimeFactory zonedDateTimeFactory;

    public WorkOrderInvoiceDetailListener(final WorkOrderDetailRepository workOrderDetailRepository,
                                          final ZonedDateTimeFactory zonedDateTimeFactory) {
        this.workOrderDetailRepository = workOrderDetailRepository;
        this.zonedDateTimeFactory = zonedDateTimeFactory;
    }

    @EventListener(InvoiceDetailEvent.class)
    public void handleEvent(final InvoiceDetailEvent invoiceDetailEvent) {

        LOGGER.info("Invoice Detail Event of type={} with invoice_detail_id={}",
                invoiceDetailEvent.getEventType(), invoiceDetailEvent.getInvoiceDetailEntity().getId());

        final var eventType = invoiceDetailEvent.getEventType();
        final var invoiceDetailEntity = invoiceDetailEvent.getInvoiceDetailEntity();

        switch (eventType) {
            case ADD -> addWorkOrderDetailFor(invoiceDetailEntity);
            case UPDATE -> updateWorkOrderDetailFor(invoiceDetailEntity);
            case DELETE -> deleteWorkOrderDetailFor(invoiceDetailEntity);
        }
    }

    // --------------------------------------------------------------------------------------------------------

    @Transactional
    void deleteWorkOrderDetailFor(final InvoiceDetailEntity invoiceDetailEntity) {

        final var workOrderEntity = invoiceDetailEntity.getWorkOrderEntity();
        final var productEntity = invoiceDetailEntity.getProductEntity();
        final var tenant = invoiceDetailEntity.getTenant();

        workOrderDetailRepository.findDetailByInvoiceDetail(invoiceDetailEntity.getId(), productEntity.getId(), tenant)
                .ifPresent(workOrderDetailEntity -> {

                    LOGGER.info("work_order_detail={} found, deleting it for tenant={}", workOrderDetailEntity, tenant);
                    workOrderDetailRepository.delete(workOrderDetailEntity);
                });
    }

    @Transactional
    void addWorkOrderDetailFor(final InvoiceDetailEntity invoiceDetailEntity) {

        final var workOrderEntity = invoiceDetailEntity.getWorkOrderEntity();
        final var productEntity = invoiceDetailEntity.getProductEntity();
        final var tenant = invoiceDetailEntity.getTenant();

        if (nonNull(invoiceDetailEntity.getWorkOrderEntity())) {
            LOGGER.info("Adding a new work order detail for work_order_number={} and tenant={}", workOrderEntity.getNumber(), tenant);

            final var detailHolder = workOrderDetailRepository.findDetailByWorkOrder(workOrderEntity.getId(),
                    productEntity.getId(), tenant);

            if (detailHolder.isPresent()) {

                LOGGER.error("Work order detail already found for work_order_number={} and tenant={}",
                        workOrderEntity.getNumber(), tenant);

                return;
            }

            final var detail = WorkOrderDetailEntity.builder()
                    .id(TimeBasedGenerator.generateTimeBasedId())
                    .invoiceDetailEntity(invoiceDetailEntity)
                    .productEntity(productEntity)
                    .workOrderEntity(workOrderEntity)
                    .quantity(invoiceDetailEntity.getQuantity())
                    .unitPrice(invoiceDetailEntity.getUnitPrice())
                    .tenant(tenant)
                    .created(zonedDateTimeFactory.getSystemNow())
                    .build();

            workOrderDetailRepository.save(detail);

            return;
        }

        LOGGER.info("No work order found for invoice_detail_id={} and tenant={}", invoiceDetailEntity.getId(), tenant);
    }

    @Transactional
    void updateWorkOrderDetailFor(final InvoiceDetailEntity invoiceDetailEntity) {

        deleteWorkOrderDetailFor(invoiceDetailEntity);
        addWorkOrderDetailFor(invoiceDetailEntity);
    }
}
