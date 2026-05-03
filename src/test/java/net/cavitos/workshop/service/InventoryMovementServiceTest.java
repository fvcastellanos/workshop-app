package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.exception.BusinessException;
import net.cavitos.workshop.domain.model.web.InventoryMovement;
import net.cavitos.workshop.domain.model.web.common.CommonOperationType;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.InventoryEntity;
import net.cavitos.workshop.model.entity.InventoryMovementTypeEntity;
import net.cavitos.workshop.model.entity.ProductEntity;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.model.repository.InventoryMovementTypeRepository;
import net.cavitos.workshop.model.repository.InventoryRepository;
import net.cavitos.workshop.model.repository.ProductRepository;
import net.cavitos.workshop.model.repository.WorkOrderDetailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryMovementServiceTest {

    private static final String TENANT = "tenant-1";

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryMovementTypeRepository inventoryMovementTypeRepository;

    @Mock
    private WorkOrderDetailRepository workOrderDetailRepository;

    @Mock
    private ZonedDateTimeFactory zonedDateTimeFactory;

    @InjectMocks
    private InventoryMovementService inventoryMovementService;

    @Test
    void search_shouldReturnPagedResults() {
        var operationType = "I";
        var operationTypeCode = "MI01";
        var initialDate = Instant.parse("2024-01-01T00:00:00Z");
        var finalDate = Instant.parse("2024-12-31T23:59:59Z");
        var page = 0;
        var size = 10;
        var pageable = PageRequest.of(page, size);
        var entity = buildInventoryEntity();
        var expectedPage = new PageImpl<>(List.of(entity));

        when(inventoryRepository.search(operationType, operationTypeCode, initialDate, finalDate, TENANT, pageable))
                .thenReturn(expectedPage);

        var result = inventoryMovementService.search(
                operationType, operationTypeCode, initialDate, finalDate, TENANT, page, size);

        assertThat(result).isSameAs(expectedPage);
        assertThat(result.getContent()).hasSize(1);
        verify(inventoryRepository).search(operationType, operationTypeCode, initialDate, finalDate, TENANT, pageable);
        verifyNoMoreInteractions(inventoryRepository);
        verifyNoInteractions(productRepository, inventoryMovementTypeRepository, workOrderDetailRepository, zonedDateTimeFactory);
    }

    @Test
    void findById_whenFound_shouldReturnEntity() {
        var id = "inv-1";
        var entity = buildInventoryEntity();

        when(inventoryRepository.findByIdAndTenant(id, TENANT)).thenReturn(Optional.of(entity));

        var result = inventoryMovementService.findById(id, TENANT);

        assertThat(result).isSameAs(entity);
        verify(inventoryRepository).findByIdAndTenant(id, TENANT);
        verifyNoMoreInteractions(inventoryRepository);
        verifyNoInteractions(productRepository, inventoryMovementTypeRepository, workOrderDetailRepository, zonedDateTimeFactory);
    }

    @Test
    void findById_whenNotFound_shouldThrowBusinessException() {
        var id = "inv-1";

        when(inventoryRepository.findByIdAndTenant(id, TENANT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryMovementService.findById(id, TENANT))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    var businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(businessException.getMessage()).isEqualTo("Inventory movement not found");
                });

        verify(inventoryRepository).findByIdAndTenant(id, TENANT);
        verifyNoMoreInteractions(inventoryRepository);
        verifyNoInteractions(productRepository, inventoryMovementTypeRepository, workOrderDetailRepository, zonedDateTimeFactory);
    }

    @Test
    void add_shouldSaveAndReturnEntity() {
        var operationDate = "2024-02-10";
        var operationInstant = Instant.parse("2024-02-10T00:00:00Z");
        var commonProduct = buildCommonProduct("P-1");
        var commonOperationType = buildCommonOperationType("MI01");
        var movement = buildInventoryMovement(commonProduct, commonOperationType, operationDate, null);
        var productEntity = buildProductEntity("prod-1", "P-1");
        var operationTypeEntity = buildOperationTypeEntity("op-1", "MI01");

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-1", TENANT))
                .thenReturn(Optional.of(productEntity));
        when(inventoryMovementTypeRepository.findByCodeAndTenant("MI01", TENANT))
                .thenReturn(Optional.of(operationTypeEntity));
        when(inventoryRepository.findByProductEntityAndInvoiceDetailEntityAndInventoryMovementTypeEntityAndTenant(
                productEntity, null, operationTypeEntity, TENANT))
                .thenReturn(Optional.empty());
        when(zonedDateTimeFactory.buildInstantFrom(operationDate)).thenReturn(operationInstant);
        when(inventoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = inventoryMovementService.add(TENANT, movement);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTenant()).isEqualTo(TENANT);
        assertThat(result.getProductEntity()).isSameAs(productEntity);
        assertThat(result.getInventoryMovementTypeEntity()).isSameAs(operationTypeEntity);
        assertThat(result.getDescription()).isEqualTo("Test movement");
        assertThat(result.getQuantity()).isEqualTo(5.0);
        assertThat(result.getUnitPrice()).isEqualTo(25.0);
        assertThat(result.getDiscountAmount()).isEqualTo(2.0);
        assertThat(result.getTotal()).isEqualTo(123.0);
        assertThat(result.getOperationDate()).isEqualTo(operationInstant);
        assertThat(result.getWorkOrderDetailEntity()).isNull();
        assertThat(result.getCreated()).isNotNull();
        assertThat(result.getUpdated()).isNotNull();

        verify(productRepository).findByCodeEqualsIgnoreCaseAndTenant("P-1", TENANT);
        verify(inventoryMovementTypeRepository).findByCodeAndTenant("MI01", TENANT);
        verify(inventoryRepository).findByProductEntityAndInvoiceDetailEntityAndInventoryMovementTypeEntityAndTenant(
                productEntity, null, operationTypeEntity, TENANT);
        verify(zonedDateTimeFactory).buildInstantFrom(operationDate);
        verifyNoInteractions(workOrderDetailRepository);
        verifyNoMoreInteractions(inventoryRepository, productRepository, inventoryMovementTypeRepository, zonedDateTimeFactory);
    }

    @Test
    void add_whenProductNotFound_shouldThrowBusinessException() {
        var commonProduct = buildCommonProduct("P-1");
        var commonOperationType = buildCommonOperationType("MI01");
        var movement = buildInventoryMovement(commonProduct, commonOperationType, "2024-02-10", null);

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-1", TENANT))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryMovementService.add(TENANT, movement))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    var businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Product not found");
                });

        verify(productRepository).findByCodeEqualsIgnoreCaseAndTenant("P-1", TENANT);
        verifyNoInteractions(inventoryMovementTypeRepository, workOrderDetailRepository, zonedDateTimeFactory);
        verifyNoMoreInteractions(inventoryRepository, productRepository);
    }

    @Test
    void add_whenOperationTypeNotFound_shouldThrowBusinessException() {
        var commonProduct = buildCommonProduct("P-1");
        var commonOperationType = buildCommonOperationType("MI01");
        var movement = buildInventoryMovement(commonProduct, commonOperationType, "2024-02-10", null);
        var productEntity = buildProductEntity("prod-1", "P-1");

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-1", TENANT))
                .thenReturn(Optional.of(productEntity));
        when(inventoryMovementTypeRepository.findByCodeAndTenant("MI01", TENANT))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryMovementService.add(TENANT, movement))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    var businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Inventory Movement Type not found");
                });

        verify(productRepository).findByCodeEqualsIgnoreCaseAndTenant("P-1", TENANT);
        verify(inventoryMovementTypeRepository).findByCodeAndTenant("MI01", TENANT);
        verifyNoInteractions(workOrderDetailRepository, zonedDateTimeFactory);
        verifyNoMoreInteractions(inventoryRepository, productRepository, inventoryMovementTypeRepository);
    }

    @Test
    void add_whenMovementAlreadyExists_shouldThrowBusinessException() {
        var commonProduct = buildCommonProduct("P-1");
        var commonOperationType = buildCommonOperationType("MI01");
        var movement = buildInventoryMovement(commonProduct, commonOperationType, "2024-02-10", null);
        var productEntity = buildProductEntity("prod-1", "P-1");
        var operationTypeEntity = buildOperationTypeEntity("op-1", "MI01");
        var existing = buildInventoryEntity();

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-1", TENANT))
                .thenReturn(Optional.of(productEntity));
        when(inventoryMovementTypeRepository.findByCodeAndTenant("MI01", TENANT))
                .thenReturn(Optional.of(operationTypeEntity));
        when(inventoryRepository.findByProductEntityAndInvoiceDetailEntityAndInventoryMovementTypeEntityAndTenant(
                productEntity, null, operationTypeEntity, TENANT))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> inventoryMovementService.add(TENANT, movement))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    var businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Inventory Movement already exists");
                });

        verify(productRepository).findByCodeEqualsIgnoreCaseAndTenant("P-1", TENANT);
        verify(inventoryMovementTypeRepository).findByCodeAndTenant("MI01", TENANT);
        verify(inventoryRepository).findByProductEntityAndInvoiceDetailEntityAndInventoryMovementTypeEntityAndTenant(
                productEntity, null, operationTypeEntity, TENANT);
        verifyNoInteractions(workOrderDetailRepository, zonedDateTimeFactory);
        verifyNoMoreInteractions(inventoryRepository, productRepository, inventoryMovementTypeRepository);
    }

    @Test
    void add_whenWorkOrderDetailIdProvided_shouldLookupAndSetWorkOrderDetail() {
        var operationDate = "2024-02-10";
        var operationInstant = Instant.parse("2024-02-10T00:00:00Z");
        var workOrderDetailId = "wo-detail-1";
        var commonProduct = buildCommonProduct("P-1");
        var commonOperationType = buildCommonOperationType("MI01");
        var movement = buildInventoryMovement(commonProduct, commonOperationType, operationDate, workOrderDetailId);
        var productEntity = buildProductEntity("prod-1", "P-1");
        var operationTypeEntity = buildOperationTypeEntity("op-1", "MI01");
        var workOrderDetailEntity = WorkOrderDetailEntity.builder()
                .id(workOrderDetailId)
                .tenant(TENANT)
                .build();

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-1", TENANT))
                .thenReturn(Optional.of(productEntity));
        when(inventoryMovementTypeRepository.findByCodeAndTenant("MI01", TENANT))
                .thenReturn(Optional.of(operationTypeEntity));
        when(inventoryRepository.findByProductEntityAndInvoiceDetailEntityAndInventoryMovementTypeEntityAndTenant(
                productEntity, null, operationTypeEntity, TENANT))
                .thenReturn(Optional.empty());
        when(zonedDateTimeFactory.buildInstantFrom(operationDate)).thenReturn(operationInstant);
        when(workOrderDetailRepository.findById(workOrderDetailId))
                .thenReturn(Optional.of(workOrderDetailEntity));
        when(inventoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = inventoryMovementService.add(TENANT, movement);

        assertThat(result).isNotNull();
        assertThat(result.getWorkOrderDetailEntity()).isSameAs(workOrderDetailEntity);

        verify(workOrderDetailRepository).findById(workOrderDetailId);
        verifyNoMoreInteractions(inventoryRepository, productRepository, inventoryMovementTypeRepository,
                workOrderDetailRepository, zonedDateTimeFactory);
    }

    @Test
    void add_whenWorkOrderDetailNotFound_shouldThrowBusinessException() {
        var operationDate = "2024-02-10";
        var workOrderDetailId = "wo-detail-1";
        var commonProduct = buildCommonProduct("P-1");
        var commonOperationType = buildCommonOperationType("MI01");
        var movement = buildInventoryMovement(commonProduct, commonOperationType, operationDate, workOrderDetailId);
        var productEntity = buildProductEntity("prod-1", "P-1");
        var operationTypeEntity = buildOperationTypeEntity("op-1", "MI01");

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-1", TENANT))
                .thenReturn(Optional.of(productEntity));
        when(inventoryMovementTypeRepository.findByCodeAndTenant("MI01", TENANT))
                .thenReturn(Optional.of(operationTypeEntity));
        when(inventoryRepository.findByProductEntityAndInvoiceDetailEntityAndInventoryMovementTypeEntityAndTenant(
                productEntity, null, operationTypeEntity, TENANT))
                .thenReturn(Optional.empty());
        when(zonedDateTimeFactory.buildInstantFrom(operationDate)).thenReturn(Instant.parse("2024-02-10T00:00:00Z"));
        when(workOrderDetailRepository.findById(workOrderDetailId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryMovementService.add(TENANT, movement))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    var businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Work Order Detail not found");
                });

        verify(workOrderDetailRepository).findById(workOrderDetailId);
        verify(inventoryRepository).findByProductEntityAndInvoiceDetailEntityAndInventoryMovementTypeEntityAndTenant(
                productEntity, null, operationTypeEntity, TENANT);
        verifyNoMoreInteractions(productRepository, inventoryMovementTypeRepository,
                workOrderDetailRepository, zonedDateTimeFactory);
        verifyNoMoreInteractions(inventoryRepository);
    }

    @Test
    void update_shouldUpdateAndReturnEntity() {
        var id = "inv-1";
        var operationDate = "2024-02-15";
        var operationInstant = Instant.parse("2024-02-15T00:00:00Z");
        var commonProduct = buildCommonProduct("P-2");
        var commonOperationType = buildCommonOperationType("MI02");
        var movement = buildInventoryMovement(commonProduct, commonOperationType, operationDate, null);
        var existingEntity = buildInventoryEntity();
        var newProductEntity = buildProductEntity("prod-2", "P-2");
        var newOperationTypeEntity = buildOperationTypeEntity("op-2", "MI02");

        when(inventoryRepository.findByIdAndTenant(id, TENANT)).thenReturn(Optional.of(existingEntity));
        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-2", TENANT))
                .thenReturn(Optional.of(newProductEntity));
        when(inventoryMovementTypeRepository.findByCodeAndTenant("MI02", TENANT))
                .thenReturn(Optional.of(newOperationTypeEntity));
        when(zonedDateTimeFactory.buildInstantFrom(operationDate)).thenReturn(operationInstant);
        when(inventoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = inventoryMovementService.update(TENANT, id, movement);

        assertThat(result).isSameAs(existingEntity);
        assertThat(existingEntity.getOperationDate()).isEqualTo(operationInstant);
        assertThat(existingEntity.getDescription()).isEqualTo("Test movement");
        assertThat(existingEntity.getQuantity()).isEqualTo(5.0);
        assertThat(existingEntity.getDiscountAmount()).isEqualTo(2.0);
        assertThat(existingEntity.getUnitPrice()).isEqualTo(25.0);
        assertThat(existingEntity.getProductEntity()).isSameAs(newProductEntity);
        assertThat(existingEntity.getInventoryMovementTypeEntity()).isSameAs(newOperationTypeEntity);
        assertThat(existingEntity.getUpdated()).isNotNull();

        verify(inventoryRepository).save(existingEntity);
        verify(inventoryRepository).findByIdAndTenant(id, TENANT);
        verify(productRepository).findByCodeEqualsIgnoreCaseAndTenant("P-2", TENANT);
        verify(inventoryMovementTypeRepository).findByCodeAndTenant("MI02", TENANT);
        verify(zonedDateTimeFactory).buildInstantFrom(operationDate);
        verifyNoInteractions(workOrderDetailRepository);
        verifyNoMoreInteractions(inventoryRepository, productRepository, inventoryMovementTypeRepository, zonedDateTimeFactory);
    }

    @Test
    void update_whenEntityNotFound_shouldThrowBusinessException() {
        var id = "inv-1";
        var commonProduct = buildCommonProduct("P-1");
        var commonOperationType = buildCommonOperationType("MI01");
        var movement = buildInventoryMovement(commonProduct, commonOperationType, "2024-02-10", null);

        when(inventoryRepository.findByIdAndTenant(id, TENANT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryMovementService.update(TENANT, id, movement))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    var businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(businessException.getMessage()).isEqualTo("Inventory Movement not found");
                });

        verify(inventoryRepository).findByIdAndTenant(id, TENANT);
        verifyNoInteractions(productRepository, inventoryMovementTypeRepository, workOrderDetailRepository, zonedDateTimeFactory);
        verifyNoMoreInteractions(inventoryRepository);
    }

    @Test
    void update_whenProductNotFound_shouldThrowBusinessException() {
        var id = "inv-1";
        var commonProduct = buildCommonProduct("P-2");
        var commonOperationType = buildCommonOperationType("MI01");
        var movement = buildInventoryMovement(commonProduct, commonOperationType, "2024-02-10", null);
        var existingEntity = buildInventoryEntity();

        when(inventoryRepository.findByIdAndTenant(id, TENANT)).thenReturn(Optional.of(existingEntity));
        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-2", TENANT))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryMovementService.update(TENANT, id, movement))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    var businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Product not found");
                });

        verify(inventoryRepository).findByIdAndTenant(id, TENANT);
        verify(productRepository).findByCodeEqualsIgnoreCaseAndTenant("P-2", TENANT);
        verifyNoInteractions(inventoryMovementTypeRepository, workOrderDetailRepository, zonedDateTimeFactory);
        verifyNoMoreInteractions(inventoryRepository, productRepository);
    }

    @Test
    void update_whenOperationTypeNotFound_shouldThrowBusinessException() {
        var id = "inv-1";
        var commonProduct = buildCommonProduct("P-1");
        var commonOperationType = buildCommonOperationType("MI02");
        var movement = buildInventoryMovement(commonProduct, commonOperationType, "2024-02-10", null);
        var existingEntity = buildInventoryEntity();
        var productEntity = buildProductEntity("prod-1", "P-1");

        when(inventoryRepository.findByIdAndTenant(id, TENANT)).thenReturn(Optional.of(existingEntity));
        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant("P-1", TENANT))
                .thenReturn(Optional.of(productEntity));
        when(inventoryMovementTypeRepository.findByCodeAndTenant("MI02", TENANT))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryMovementService.update(TENANT, id, movement))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    var businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Inventory Movement Type not found");
                });

        verify(inventoryRepository).findByIdAndTenant(id, TENANT);
        verify(productRepository).findByCodeEqualsIgnoreCaseAndTenant("P-1", TENANT);
        verify(inventoryMovementTypeRepository).findByCodeAndTenant("MI02", TENANT);
        verifyNoInteractions(workOrderDetailRepository, zonedDateTimeFactory);
        verifyNoMoreInteractions(inventoryRepository, productRepository, inventoryMovementTypeRepository);
    }

    @Test
    void delete_whenEntityFound_shouldDelete() {
        var id = "inv-1";
        var entity = buildInventoryEntity();

        when(inventoryRepository.findByIdAndTenant(id, TENANT)).thenReturn(Optional.of(entity));

        inventoryMovementService.delete(id, TENANT);

        verify(inventoryRepository).findByIdAndTenant(id, TENANT);
        verify(inventoryRepository).delete(entity);
        verifyNoMoreInteractions(inventoryRepository);
        verifyNoInteractions(productRepository, inventoryMovementTypeRepository, workOrderDetailRepository, zonedDateTimeFactory);
    }

    @Test
    void delete_whenEntityNotFound_shouldNotDelete() {
        var id = "inv-1";

        when(inventoryRepository.findByIdAndTenant(id, TENANT)).thenReturn(Optional.empty());

        inventoryMovementService.delete(id, TENANT);

        verify(inventoryRepository).findByIdAndTenant(id, TENANT);
        verifyNoMoreInteractions(inventoryRepository);
        verifyNoInteractions(productRepository, inventoryMovementTypeRepository, workOrderDetailRepository, zonedDateTimeFactory);
    }

    @Test
    void findLatestUnitPrice_whenProductFoundAndMovementExists_shouldReturnUnitPrice() {
        var productCode = "P-1";
        var productEntity = buildProductEntity("prod-1", productCode);
        var inventoryEntity = buildInventoryEntity();
        var page = new PageImpl<>(List.of(inventoryEntity));

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant(productCode, TENANT))
                .thenReturn(Optional.of(productEntity));
        when(inventoryRepository.findLatestUnitPriceByProductIdAndTenant("prod-1", TENANT, PageRequest.of(0, 1)))
                .thenReturn(page);

        var result = inventoryMovementService.findLatestUnitPrice(productCode, TENANT);

        assertThat(result).isEqualTo(25.0);
        verify(productRepository).findByCodeEqualsIgnoreCaseAndTenant(productCode, TENANT);
        verify(inventoryRepository).findLatestUnitPriceByProductIdAndTenant("prod-1", TENANT, PageRequest.of(0, 1));
        verifyNoMoreInteractions(productRepository, inventoryRepository);
        verifyNoInteractions(inventoryMovementTypeRepository, workOrderDetailRepository, zonedDateTimeFactory);
    }

    @Test
    void findLatestUnitPrice_whenNoMovements_shouldReturnZero() {
        var productCode = "P-1";
        var productEntity = buildProductEntity("prod-1", productCode);
        var emptyPage = Page.<InventoryEntity>empty();

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant(productCode, TENANT))
                .thenReturn(Optional.of(productEntity));
        when(inventoryRepository.findLatestUnitPriceByProductIdAndTenant("prod-1", TENANT, PageRequest.of(0, 1)))
                .thenReturn(emptyPage);

        var result = inventoryMovementService.findLatestUnitPrice(productCode, TENANT);

        assertThat(result).isEqualTo(0.0);
        verify(productRepository).findByCodeEqualsIgnoreCaseAndTenant(productCode, TENANT);
        verify(inventoryRepository).findLatestUnitPriceByProductIdAndTenant("prod-1", TENANT, PageRequest.of(0, 1));
        verifyNoMoreInteractions(productRepository, inventoryRepository);
        verifyNoInteractions(inventoryMovementTypeRepository, workOrderDetailRepository, zonedDateTimeFactory);
    }

    @Test
    void findLatestUnitPrice_whenProductNotFound_shouldThrowBusinessException() {
        var productCode = "P-1";

        when(productRepository.findByCodeEqualsIgnoreCaseAndTenant(productCode, TENANT))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryMovementService.findLatestUnitPrice(productCode, TENANT))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    var businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Product not found");
                });

        verify(productRepository).findByCodeEqualsIgnoreCaseAndTenant(productCode, TENANT);
        verifyNoInteractions(inventoryRepository, inventoryMovementTypeRepository, workOrderDetailRepository, zonedDateTimeFactory);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void findByWorkOrderDetailId_whenFound_shouldReturnEntity() {
        var workOrderDetailId = "wo-detail-1";
        var entity = buildInventoryEntity();

        when(inventoryRepository.findByWorkOrderDetailEntityIdAndTenant(workOrderDetailId, TENANT))
                .thenReturn(Optional.of(entity));

        var result = inventoryMovementService.findByWorkOrderDetailId(workOrderDetailId, TENANT);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(entity);
        verify(inventoryRepository).findByWorkOrderDetailEntityIdAndTenant(workOrderDetailId, TENANT);
        verifyNoMoreInteractions(inventoryRepository);
        verifyNoInteractions(productRepository, inventoryMovementTypeRepository, workOrderDetailRepository, zonedDateTimeFactory);
    }

    @Test
    void findByWorkOrderDetailId_whenNotFound_shouldReturnEmpty() {
        var workOrderDetailId = "wo-detail-1";

        when(inventoryRepository.findByWorkOrderDetailEntityIdAndTenant(workOrderDetailId, TENANT))
                .thenReturn(Optional.empty());

        var result = inventoryMovementService.findByWorkOrderDetailId(workOrderDetailId, TENANT);

        assertThat(result).isEmpty();
        verify(inventoryRepository).findByWorkOrderDetailEntityIdAndTenant(workOrderDetailId, TENANT);
        verifyNoMoreInteractions(inventoryRepository);
        verifyNoInteractions(productRepository, inventoryMovementTypeRepository, workOrderDetailRepository, zonedDateTimeFactory);
    }

    // --------------------------------------------------------------------------------------------------------
    // Helper methods
    // --------------------------------------------------------------------------------------------------------

    private static CommonProduct buildCommonProduct(String code) {
        var product = new CommonProduct();
        product.setCode(code);
        product.setName("Product " + code);
        product.setStorable(true);
        return product;
    }

    private static CommonOperationType buildCommonOperationType(String code) {
        var operationType = new CommonOperationType();
        operationType.setCode(code);
        operationType.setName("Operation " + code);
        return operationType;
    }

    private static InventoryMovement buildInventoryMovement(CommonProduct product,
                                                            CommonOperationType operationType,
                                                            String operationDate,
                                                            String workOrderDetailId) {
        var movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setOperationType(operationType);
        movement.setOperationDate(operationDate);
        movement.setQuantity(5.0);
        movement.setUnitPrice(25.0);
        movement.setDiscountAmount(2.0);
        movement.setDescription("Test movement");
        movement.setWorkOrderDetailId(workOrderDetailId);
        return movement;
    }

    private static ProductEntity buildProductEntity(String id, String code) {
        return ProductEntity.builder()
                .id(id)
                .code(code)
                .name("Product " + code)
                .storable(true)
                .tenant(TENANT)
                .build();
    }

    private static InventoryMovementTypeEntity buildOperationTypeEntity(String id, String code) {
        return InventoryMovementTypeEntity.builder()
                .id(id)
                .code(code)
                .name("Operation " + code)
                .type("I")
                .active(1)
                .tenant(TENANT)
                .build();
    }

    private static InventoryEntity buildInventoryEntity() {
        return InventoryEntity.builder()
                .id("inv-1")
                .tenant(TENANT)
                .quantity(5.0)
                .unitPrice(25.0)
                .discountAmount(2.0)
                .total(123.0)
                .description("Test movement")
                .operationDate(Instant.parse("2024-02-10T00:00:00Z"))
                .created(Instant.parse("2024-02-10T10:00:00Z"))
                .updated(Instant.parse("2024-02-10T10:00:00Z"))
                .build();
    }
}
