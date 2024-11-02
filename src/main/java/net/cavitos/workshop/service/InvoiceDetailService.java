package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.model.web.InvoiceDetail;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.event.model.EventType;
import net.cavitos.workshop.event.model.InvoiceDetailEvent;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import net.cavitos.workshop.model.entity.InvoiceEntity;
import net.cavitos.workshop.model.entity.ProductEntity;
import net.cavitos.workshop.model.entity.WorkOrderEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.InvoiceDetailRepository;
import net.cavitos.workshop.model.repository.InvoiceRepository;
import net.cavitos.workshop.model.repository.ProductRepository;
import net.cavitos.workshop.model.repository.WorkOrderRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static net.cavitos.workshop.factory.BusinessExceptionFactory.createBusinessException;
import static net.cavitos.workshop.factory.DateTimeFactory.getUTCNow;

@Service
public class InvoiceDetailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceDetailService.class);

    private final InvoiceDetailRepository invoiceDetailRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final WorkOrderRepository workOrderRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    public InvoiceDetailService(final InvoiceDetailRepository invoiceDetailRepository,
                                final ProductRepository productRepository,
                                final InvoiceRepository invoiceRepository,
                                final WorkOrderRepository workOrderRepository,
                                final ApplicationEventPublisher applicationEventPublisher) {

        this.invoiceDetailRepository = invoiceDetailRepository;
        this.productRepository = productRepository;
        this.invoiceRepository = invoiceRepository;
        this.workOrderRepository = workOrderRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<InvoiceDetailEntity> getInvoiceDetails(final String invoiceId, final String tenant) {

        LOGGER.info("Retrieve invoice details for invoice_id={} and tenant={}", invoiceId, tenant);

        return invoiceDetailRepository.getInvoiceDetails(invoiceId);
    }

    @Transactional
    public InvoiceDetailEntity add(final String tenant, final String invoiceId, final InvoiceDetail invoiceDetail) {

        LOGGER.info("Add new detail for invoice_id={} and tenant={}", invoiceId, tenant);

        final var product = invoiceDetail.getProduct();
        final var productEntity = findProductEntity(tenant, product);
        final var invoiceEntity = findInvoiceEntity(tenant, invoiceId);

        WorkOrderEntity workOrderEntity = null;
        if (StringUtils.isNotBlank(invoiceDetail.getWorkOrderNumber())) {

            workOrderEntity = workOrderRepository.findByNumberEqualsIgnoreCaseAndTenant(invoiceDetail.getWorkOrderNumber(), tenant)
                    .orElseThrow(() -> createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Work Order Number not found"));

        }

        final var invoiceHolder = invoiceDetailRepository.findByInvoiceEntityIdAndProductEntityIdAndTenant(invoiceId,
                productEntity.getId(), tenant);

        if (invoiceHolder.isPresent()) {

            LOGGER.error("product_id={} already exists for invoice_id={} and tenant={}", productEntity.getId(),
                    invoiceId, tenant);

            throw createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Product already exists");
        }

        final var entity = InvoiceDetailEntity.builder()
                .id(TimeBasedGenerator.generateTimeBasedId())
                .productEntity(productEntity)
                .invoiceEntity(invoiceEntity)
                .workOrderEntity(workOrderEntity)
                .quantity(invoiceDetail.getQuantity())
                .unitPrice(invoiceDetail.getUnitPrice())
                .discountAmount(invoiceDetail.getDiscountAmount())
                .tenant(tenant)
                .created(getUTCNow())
                .build();


        invoiceDetailRepository.save(entity);

        applicationEventPublisher.publishEvent(buildInvoiceDetailEventFor(EventType.ADD, entity));

        return entity;
    }

    @Transactional
    public InvoiceDetailEntity update(final String tenant,
                                      final String invoiceId,
                                      final String invoiceDetailId,
                                      final InvoiceDetail invoiceDetail) {

        LOGGER.info("update invoice_detail_id={} for invoice_id={} and tenant={}", invoiceDetailId, invoiceId, tenant);

        final var invoiceDetailEntity = invoiceDetailRepository.findByIdAndTenant(invoiceDetailId, tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Invoice Detail not found"));

        var productEntity = invoiceDetailEntity.getProductEntity();

        final var product = invoiceDetail.getProduct();

        if (!productEntity.getCode().equalsIgnoreCase(product.getCode())) {

            productEntity = findProductEntity(tenant, product);

            final var invoiceHolder = invoiceDetailRepository.findByInvoiceEntityIdAndProductEntityIdAndTenant(invoiceId,
                    productEntity.getId(), tenant);

            final var productId = productEntity.getId();
            invoiceHolder.ifPresent(detail -> {

                if (!detail.getId().equalsIgnoreCase(invoiceDetailId)) {

                    LOGGER.error("product_id={} already exists for invoice_id={} and tenant={}", productId,
                            invoiceId, tenant);

                    throw createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Product already exists");
                }

            });
        }

        WorkOrderEntity workOrderEntity = null;
        if (StringUtils.isNotBlank(invoiceDetail.getWorkOrderNumber())) {

            workOrderEntity = workOrderRepository.findByNumberEqualsIgnoreCaseAndTenant(invoiceDetail.getWorkOrderNumber(), tenant)
                    .orElseThrow(() -> createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Work Order not found"));
        }

        invoiceDetailEntity.setWorkOrderEntity(workOrderEntity);
        invoiceDetailEntity.setProductEntity(productEntity);
        invoiceDetailEntity.setQuantity(invoiceDetail.getQuantity());
        invoiceDetailEntity.setUnitPrice(invoiceDetail.getUnitPrice());
        invoiceDetailEntity.setDiscountAmount(invoiceDetail.getDiscountAmount());

        invoiceDetailRepository.save(invoiceDetailEntity);

        applicationEventPublisher.publishEvent(buildInvoiceDetailEventFor(EventType.UPDATE, invoiceDetailEntity));

        return invoiceDetailEntity;
    }

    @Transactional
    public void delete(final String tenant, final String invoiceId, final String invoiceDetailId) {

        LOGGER.info("delete invoice_detail_id={} for invoice_id={} and tenant={}", invoiceDetailId, invoiceId, tenant);

        final var invoiceDetailEntity = invoiceDetailRepository.findByIdAndTenant(invoiceDetailId, tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Invoice Detail not found"));

        invoiceDetailRepository.delete(invoiceDetailEntity);

        applicationEventPublisher.publishEvent(buildInvoiceDetailEventFor(EventType.DELETE, invoiceDetailEntity));
    }

    // --------------------------------------------------------------------------------------------------------

    private ProductEntity findProductEntity(final String tenant, final CommonProduct product) {

        return productRepository.findByCodeEqualsIgnoreCaseAndTenant(product.getCode(), tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Product not found"));
    }

    private InvoiceEntity findInvoiceEntity(final String tenant, final String invoiceId) {

        return invoiceRepository.findByIdAndTenant(invoiceId, tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    private InvoiceDetailEvent buildInvoiceDetailEventFor(final EventType eventType,
                                                          final InvoiceDetailEntity entity) {

        return InvoiceDetailEvent.builder()
                .eventType(eventType)
                .invoiceDetailEntity(entity)
                .build();
    }
}
