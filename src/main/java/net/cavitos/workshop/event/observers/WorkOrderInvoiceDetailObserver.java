package net.cavitos.workshop.event.observers;

import net.cavitos.workshop.event.model.EventType;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.model.repository.WorkOrderDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static net.cavitos.workshop.factory.DateTimeFactory.getUTCNow;

//@Component
public class WorkOrderInvoiceDetailObserver implements InvoiceDetailObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderInvoiceDetailObserver.class);

    private final WorkOrderDetailRepository workOrderDetailRepository;

    public WorkOrderInvoiceDetailObserver(final WorkOrderDetailRepository workOrderDetailRepository) {

        this.workOrderDetailRepository = workOrderDetailRepository;
    }

    @Override
    @Transactional
    public void update(EventType eventType, InvoiceDetailEntity invoiceDetailEntity) {

        if (Objects.nonNull(invoiceDetailEntity) && Objects.nonNull(invoiceDetailEntity.getWorkOrderEntity())) {

            switch (eventType) {
                case ADD -> addWorkOrderDetailFor(invoiceDetailEntity);
                case UPDATE -> updateWorkOrderDetailFor(invoiceDetailEntity);
                case DELETE -> deleteWorkOrderDetailFor(invoiceDetailEntity);
            }
        }
    }

    @Override
    public String getName() {
        return "WorkOrderInvoiceDetailObserver";
    }

    // --------------------------------------------------------------------------------------------------------

    @Transactional
    void deleteWorkOrderDetailFor(InvoiceDetailEntity invoiceDetailEntity) {

        final var workOrderEntity = invoiceDetailEntity.getWorkOrderEntity();
        final var productEntity = invoiceDetailEntity.getProductEntity();
        final var tenant = invoiceDetailEntity.getTenant();

        workOrderDetailRepository.findByWorkOrderEntityAndProductEntityAndTenant(workOrderEntity, productEntity, tenant)
                        .ifPresent(workOrderDetailEntity -> {

                            LOGGER.info("work_order_detail={} found, deleting it for tenant={}", workOrderDetailEntity, tenant);
                            workOrderDetailRepository.delete(workOrderDetailEntity);
                        });
    }

    @Transactional
    void addWorkOrderDetailFor(InvoiceDetailEntity invoiceDetailEntity) {

        final var workOrderEntity = invoiceDetailEntity.getWorkOrderEntity();
        final var productEntity = invoiceDetailEntity.getProductEntity();
        final var tenant = invoiceDetailEntity.getTenant();

        LOGGER.info("Adding a new work order detail for work_order_number={} and tenant={}", workOrderEntity.getNumber(), tenant);

        final var detailHolder = workOrderDetailRepository.findByWorkOrderEntityAndProductEntityAndTenant(workOrderEntity,
                productEntity, tenant);

        if (detailHolder.isPresent()) {

            LOGGER.error("Work order detail already found for work_order_number={} and tenant={}", workOrderEntity.getNumber(),
                    tenant);

            return;
        }

        final var detail = WorkOrderDetailEntity.builder()
                .id(UUID.randomUUID().toString())
                .invoiceDetailEntity(invoiceDetailEntity)
                .productEntity(productEntity)
                .workOrderEntity(workOrderEntity)
                .quantity(invoiceDetailEntity.getQuantity())
                .unitPrice(invoiceDetailEntity.getUnitPrice())
                .tenant(tenant)
                .created(getUTCNow())
                .build();

        workOrderDetailRepository.save(detail);
    }

    @Transactional
    void updateWorkOrderDetailFor(InvoiceDetailEntity invoiceDetailEntity) {

        final var workOrderEntity = invoiceDetailEntity.getWorkOrderEntity();
        final var productEntity = invoiceDetailEntity.getProductEntity();
        final var tenant = invoiceDetailEntity.getTenant();

        LOGGER.info("Update work order detail for work_order_number={} and tenant={}", workOrderEntity.getNumber(), tenant);

        workOrderDetailRepository.findByWorkOrderEntityAndProductEntityAndTenant(workOrderEntity, productEntity, tenant)
                .ifPresent(detail -> {

                    detail.setWorkOrderEntity(workOrderEntity);
                    detail.setProductEntity(productEntity);
                    detail.setQuantity(invoiceDetailEntity.getQuantity());
                    detail.setUnitPrice(invoiceDetailEntity.getUnitPrice());

                    workOrderDetailRepository.save(detail);
                });
    }
}
