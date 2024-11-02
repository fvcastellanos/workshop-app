package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.model.web.WorkOrderDetail;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.ProductRepository;
import net.cavitos.workshop.model.repository.WorkOrderDetailRepository;
import net.cavitos.workshop.model.repository.WorkOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static net.cavitos.workshop.factory.BusinessExceptionFactory.createBusinessException;

@Service
public class WorkOrderDetailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderDetailService.class);

    private final WorkOrderRepository workOrderRepository;

    private final WorkOrderDetailRepository workOrderDetailRepository;

    private final ProductRepository productRepository;

    public WorkOrderDetailService(final WorkOrderRepository workOrderRepository,
                                  final WorkOrderDetailRepository workOrderDetailRepository,
                                  final ProductRepository productRepository) {

        this.workOrderRepository = workOrderRepository;
        this.workOrderDetailRepository = workOrderDetailRepository;
        this.productRepository = productRepository;
    }

    public List<WorkOrderDetailEntity> getOrderDetails(final String tenant, final String orderId) {

        LOGGER.info("retrieve order details for order_id={} - tenant: {}", orderId, tenant);

        return workOrderDetailRepository.getOrderDetails(orderId);
    }

    public WorkOrderDetailEntity addOrderDetail(final String tenant,
                                                final String workOrderId,
                                                final WorkOrderDetail workOrderDetail) {

        LOGGER.info("add custom order detail for order_id={} and tenant={}", workOrderId, tenant);

        final var product = workOrderDetail.getProduct();

        final var workOrderEntity = workOrderRepository.findByIdAndTenant(workOrderId, tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Work Order not found for tenant"));

        final var productEntity = productRepository.findByCodeEqualsIgnoreCaseAndTenant(product.getCode(), tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Product code not found"));

        final var entity = WorkOrderDetailEntity.builder()
                .id(TimeBasedGenerator.generateTimeBasedId())
                .workOrderEntity(workOrderEntity)
                .productEntity(productEntity)
                .quantity(workOrderDetail.getQuantity())
                .unitPrice(workOrderDetail.getUnitPrice())
                .created(Instant.now())
                .tenant(tenant)
                .build();

        return workOrderDetailRepository.save(entity);
    }

    public void deleteOrderDetail(final String workOrderId, final String workOrderDetailId, final String tenant) {

        LOGGER.info("delete custom order detail for order_id={} and tenant={}", workOrderId, tenant);

        final var workOrderDetailEntity = workOrderDetailRepository.findByIdAndTenant(workOrderDetailId, tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Work Order Detail not found for tenant"));

        workOrderRepository.findByIdAndTenant(workOrderId, tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Work Order not found for tenant"));

        if (workOrderDetailEntity.getInvoiceDetailEntity() != null) {

            throw createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Work Order Detail has an invoice associated");
        }

        workOrderDetailRepository.delete(workOrderDetailEntity);
    }
}
