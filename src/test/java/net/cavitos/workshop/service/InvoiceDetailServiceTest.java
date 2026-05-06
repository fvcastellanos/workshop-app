package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.exception.BusinessException;
import net.cavitos.workshop.domain.model.web.InvoiceDetail;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.event.model.EventType;
import net.cavitos.workshop.event.model.InvoiceDetailEvent;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import net.cavitos.workshop.model.entity.InvoiceEntity;
import net.cavitos.workshop.model.entity.ProductEntity;
import net.cavitos.workshop.model.entity.WorkOrderEntity;
import net.cavitos.workshop.model.repository.InvoiceDetailRepository;
import net.cavitos.workshop.model.repository.InvoiceRepository;
import net.cavitos.workshop.model.repository.ProductRepository;
import net.cavitos.workshop.model.repository.WorkOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class InvoiceDetailServiceTest {

    @Mock
    private InvoiceDetailRepository invoiceDetailRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private InvoiceDetailService invoiceDetailService;

    @Test
    void getInvoiceDetails_shouldReturnRepositoryList() {
        String invoiceId = "inv-1";
        List<InvoiceDetailEntity> expected = List.of(detailEntity("detail-1", "prod-1", invoiceId));

        when(invoiceDetailRepository.getInvoiceDetails(invoiceId)).thenReturn(expected);

        List<InvoiceDetailEntity> result = invoiceDetailService.getInvoiceDetails(invoiceId, "tenant-1");

        assertThat(result).isSameAs(expected);
        verify(invoiceDetailRepository).getInvoiceDetails(invoiceId);
        verifyNoMoreInteractions(invoiceDetailRepository);
        verifyNoInteractions(productRepository, invoiceRepository, workOrderRepository, applicationEventPublisher);
    }

    @Test
    void add_shouldSaveAndPublishEvent_whenProductAndInvoiceFound() {
        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        CommonProduct product = commonProduct("P-1");
        InvoiceDetail request = invoiceDetail(product, "", 2, 15.5, 1.2);
        ProductEntity productEntity = productEntity("prod-1", "P-1");
        InvoiceEntity invoiceEntity = invoiceEntity(invoiceId, Instant.parse("2024-02-10T10:15:30Z"));

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-1", tenant))
                .thenReturn(Optional.of(productEntity));
        when(invoiceRepository.findByIdAndTenant(invoiceId, tenant)).thenReturn(Optional.of(invoiceEntity));

        InvoiceDetailEntity result = invoiceDetailService.add(tenant, invoiceId, request);

        ArgumentCaptor<InvoiceDetailEntity> entityCaptor = ArgumentCaptor.forClass(InvoiceDetailEntity.class);
        verify(invoiceDetailRepository).save(entityCaptor.capture());
        InvoiceDetailEntity saved = entityCaptor.getValue();

        assertThat(result).isSameAs(saved);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProductEntity()).isSameAs(productEntity);
        assertThat(saved.getInvoiceEntity()).isSameAs(invoiceEntity);
        assertThat(saved.getWorkOrderEntity()).isNull();
        assertThat(saved.getQuantity()).isEqualTo(2);
        assertThat(saved.getUnitPrice()).isEqualTo(15.5);
        assertThat(saved.getDiscountAmount()).isEqualTo(1.2);
        assertThat(saved.getTenant()).isEqualTo(tenant);
        assertThat(saved.getCreated()).isEqualTo(invoiceEntity.getInvoiceDate());

        ArgumentCaptor<InvoiceDetailEvent> eventCaptor = ArgumentCaptor.forClass(InvoiceDetailEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        InvoiceDetailEvent event = eventCaptor.getValue();
        assertThat(event.getEventType()).isEqualTo(EventType.ADD);
        assertThat(event.getPreviousInvoiceDetailId()).isEmpty();
        assertThat(event.getInvoiceDetailEntity()).isSameAs(saved);

        verifyNoInteractions(workOrderRepository);
        verifyNoMoreInteractions(invoiceDetailRepository, productRepository, invoiceRepository, applicationEventPublisher);
    }

    @Test
    void add_whenProductNotFound_shouldThrowBusinessException() {
        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        CommonProduct product = commonProduct("P-1");
        InvoiceDetail request = invoiceDetail(product, "", 2, 15.5, 1.2);

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-1", tenant))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceDetailService.add(tenant, invoiceId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Product not found");
                });

        verify(productRepository).findByCodeEqualsIgnoreCaseAndTenant("P-1", tenant);
        verifyNoInteractions(applicationEventPublisher, workOrderRepository);
        verifyNoMoreInteractions(invoiceDetailRepository, invoiceRepository);
    }

    @Test
    void add_whenInvoiceNotFound_shouldThrowBusinessException() {
        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        CommonProduct product = commonProduct("P-1");
        InvoiceDetail request = invoiceDetail(product, "", 2, 15.5, 1.2);
        ProductEntity productEntity = productEntity("prod-1", "P-1");

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-1", tenant))
                .thenReturn(Optional.of(productEntity));
        when(invoiceRepository.findByIdAndTenant(invoiceId, tenant)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceDetailService.add(tenant, invoiceId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(businessException.getMessage()).isEqualTo("Invoice not found");
                });

        verify(invoiceRepository).findByIdAndTenant(invoiceId, tenant);
        verifyNoInteractions(applicationEventPublisher, workOrderRepository);
        verifyNoMoreInteractions(invoiceDetailRepository, productRepository);
    }

    @Test
    void add_whenWorkOrderNumberProvided_shouldLookupWorkOrderAndSave() {

        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        String workOrderNumber = "WO-1001";
        CommonProduct product = commonProduct("P-1");
        InvoiceDetail request = invoiceDetail(product, workOrderNumber, 2, 15.5, 1.2);
        ProductEntity productEntity = productEntity("prod-1", "P-1");
        InvoiceEntity invoiceEntity = invoiceEntity(invoiceId, Instant.parse("2024-02-10T10:15:30Z"));
        WorkOrderEntity workOrderEntity = WorkOrderEntity.builder()
                .id("wo-1")
                .number(workOrderNumber)
                .tenant(tenant)
                .build();

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-1", tenant))
                .thenReturn(Optional.of(productEntity));
        when(invoiceRepository.findByIdAndTenant(invoiceId, tenant)).thenReturn(Optional.of(invoiceEntity));
        when(workOrderRepository.findByNumberEqualsIgnoreCaseAndTenant(workOrderNumber, tenant))
                .thenReturn(Optional.of(workOrderEntity));

        InvoiceDetailEntity result = invoiceDetailService.add(tenant, invoiceId, request);

        ArgumentCaptor<InvoiceDetailEntity> entityCaptor = ArgumentCaptor.forClass(InvoiceDetailEntity.class);
        verify(invoiceDetailRepository).save(entityCaptor.capture());
        InvoiceDetailEntity saved = entityCaptor.getValue();

        assertThat(result).isSameAs(saved);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProductEntity()).isSameAs(productEntity);
        assertThat(saved.getInvoiceEntity()).isSameAs(invoiceEntity);
        assertThat(saved.getWorkOrderEntity()).isSameAs(workOrderEntity);
        assertThat(saved.getQuantity()).isEqualTo(2);
        assertThat(saved.getUnitPrice()).isEqualTo(15.5);
        assertThat(saved.getDiscountAmount()).isEqualTo(1.2);
        assertThat(saved.getTenant()).isEqualTo(tenant);
        assertThat(saved.getCreated()).isEqualTo(invoiceEntity.getInvoiceDate());

        ArgumentCaptor<InvoiceDetailEvent> eventCaptor = ArgumentCaptor.forClass(InvoiceDetailEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        InvoiceDetailEvent event = eventCaptor.getValue();
        assertThat(event.getEventType()).isEqualTo(EventType.ADD);
        assertThat(event.getPreviousInvoiceDetailId()).isEmpty();
        assertThat(event.getInvoiceDetailEntity()).isSameAs(saved);

        verifyNoMoreInteractions(invoiceDetailRepository, productRepository, invoiceRepository, workOrderRepository, applicationEventPublisher);
    }

    @Test
    void add_whenWorkOrderNotFound_shouldThrowBusinessException() {
        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        String workOrderNumber = "WO-1001";
        CommonProduct product = commonProduct("P-1");
        InvoiceDetail request = invoiceDetail(product, workOrderNumber, 2, 15.5, 1.2);
        ProductEntity productEntity = productEntity("prod-1", "P-1");
        InvoiceEntity invoiceEntity = invoiceEntity(invoiceId, Instant.parse("2024-02-10T10:15:30Z"));

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-1", tenant))
                .thenReturn(Optional.of(productEntity));
        when(invoiceRepository.findByIdAndTenant(invoiceId, tenant)).thenReturn(Optional.of(invoiceEntity));
        when(workOrderRepository.findByNumberEqualsIgnoreCaseAndTenant(workOrderNumber, tenant))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceDetailService.add(tenant, invoiceId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Work Order Number not found");
                });

        verify(workOrderRepository).findByNumberEqualsIgnoreCaseAndTenant(workOrderNumber, tenant);
        verifyNoInteractions(applicationEventPublisher);
        verifyNoMoreInteractions(invoiceDetailRepository, productRepository, invoiceRepository);
    }

    @Test
    void update_shouldSaveAndPublishEvent_whenProductUnchanged() {
        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        String invoiceDetailId = "detail-1";
        ProductEntity productEntity = productEntity("prod-1", "P-1");
        InvoiceDetailEntity existing = detailEntity(invoiceDetailId, "prod-1", invoiceId);
        existing.setProductEntity(productEntity);
        InvoiceDetail request = invoiceDetail(commonProduct("P-1"), "", 3, 20.5, 2.5);

        when(invoiceDetailRepository.findByIdAndTenant(invoiceDetailId, tenant)).thenReturn(Optional.of(existing));

        InvoiceDetailEntity result = invoiceDetailService.update(tenant, invoiceId, invoiceDetailId, request);

        assertThat(result).isSameAs(existing);
        assertThat(existing.getProductEntity()).isSameAs(productEntity);
        assertThat(existing.getQuantity()).isEqualTo(3);
        assertThat(existing.getUnitPrice()).isEqualTo(20.5);
        assertThat(existing.getDiscountAmount()).isEqualTo(2.5);
        assertThat(existing.getWorkOrderEntity()).isNull();

        verify(invoiceDetailRepository).save(existing);

        ArgumentCaptor<InvoiceDetailEvent> eventCaptor = ArgumentCaptor.forClass(InvoiceDetailEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        InvoiceDetailEvent event = eventCaptor.getValue();
        assertThat(event.getEventType()).isEqualTo(EventType.UPDATE);
        assertThat(event.getPreviousInvoiceDetailId()).isEqualTo(invoiceDetailId);
        assertThat(event.getInvoiceDetailEntity()).isSameAs(existing);

        verifyNoInteractions(productRepository, workOrderRepository, invoiceRepository);
        verifyNoMoreInteractions(invoiceDetailRepository, applicationEventPublisher);
    }

    @Test
    void update_whenWorkOrderNumberProvided_shouldLookupWorkOrderAndSave() {
        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        String invoiceDetailId = "detail-1";
        String workOrderNumber = "WO-1001";
        ProductEntity productEntity = productEntity("prod-1", "P-1");
        InvoiceDetailEntity existing = detailEntity(invoiceDetailId, "prod-1", invoiceId);
        existing.setProductEntity(productEntity);
        InvoiceDetail request = invoiceDetail(commonProduct("P-1"), workOrderNumber, 3, 20.5, 2.5);
        WorkOrderEntity workOrderEntity = WorkOrderEntity.builder()
                .id("wo-1")
                .number(workOrderNumber)
                .tenant(tenant)
                .build();

        when(invoiceDetailRepository.findByIdAndTenant(invoiceDetailId, tenant)).thenReturn(Optional.of(existing));
        when(workOrderRepository.findByNumberEqualsIgnoreCaseAndTenant(workOrderNumber, tenant))
                .thenReturn(Optional.of(workOrderEntity));

        InvoiceDetailEntity result = invoiceDetailService.update(tenant, invoiceId, invoiceDetailId, request);

        assertThat(result).isSameAs(existing);
        assertThat(existing.getProductEntity()).isSameAs(productEntity);
        assertThat(existing.getQuantity()).isEqualTo(3);
        assertThat(existing.getUnitPrice()).isEqualTo(20.5);
        assertThat(existing.getDiscountAmount()).isEqualTo(2.5);
        assertThat(existing.getWorkOrderEntity()).isSameAs(workOrderEntity);

        verify(invoiceDetailRepository).save(existing);

        ArgumentCaptor<InvoiceDetailEvent> eventCaptor = ArgumentCaptor.forClass(InvoiceDetailEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        InvoiceDetailEvent event = eventCaptor.getValue();
        assertThat(event.getEventType()).isEqualTo(EventType.UPDATE);
        assertThat(event.getPreviousInvoiceDetailId()).isEqualTo(invoiceDetailId);
        assertThat(event.getInvoiceDetailEntity()).isSameAs(existing);

        verifyNoInteractions(productRepository, invoiceRepository);
        verifyNoMoreInteractions(invoiceDetailRepository, workOrderRepository, applicationEventPublisher);
    }

    @Test
    void update_whenWorkOrderNotFound_shouldThrowBusinessException() {
        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        String invoiceDetailId = "detail-1";
        String workOrderNumber = "WO-1001";
        ProductEntity productEntity = productEntity("prod-1", "P-1");
        InvoiceDetailEntity existing = detailEntity(invoiceDetailId, "prod-1", invoiceId);
        existing.setProductEntity(productEntity);
        InvoiceDetail request = invoiceDetail(commonProduct("P-1"), workOrderNumber, 3, 20.5, 2.5);

        when(invoiceDetailRepository.findByIdAndTenant(invoiceDetailId, tenant)).thenReturn(Optional.of(existing));
        when(workOrderRepository.findByNumberEqualsIgnoreCaseAndTenant(workOrderNumber, tenant))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceDetailService.update(tenant, invoiceId, invoiceDetailId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Work Order not found");
                });

        verify(workOrderRepository).findByNumberEqualsIgnoreCaseAndTenant(workOrderNumber, tenant);
        verifyNoInteractions(applicationEventPublisher);
        verifyNoMoreInteractions(invoiceDetailRepository, productRepository, invoiceRepository);
    }

    @Test
    void update_whenDetailNotFound_shouldThrowBusinessException() {
        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        String invoiceDetailId = "detail-1";
        InvoiceDetail request = invoiceDetail(commonProduct("P-1"), "", 3, 20.5, 2.5);

        when(invoiceDetailRepository.findByIdAndTenant(invoiceDetailId, tenant)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceDetailService.update(tenant, invoiceId, invoiceDetailId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(businessException.getMessage()).isEqualTo("Invoice Detail not found");
                });

        verify(invoiceDetailRepository).findByIdAndTenant(invoiceDetailId, tenant);
        verifyNoInteractions(applicationEventPublisher, productRepository, workOrderRepository, invoiceRepository);
        verifyNoMoreInteractions(invoiceDetailRepository);
    }

    @Test
    void update_whenProductChanged_shouldLookupNewProductAndSave() {

        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        String invoiceDetailId = "detail-1";
        ProductEntity existingProduct = productEntity("prod-1", "P-1");
        ProductEntity newProduct = productEntity("prod-2", "P-2");
        InvoiceDetailEntity existing = detailEntity(invoiceDetailId, "prod-1", invoiceId);
        existing.setProductEntity(existingProduct);
        InvoiceDetail request = invoiceDetail(commonProduct("P-2"), "", 3, 20.5, 2.5);

        when(invoiceDetailRepository.findByIdAndTenant(invoiceDetailId, tenant)).thenReturn(Optional.of(existing));
        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-2", tenant))
                .thenReturn(Optional.of(newProduct));
        when(invoiceDetailRepository.findByInvoiceEntityIdAndProductEntityIdAndTenant(invoiceId, "prod-2", tenant))
                .thenReturn(Optional.empty());

        InvoiceDetailEntity result = invoiceDetailService.update(tenant, invoiceId, invoiceDetailId, request);

        assertThat(result).isSameAs(existing);
        assertThat(existing.getProductEntity()).isSameAs(newProduct);
        assertThat(existing.getQuantity()).isEqualTo(3);
        assertThat(existing.getUnitPrice()).isEqualTo(20.5);
        assertThat(existing.getDiscountAmount()).isEqualTo(2.5);
        assertThat(existing.getWorkOrderEntity()).isNull();

        verify(invoiceDetailRepository).save(existing);

        ArgumentCaptor<InvoiceDetailEvent> eventCaptor = ArgumentCaptor.forClass(InvoiceDetailEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        InvoiceDetailEvent event = eventCaptor.getValue();
        assertThat(event.getEventType()).isEqualTo(EventType.UPDATE);
        assertThat(event.getPreviousInvoiceDetailId()).isEqualTo(invoiceDetailId);
        assertThat(event.getInvoiceDetailEntity()).isSameAs(existing);

        verifyNoInteractions(workOrderRepository, invoiceRepository);
        verifyNoMoreInteractions(invoiceDetailRepository, productRepository, applicationEventPublisher);
    }

    @Test
    void update_whenProductChangedAndAlreadyExists_shouldThrowBusinessException() {
        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        String invoiceDetailId = "detail-1";
        ProductEntity existingProduct = productEntity("prod-1", "P-1");
        ProductEntity newProduct = productEntity("prod-2", "P-2");
        InvoiceDetailEntity existing = detailEntity(invoiceDetailId, "prod-1", invoiceId);
        existing.setProductEntity(existingProduct);
        InvoiceDetailEntity otherDetail = detailEntity("detail-2", "prod-2", invoiceId);
        InvoiceDetail request = invoiceDetail(commonProduct("P-2"), "", 3, 20.5, 2.5);

        when(invoiceDetailRepository.findByIdAndTenant(invoiceDetailId, tenant)).thenReturn(Optional.of(existing));
        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-2", tenant))
                .thenReturn(Optional.of(newProduct));
        when(invoiceDetailRepository.findByInvoiceEntityIdAndProductEntityIdAndTenant(invoiceId, "prod-2", tenant))
                .thenReturn(Optional.of(otherDetail));

        assertThatThrownBy(() -> invoiceDetailService.update(tenant, invoiceId, invoiceDetailId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Product already exists");
                });

        verify(invoiceDetailRepository).findByInvoiceEntityIdAndProductEntityIdAndTenant(invoiceId, "prod-2", tenant);
        verifyNoInteractions(applicationEventPublisher, workOrderRepository);
        verifyNoMoreInteractions(invoiceDetailRepository, productRepository, invoiceRepository);
    }

    @Test
    void update_whenProductChangedAndNewProductNotFound_shouldThrowBusinessException() {
        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        String invoiceDetailId = "detail-1";
        ProductEntity existingProduct = productEntity("prod-1", "P-1");
        InvoiceDetailEntity existing = detailEntity(invoiceDetailId, "prod-1", invoiceId);
        existing.setProductEntity(existingProduct);
        InvoiceDetail request = invoiceDetail(commonProduct("P-2"), "", 3, 20.5, 2.5);

        when(invoiceDetailRepository.findByIdAndTenant(invoiceDetailId, tenant)).thenReturn(Optional.of(existing));
        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-2", tenant))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceDetailService.update(tenant, invoiceId, invoiceDetailId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Product not found");
                });

        verify(productRepository).findByCodeEqualsIgnoreCaseAndTenant("P-2", tenant);
        verifyNoInteractions(applicationEventPublisher, workOrderRepository, invoiceRepository);
        verifyNoMoreInteractions(invoiceDetailRepository);
    }

    @Test
    void delete_shouldRemoveEntityAndPublishEvent() {

        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        String invoiceDetailId = "detail-1";
        InvoiceDetailEntity existing = detailEntity(invoiceDetailId, "prod-1", invoiceId);

        when(invoiceDetailRepository.findByIdAndTenant(invoiceDetailId, tenant)).thenReturn(Optional.of(existing));

        invoiceDetailService.delete(tenant, invoiceId, invoiceDetailId);

        verify(invoiceDetailRepository).delete(existing);

        ArgumentCaptor<InvoiceDetailEvent> eventCaptor = ArgumentCaptor.forClass(InvoiceDetailEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        InvoiceDetailEvent event = eventCaptor.getValue();
        assertThat(event.getEventType()).isEqualTo(EventType.DELETE);
        assertThat(event.getPreviousInvoiceDetailId()).isEqualTo(invoiceDetailId);
        assertThat(event.getInvoiceDetailEntity()).isSameAs(existing);

        verifyNoMoreInteractions(invoiceDetailRepository, applicationEventPublisher);
        verifyNoInteractions(productRepository, workOrderRepository, invoiceRepository);
    }

    @Test
    void delete_whenDetailNotFound_shouldThrowBusinessException() {
        String tenant = "tenant-1";
        String invoiceId = "inv-1";
        String invoiceDetailId = "detail-1";

        when(invoiceDetailRepository.findByIdAndTenant(invoiceDetailId, tenant)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceDetailService.delete(tenant, invoiceId, invoiceDetailId))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(businessException.getMessage()).isEqualTo("Invoice Detail not found");
                });

        verify(invoiceDetailRepository).findByIdAndTenant(invoiceDetailId, tenant);
        verifyNoInteractions(applicationEventPublisher, productRepository, workOrderRepository, invoiceRepository);
        verifyNoMoreInteractions(invoiceDetailRepository);
    }

    @Test
    void updateInvoiceDate_whenNoDetails_shouldNotSaveOrPublish() {
        InvoiceEntity invoiceEntity = invoiceEntity("inv-1", Instant.parse("2024-02-11T10:15:30Z"));

        when(invoiceDetailRepository.getInvoiceDetails(invoiceEntity.getId())).thenReturn(List.of());

        invoiceDetailService.updateInvoiceDate(invoiceEntity);

        verify(invoiceDetailRepository).getInvoiceDetails(invoiceEntity.getId());
        verifyNoInteractions(applicationEventPublisher);
        verifyNoMoreInteractions(invoiceDetailRepository);
        verifyNoInteractions(productRepository, workOrderRepository, invoiceRepository);
    }

    @Test
    void updateInvoiceDate_shouldUpdateDetailCreatedAndPublishEvent() {

        InvoiceEntity invoiceEntity = invoiceEntity("inv-1", Instant.parse("2024-02-11T10:15:30Z"));
        InvoiceDetailEntity detailOne = detailEntity("detail-1", "prod-1", invoiceEntity.getId());
        InvoiceDetailEntity detailTwo = detailEntity("detail-2", "prod-2", invoiceEntity.getId());

        when(invoiceDetailRepository.getInvoiceDetails(invoiceEntity.getId())).thenReturn(List.of(detailOne, detailTwo));

        invoiceDetailService.updateInvoiceDate(invoiceEntity);

        assertThat(detailOne.getCreated()).isEqualTo(invoiceEntity.getInvoiceDate());
        assertThat(detailTwo.getCreated()).isEqualTo(invoiceEntity.getInvoiceDate());

        verify(invoiceDetailRepository).save(detailOne);
        verify(invoiceDetailRepository).save(detailTwo);

        ArgumentCaptor<InvoiceDetailEvent> eventCaptor = ArgumentCaptor.forClass(InvoiceDetailEvent.class);
        verify(applicationEventPublisher, times(2)).publishEvent(eventCaptor.capture());

        List<InvoiceDetailEvent> events = eventCaptor.getAllValues();
        assertThat(events).hasSize(2)
            .allSatisfy(event -> {
                assertThat(event.getEventType()).isEqualTo(EventType.UPDATE);
                assertThat(event.getPreviousInvoiceDetailId()).isEmpty();
            });

        assertThat(events)
                .extracting(InvoiceDetailEvent::getInvoiceDetailEntity)
                .containsExactlyInAnyOrder(detailOne, detailTwo);

        verifyNoMoreInteractions(invoiceDetailRepository, applicationEventPublisher);
        verifyNoInteractions(productRepository, workOrderRepository, invoiceRepository);
    }

    private static InvoiceDetail invoiceDetail(CommonProduct product,
                                               String workOrderNumber,
                                               double quantity,
                                               double unitPrice,
                                               double discountAmount) {
        InvoiceDetail invoiceDetail = new InvoiceDetail();
        invoiceDetail.setProduct(product);
        invoiceDetail.setWorkOrderNumber(workOrderNumber);
        invoiceDetail.setQuantity(quantity);
        invoiceDetail.setUnitPrice(unitPrice);
        invoiceDetail.setDiscountAmount(discountAmount);
        return invoiceDetail;
    }

    private static CommonProduct commonProduct(String code) {
        CommonProduct product = new CommonProduct();
        product.setCode(code);
        product.setName("Product " + code);
        product.setStorable(true);
        return product;
    }

    private static ProductEntity productEntity(String id, String code) {
        return ProductEntity.builder()
                .id(id)
                .code(code)
                .name("Product " + code)
                .storable(true)
                .tenant("tenant-1")
                .build();
    }

    private static InvoiceEntity invoiceEntity(String id, Instant invoiceDate) {
        return InvoiceEntity.builder()
                .id(id)
                .invoiceDate(invoiceDate)
                .tenant("tenant-1")
                .number("INV-001")
                .status("A")
                .type("P")
                .build();
    }

    private static InvoiceDetailEntity detailEntity(String id, String productId, String invoiceId) {
        ProductEntity productEntity = productEntity(productId, "P-" + productId);
        InvoiceEntity invoiceEntity = invoiceEntity(invoiceId, Instant.parse("2024-02-10T10:15:30Z"));
        return InvoiceDetailEntity.builder()
                .id(id)
                .productEntity(productEntity)
                .invoiceEntity(invoiceEntity)
                .quantity(1)
                .unitPrice(10)
                .discountAmount(0)
                .tenant("tenant-1")
                .build();
    }
}
