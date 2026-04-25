package net.cavitos.workshop.event.processor;

import net.cavitos.workshop.domain.model.web.InventoryMovement;
import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.InventoryEntity;
import net.cavitos.workshop.model.entity.ProductEntity;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.service.ConfigurationService;
import net.cavitos.workshop.service.InventoryMovementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkOrderDetailInventoryProcessorTest {

    private static final String TENANT = "tenant-a";

    @Mock
    private InventoryMovementService inventoryMovementService;

    @Mock
    private ZonedDateTimeFactory zonedDateTimeFactory;

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private WorkOrderDetailInventoryProcessor processor;

    @Test
    void deleteInventoryForWhenMovementExistsDeletesByIdAndTenant() {

        WorkOrderDetailEntity detailEntity = buildWorkOrderDetailEntity();
        InventoryEntity inventoryEntity = InventoryEntity.builder()
                .id("inventory-1")
                .tenant(TENANT)
                .build();

        when(inventoryMovementService.findByWorkOrderDetailId(detailEntity.getId(), TENANT))
                .thenReturn(Optional.of(inventoryEntity));

        assertThatCode(() -> processor.deleteInventoryFor(detailEntity)).doesNotThrowAnyException();

        verify(inventoryMovementService).delete("inventory-1", TENANT);
    }

    @Test
    void deleteInventoryForWhenMissingDoesNotDelete() {

        WorkOrderDetailEntity detailEntity = buildWorkOrderDetailEntity();

        when(inventoryMovementService.findByWorkOrderDetailId(detailEntity.getId(), TENANT))
                .thenReturn(Optional.empty());

        assertThatCode(() -> processor.deleteInventoryFor(detailEntity)).doesNotThrowAnyException();

        verify(inventoryMovementService, never()).delete("inventory-1", TENANT);
    }

    @Test
    void addInventoryForWhenProductPresentCreatesMovementAndAdds() {

        WorkOrderDetailEntity detailEntity = buildWorkOrderDetailEntity();
        Properties properties = new Properties();
        properties.setProperty("sale.operation-type", "MI77");

        when(configurationService.getConfiguration(TENANT)).thenReturn(properties);
        when(zonedDateTimeFactory.buildStringFromInstant(detailEntity.getOperationDate()))
                .thenReturn("2026-04-23");

        assertThatCode(() -> processor.addInventoryFor(detailEntity)).doesNotThrowAnyException();

        ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
        verify(inventoryMovementService).add(eq(TENANT), movementCaptor.capture());

        InventoryMovement movement = movementCaptor.getValue();
        assertThat(movement.getOperationDate()).isEqualTo("2026-04-23");
        assertThat(movement.getQuantity()).isEqualTo(2.0);
        assertThat(movement.getUnitPrice()).isEqualTo(25.0);
        assertThat(movement.getProduct().getCode()).isEqualTo("PROD-1");
        assertThat(movement.getProduct().getName()).isEqualTo("Product One");
        assertThat(movement.getProduct().isStorable()).isTrue();
        assertThat(movement.getOperationType().getCode()).isEqualTo("MI77");
        assertThat(movement.getWorkOrderDetailId()).isEqualTo("detail-1");
    }

    @Test
    void addInventoryForWhenProductMissingSkipsAdd() {

        WorkOrderDetailEntity detailEntity = WorkOrderDetailEntity.builder()
                .id("detail-1")
                .tenant(TENANT)
                .operationDate(Instant.parse("2026-04-23T10:15:30Z"))
                .quantity(2.0)
                .unitPrice(25.0)
                .build();

        assertThatCode(() -> processor.addInventoryFor(detailEntity)).doesNotThrowAnyException();

        verify(inventoryMovementService, never()).add(eq(TENANT), any(InventoryMovement.class));
    }

    private WorkOrderDetailEntity buildWorkOrderDetailEntity() {

        ProductEntity productEntity = ProductEntity.builder()
                .id("product-1")
                .code("PROD-1")
                .name("Product One")
                .storable(true)
                .tenant(TENANT)
                .build();

        return WorkOrderDetailEntity.builder()
                .id("detail-1")
                .tenant(TENANT)
                .operationDate(Instant.parse("2026-04-23T10:15:30Z"))
                .quantity(2.0)
                .unitPrice(25.0)
                .productEntity(productEntity)
                .build();
    }
}
