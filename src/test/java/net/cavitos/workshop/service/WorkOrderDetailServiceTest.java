package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.exception.BusinessException;
import net.cavitos.workshop.domain.model.web.WorkOrderDetail;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.event.model.EventType;
import net.cavitos.workshop.event.model.WorkOrderDetailEvent;
import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import net.cavitos.workshop.model.entity.ProductEntity;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.model.entity.WorkOrderEntity;
import net.cavitos.workshop.model.repository.ProductRepository;
import net.cavitos.workshop.model.repository.WorkOrderDetailRepository;
import net.cavitos.workshop.model.repository.WorkOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkOrderDetailServiceTest {

    private static final String TENANT = "tenant-a";

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private WorkOrderDetailRepository workOrderDetailRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ZonedDateTimeFactory zonedDateTimeFactory;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private WorkOrderDetailService service;

    @Test
    void getOrderDetailsReturnsRepositoryResults() {

        List<WorkOrderDetailEntity> expected = List.of(WorkOrderDetailEntity.builder().id("detail-1").build());

        when(workOrderDetailRepository.getOrderDetails("order-1")).thenReturn(expected);

        List<WorkOrderDetailEntity> result = service.getOrderDetails(TENANT, "order-1");

        assertThat(result).isSameAs(expected);
        verify(workOrderDetailRepository).getOrderDetails("order-1");
    }

    @Test
    void addOrderDetailWithProductBuildsEntityAndPublishesEvent() {

        WorkOrderDetail workOrderDetail = buildWorkOrderDetail("2026-04-23", true);
        WorkOrderEntity workOrderEntity = buildWorkOrderEntity();
        ProductEntity productEntity = buildProductEntity();

        when(workOrderRepository.findByIdAndTenant("order-1", TENANT))
                .thenReturn(Optional.of(workOrderEntity));
        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("PROD-1", TENANT))
                .thenReturn(Optional.of(productEntity));
        when(zonedDateTimeFactory.buildInstantFrom("2026-04-23"))
                .thenReturn(Instant.parse("2026-04-23T10:15:30Z"));
        when(zonedDateTimeFactory.getSystemNow())
                .thenReturn(Instant.parse("2026-04-23T10:20:00Z"));

        assertThatCode(() -> service.addOrderDetail(TENANT, "order-1", workOrderDetail)).doesNotThrowAnyException();

        ArgumentCaptor<WorkOrderDetailEntity> entityCaptor = ArgumentCaptor.forClass(WorkOrderDetailEntity.class);
        verify(workOrderDetailRepository).save(entityCaptor.capture());

        WorkOrderDetailEntity savedEntity = entityCaptor.getValue();
        assertThat(savedEntity.getWorkOrderEntity()).isSameAs(workOrderEntity);
        assertThat(savedEntity.getProductEntity()).isSameAs(productEntity);
        assertThat(savedEntity.getDescription()).isEqualTo("Oil change");
        assertThat(savedEntity.getNotes()).isEqualTo("Use synthetic");
        assertThat(savedEntity.getQuantity()).isEqualTo(2.0);
        assertThat(savedEntity.getUnitPrice()).isEqualTo(25.0);
        assertThat(savedEntity.getSalePrice()).isEqualTo(45.0);
        assertThat(savedEntity.getOperationDate()).isEqualTo(Instant.parse("2026-04-23T10:15:30Z"));
        assertThat(savedEntity.getTenant()).isEqualTo(TENANT);

        ArgumentCaptor<WorkOrderDetailEvent> eventCaptor = ArgumentCaptor.forClass(WorkOrderDetailEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        WorkOrderDetailEvent event = eventCaptor.getValue();
        assertThat(event.getEventType()).isEqualTo(EventType.ADD);
        assertThat(event.getWorkOrderDetailEntity()).isSameAs(savedEntity);
    }

    @Test
    void addOrderDetailWithoutProductSkipsProductLookup() {

        WorkOrderDetail workOrderDetail = buildWorkOrderDetail(null, false);
        WorkOrderEntity workOrderEntity = buildWorkOrderEntity();

        when(workOrderRepository.findByIdAndTenant("order-1", TENANT))
                .thenReturn(Optional.of(workOrderEntity));
        when(zonedDateTimeFactory.getSystemNow())
                .thenReturn(Instant.parse("2026-04-23T10:20:00Z"));

        assertThatCode(() -> service.addOrderDetail(TENANT, "order-1", workOrderDetail)).doesNotThrowAnyException();

        verify(productRepository, never()).findByCodeEqualsIgnoreCaseAndTenant(eq("PROD-1"), eq(TENANT));
        ArgumentCaptor<WorkOrderDetailEntity> entityCaptor = ArgumentCaptor.forClass(WorkOrderDetailEntity.class);
        verify(workOrderDetailRepository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getProductEntity()).isNull();
    }

    @Test
    void deleteOrderDetailRemovesEntityAndPublishesEvent() {

        WorkOrderDetailEntity detailEntity = WorkOrderDetailEntity.builder()
                .id("detail-1")
                .tenant(TENANT)
                .build();
        WorkOrderEntity workOrderEntity = buildWorkOrderEntity();

        when(workOrderDetailRepository.findByIdAndTenant("detail-1", TENANT))
                .thenReturn(Optional.of(detailEntity));
        when(workOrderRepository.findByIdAndTenant("order-1", TENANT))
                .thenReturn(Optional.of(workOrderEntity));

        assertThatCode(() -> service.deleteOrderDetail("order-1", "detail-1", TENANT)).doesNotThrowAnyException();

        verify(workOrderDetailRepository).delete(detailEntity);

        ArgumentCaptor<WorkOrderDetailEvent> eventCaptor = ArgumentCaptor.forClass(WorkOrderDetailEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEventType()).isEqualTo(EventType.DELETE);
        assertThat(eventCaptor.getValue().getWorkOrderDetailEntity()).isSameAs(detailEntity);
    }

    @Test
    void deleteOrderDetailWhenInvoiceExistsThrows() {

        WorkOrderDetailEntity detailEntity = WorkOrderDetailEntity.builder()
                .id("detail-1")
                .tenant(TENANT)
                .invoiceDetailEntity(InvoiceDetailEntity.builder().id("invoice-detail-1").build())
                .build();

        when(workOrderDetailRepository.findByIdAndTenant("detail-1", TENANT))
                .thenReturn(Optional.of(detailEntity));
        when(workOrderRepository.findByIdAndTenant("order-1", TENANT))
                .thenReturn(Optional.of(buildWorkOrderEntity()));

        assertThatThrownBy(() -> service.deleteOrderDetail("order-1", "detail-1", TENANT))
                .isInstanceOf(BusinessException.class);

        verify(workOrderDetailRepository, never()).delete(detailEntity);
    }

    @Test
    void updateOrderDetailUpdatesEntityAndPublishesEvent() {

        WorkOrderDetailEntity detailEntity = WorkOrderDetailEntity.builder()
                .id("detail-1")
                .tenant(TENANT)
                .build();
        WorkOrderEntity workOrderEntity = buildWorkOrderEntity();
        ProductEntity productEntity = buildProductEntity();
        WorkOrderDetail workOrderDetail = buildWorkOrderDetail("2026-04-23", true);

        when(workOrderDetailRepository.findByIdAndTenant("detail-1", TENANT))
                .thenReturn(Optional.of(detailEntity));
        when(workOrderRepository.findByIdAndTenant("order-1", TENANT))
                .thenReturn(Optional.of(workOrderEntity));
        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("PROD-1", TENANT))
                .thenReturn(Optional.of(productEntity));
        when(zonedDateTimeFactory.buildInstantFrom("2026-04-23"))
                .thenReturn(Instant.parse("2026-04-23T10:15:30Z"));

        assertThatCode(() -> service.updateOrderDetail("order-1", "detail-1", workOrderDetail, TENANT))
                .doesNotThrowAnyException();

        ArgumentCaptor<WorkOrderDetailEntity> entityCaptor = ArgumentCaptor.forClass(WorkOrderDetailEntity.class);
        verify(workOrderDetailRepository).save(entityCaptor.capture());

        WorkOrderDetailEntity savedEntity = entityCaptor.getValue();
        assertThat(savedEntity.getWorkOrderEntity()).isSameAs(workOrderEntity);
        assertThat(savedEntity.getProductEntity()).isSameAs(productEntity);
        assertThat(savedEntity.getDescription()).isEqualTo("Oil change");
        assertThat(savedEntity.getNotes()).isEqualTo("Use synthetic");
        assertThat(savedEntity.getQuantity()).isEqualTo(2.0);
        assertThat(savedEntity.getUnitPrice()).isEqualTo(25.0);
        assertThat(savedEntity.getSalePrice()).isEqualTo(45.0);
        assertThat(savedEntity.getOperationDate()).isEqualTo(Instant.parse("2026-04-23T10:15:30Z"));

        ArgumentCaptor<WorkOrderDetailEvent> eventCaptor = ArgumentCaptor.forClass(WorkOrderDetailEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEventType()).isEqualTo(EventType.UPDATE);
        assertThat(eventCaptor.getValue().getWorkOrderDetailEntity()).isSameAs(savedEntity);
    }

    private WorkOrderDetail buildWorkOrderDetail(final String operationDate, final boolean includeProduct) {

        WorkOrderDetail detail = new WorkOrderDetail();
        detail.setDescription("Oil change");
        detail.setNotes("Use synthetic");
        detail.setQuantity(2.0);
        detail.setUnitPrice(25.0);
        detail.setSalePrice(45.0);
        detail.setOperationDate(operationDate);

        if (includeProduct) {
            CommonProduct product = new CommonProduct();
            product.setCode("PROD-1");
            product.setName("Product One");
            product.setStorable(true);
            detail.setProduct(product);
        }

        return detail;
    }

    private WorkOrderEntity buildWorkOrderEntity() {

        return WorkOrderEntity.builder()
                .id("order-1")
                .number("WO-1")
                .orderDate(Instant.parse("2026-04-22T10:00:00Z"))
                .tenant(TENANT)
                .build();
    }

    private ProductEntity buildProductEntity() {

        return ProductEntity.builder()
                .id("product-1")
                .code("PROD-1")
                .name("Product One")
                .storable(true)
                .tenant(TENANT)
                .build();
    }
}

