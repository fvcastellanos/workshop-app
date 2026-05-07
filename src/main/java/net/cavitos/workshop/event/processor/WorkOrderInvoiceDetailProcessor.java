package net.cavitos.workshop.event.processor;

import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.WorkOrderDetailRepository;
import net.cavitos.workshop.service.PriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.nonNull;

@Component
public class WorkOrderInvoiceDetailProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderInvoiceDetailProcessor.class);

    private final WorkOrderDetailRepository workOrderDetailRepository;
    private final ZonedDateTimeFactory zonedDateTimeFactory;
    private final PriceService priceService;

    public WorkOrderInvoiceDetailProcessor(final WorkOrderDetailRepository workOrderDetailRepository,
                                           final ZonedDateTimeFactory zonedDateTimeFactory,
                                           final PriceService priceService) {

        this.workOrderDetailRepository = workOrderDetailRepository;
        this.zonedDateTimeFactory = zonedDateTimeFactory;
        this.priceService = priceService;
    }

    @Transactional
    public void deleteWorkOrderDetailFor(final String previousInvoiceDetailId, final InvoiceDetailEntity invoiceDetailEntity) {

        final var tenant = invoiceDetailEntity.getTenant();

        workOrderDetailRepository.findByInvoiceDetailIdAndTenant(previousInvoiceDetailId, tenant)
                .ifPresent(workOrderDetailEntity -> {

                    LOGGER.info("work_order_detail_id={} found, deleting it for tenant={}", workOrderDetailEntity.getId(), tenant);
                    workOrderDetailRepository.delete(workOrderDetailEntity);
                });
    }

    @Transactional
    public void addWorkOrderDetailFor(final InvoiceDetailEntity invoiceDetailEntity) {

        final var workOrderEntity = invoiceDetailEntity.getWorkOrderEntity();
        final var productEntity = invoiceDetailEntity.getProductEntity();
        final var invoiceEntity = invoiceDetailEntity.getInvoiceEntity();
        final var tenant = invoiceDetailEntity.getTenant();

        if (nonNull(invoiceDetailEntity.getWorkOrderEntity())) {
            LOGGER.info("Adding a new work order detail for work_order_number={} and tenant={}", workOrderEntity.getNumber(), tenant);

            final var salePrice = priceService.calculatePrice(invoiceDetailEntity.getQuantity() * invoiceDetailEntity.getUnitPrice(), tenant);

            final var detail = WorkOrderDetailEntity.builder()
                    .id(TimeBasedGenerator.generateTimeBasedId())
                    .invoiceDetailEntity(invoiceDetailEntity)
                    .productEntity(productEntity)
                    .workOrderEntity(workOrderEntity)
                    .operationDate(invoiceEntity.getInvoiceDate())
                    .quantity(invoiceDetailEntity.getQuantity())
                    .unitPrice(invoiceDetailEntity.getUnitPrice())
                    .salePrice(salePrice)
                    .tenant(tenant)
                    .created(zonedDateTimeFactory.getSystemNow())
                    .build();

            workOrderDetailRepository.save(detail);

            return;
        }

        LOGGER.info("No work order found for invoice_detail_id={} and tenant={}", invoiceDetailEntity.getId(), tenant);
    }
}
