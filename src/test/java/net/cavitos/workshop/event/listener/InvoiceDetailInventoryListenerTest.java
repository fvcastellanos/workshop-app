package net.cavitos.workshop.event.listener;

import net.cavitos.workshop.event.model.EventType;
import net.cavitos.workshop.event.model.InvoiceDetailEvent;
import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.InventoryEntity;
import net.cavitos.workshop.model.entity.InventoryMovementTypeEntity;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import net.cavitos.workshop.model.entity.InvoiceEntity;
import net.cavitos.workshop.model.entity.ProductEntity;
import net.cavitos.workshop.model.repository.InventoryMovementTypeRepository;
import net.cavitos.workshop.model.repository.InventoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceDetailInventoryListenerTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryMovementTypeRepository inventoryMovementTypeRepository;
    @Mock
    private ZonedDateTimeFactory zonedDateTimeFactory;

    private InvoiceDetailInventoryListener listener;

    private final Instant now = Instant.parse("2024-04-05T12:00:00Z");

    @BeforeEach
    void setupMocks() {
        createListener("IN_TYPE", "OUT_TYPE");
    }

    @Test
    void handleEvent_addEvent_shouldAddInventoryMovement() {

        // Arrange
        final var product = ProductEntity.builder()
                .id("prod-1")
                .storable(true)
                .build();

        final var invoice = InvoiceEntity.builder()
                .invoiceDate(now)
                .build();

        final var detail = InvoiceDetailEntity.builder()
                    .id("detail-1")
                    .productEntity(product)
                    .invoiceEntity(invoice)
                    .quantity(2)
                    .unitPrice(10.0)
                    .discountAmount(0.0)
                    .tenant("tenant-1")
                    .build();

        final var movementType = InventoryMovementTypeEntity.builder()
                .id("mt-1")
                .build();

        when(inventoryRepository.findInventoryMovementsFor(eq("prod-1"), eq("detail-1"), eq("tenant-1")))
                .thenReturn(Collections.emptyList());

        when(inventoryMovementTypeRepository.findByCodeAndTenant(eq("IN_TYPE"), eq("tenant-1")))
                .thenReturn(Optional.of(movementType));

        when(zonedDateTimeFactory.getSystemNow())
                .thenReturn(now);

        final var event = InvoiceDetailEvent.builder()
                .eventType(EventType.ADD)
                .invoiceDetailEntity(detail)
                .build();

        // Act
        listener.handleEvent(event);

        // Assert
        ArgumentCaptor<InventoryEntity> captor = ArgumentCaptor.forClass(InventoryEntity.class);
        verify(inventoryRepository).save(captor.capture());

        final var saved = captor.getValue();

        Assertions.assertThat(saved)
            .hasFieldOrPropertyWithValue("productEntity", product)
            .hasFieldOrPropertyWithValue("invoiceDetailEntity", detail)
            .hasFieldOrPropertyWithValue("quantity", 2.0)
            .hasFieldOrPropertyWithValue("unitPrice", 10.0)
            .hasFieldOrPropertyWithValue("total", 20.0)
            .hasFieldOrPropertyWithValue("tenant", "tenant-1")
            .hasFieldOrPropertyWithValue("created", now)
            .hasFieldOrPropertyWithValue("updated", now);
    }

    @Test
    void handleEvent_addEvent_shouldNotAddIfNotStorable() {

        // Arrange
        ProductEntity product = ProductEntity.builder().id("prod-2").storable(false).build();
        InvoiceEntity invoice = InvoiceEntity.builder().invoiceDate(now).build();
        InvoiceDetailEntity detail = InvoiceDetailEntity.builder()
                .id("detail-2").productEntity(product).invoiceEntity(invoice)
                .quantity(1).unitPrice(5.0).discountAmount(0.0).tenant("tenant-2").build();
        InvoiceDetailEvent event = InvoiceDetailEvent.builder()
                .eventType(EventType.ADD)
                .invoiceDetailEntity(detail)
                .build();

        // Act
        listener.handleEvent(event);

        // Assert
        verifyNoInteractions(inventoryRepository);
    }

    @Test
    void handleEvent_deleteEvent_shouldDeleteInventoryMovements() {
        createListener("IN_TYPE", "OUT_TYPE");
        // Arrange
        ProductEntity product = ProductEntity.builder().id("prod-3").storable(true).build();
        InvoiceEntity invoice = InvoiceEntity.builder().invoiceDate(now).build();
        InvoiceDetailEntity detail = InvoiceDetailEntity.builder()
                .id("detail-3").productEntity(product).invoiceEntity(invoice)
                .quantity(1).unitPrice(5.0).discountAmount(0.0).tenant("tenant-3").build();
        InventoryEntity movement = InventoryEntity.builder().id("inv-1").build();
        when(inventoryRepository.findInventoryMovementsFor(eq("prod-3"), eq("detail-3"), eq("tenant-3")))
                .thenReturn(Collections.singletonList(movement));
        InvoiceDetailEvent event = InvoiceDetailEvent.builder()
                .eventType(EventType.DELETE)
                .invoiceDetailEntity(detail)
                .build();

        // Act
        listener.handleEvent(event);

        // Assert
        verify(inventoryRepository).deleteAll(Collections.singletonList(movement));
    }

    @Test
    void handleEvent_updateEvent_shouldDeleteAndAdd() {
        createListener("IN_TYPE", "OUT_TYPE");
        // Arrange
        ProductEntity product = ProductEntity.builder().id("prod-4").storable(true).build();
        InvoiceEntity invoice = InvoiceEntity.builder().invoiceDate(now).build();
        InvoiceDetailEntity detail = InvoiceDetailEntity.builder()
                .id("detail-4").productEntity(product).invoiceEntity(invoice)
                .quantity(3).unitPrice(7.0).discountAmount(1.0).tenant("tenant-4").build();
        InventoryMovementTypeEntity movementType = InventoryMovementTypeEntity.builder().id("mt-2").build();
        when(inventoryRepository.findInventoryMovementsFor(eq("prod-4"), eq("detail-4"), eq("tenant-4")))
                .thenReturn(Collections.emptyList());
        when(inventoryMovementTypeRepository.findByCodeAndTenant(eq("IN_TYPE"), eq("tenant-4")))
                .thenReturn(Optional.of(movementType));
        InvoiceDetailEvent event = InvoiceDetailEvent.builder()
                .eventType(EventType.UPDATE)
                .invoiceDetailEntity(detail)
                .build();

        // Act
        listener.handleEvent(event);

        // Assert
        verify(inventoryRepository, atLeastOnce()).save(any());
        verify(inventoryRepository, atLeastOnce()).findInventoryMovementsFor(any(), any(), any());
    }

    private void createListener(String inType, String outType) {
        listener = new InvoiceDetailInventoryListener(
                inType, outType, inventoryRepository, inventoryMovementTypeRepository, zonedDateTimeFactory);
    }
}
