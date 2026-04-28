package net.cavitos.workshop.event.processor;

import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import net.cavitos.workshop.model.entity.ProductEntity;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.model.entity.WorkOrderEntity;
import net.cavitos.workshop.model.repository.WorkOrderDetailRepository;
import net.cavitos.workshop.service.PriceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkOrderInvoiceDetailProcessorTest {

    @Mock
    private WorkOrderDetailRepository workOrderDetailRepository;

    @Mock
    private ZonedDateTimeFactory zonedDateTimeFactory;

    @Mock
    private PriceService priceService;

    @InjectMocks
    private WorkOrderInvoiceDetailProcessor processor;

    @Test
    void deleteWorkOrderDetailFor_whenEntityFound_shouldDelete() {
        String tenant = "tenant-1";
        WorkOrderDetailEntity existing = WorkOrderDetailEntity.builder()
                .id("detail-1")
                .tenant(tenant)
                .build();

        when(workOrderDetailRepository.findByInvoiceDetailIdAndTenant("prev-1", tenant))
                .thenReturn(Optional.of(existing));

        processor.deleteWorkOrderDetailFor("prev-1", invoiceDetailWithTenant(tenant));

        verify(workOrderDetailRepository).delete(existing);
        verifyNoMoreInteractions(workOrderDetailRepository);
        verifyNoInteractions(zonedDateTimeFactory, priceService);
    }

    @Test
    void deleteWorkOrderDetailFor_whenEntityMissing_shouldNotDelete() {
        String tenant = "tenant-1";

        when(workOrderDetailRepository.findByInvoiceDetailIdAndTenant("prev-1", tenant))
                .thenReturn(Optional.empty());

        processor.deleteWorkOrderDetailFor("prev-1", invoiceDetailWithTenant(tenant));

        verifyNoMoreInteractions(workOrderDetailRepository);
        verifyNoInteractions(zonedDateTimeFactory, priceService);
    }

    @Test
    void addWorkOrderDetailFor_whenWorkOrderPresent_shouldSaveNewDetail() {
        String tenant = "tenant-1";
        Instant created = Instant.parse("2024-02-10T10:15:30Z");
        WorkOrderEntity workOrderEntity = WorkOrderEntity.builder()
                .id("wo-1")
                .number("WO-1001")
                .tenant(tenant)
                .build();
        ProductEntity productEntity = ProductEntity.builder()
                .id("prod-1")
                .code("P-1")
                .name("Product P-1")
                .tenant(tenant)
                .build();
        InvoiceDetailEntity invoiceDetailEntity = InvoiceDetailEntity.builder()
                .id("inv-detail-1")
                .workOrderEntity(workOrderEntity)
                .productEntity(productEntity)
                .quantity(2)
                .unitPrice(15.5)
                .tenant(tenant)
                .build();

        when(priceService.calculatePrice(31.0, tenant)).thenReturn(40.0);
        when(zonedDateTimeFactory.getSystemNow()).thenReturn(created);

        processor.addWorkOrderDetailFor(invoiceDetailEntity);

        ArgumentCaptor<WorkOrderDetailEntity> captor = ArgumentCaptor.forClass(WorkOrderDetailEntity.class);
        verify(workOrderDetailRepository).save(captor.capture());
        WorkOrderDetailEntity saved = captor.getValue();

        assertThat(saved.getInvoiceDetailEntity()).isSameAs(invoiceDetailEntity);
        assertThat(saved.getProductEntity()).isSameAs(productEntity);
        assertThat(saved.getWorkOrderEntity()).isSameAs(workOrderEntity);
        assertThat(saved.getQuantity()).isEqualTo(2);
        assertThat(saved.getUnitPrice()).isEqualTo(15.5);
        assertThat(saved.getSalePrice()).isEqualTo(40.0);
        assertThat(saved.getTenant()).isEqualTo(tenant);
        assertThat(saved.getCreated()).isEqualTo(created);

        verify(priceService).calculatePrice(31.0, tenant);
        verify(zonedDateTimeFactory).getSystemNow();
        verifyNoMoreInteractions(workOrderDetailRepository, priceService, zonedDateTimeFactory);
    }

    @Test
    void addWorkOrderDetailFor_whenNoWorkOrder_shouldNotSaveOrCalculate() {
        String tenant = "tenant-1";
        InvoiceDetailEntity invoiceDetailEntity = InvoiceDetailEntity.builder()
                .id("inv-detail-2")
                .quantity(1)
                .unitPrice(10.0)
                .tenant(tenant)
                .build();

        processor.addWorkOrderDetailFor(invoiceDetailEntity);

        verifyNoInteractions(workOrderDetailRepository, priceService, zonedDateTimeFactory);
    }

    private static InvoiceDetailEntity invoiceDetailWithTenant(String tenant) {
        return InvoiceDetailEntity.builder()
                .id("inv-detail-1")
                .tenant(tenant)
                .build();
    }
}

