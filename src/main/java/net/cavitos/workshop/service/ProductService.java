package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.model.type.ProductType;
import net.cavitos.workshop.domain.model.web.Product;
import net.cavitos.workshop.model.entity.ProductCategoryEntity;
import net.cavitos.workshop.model.entity.ProductEntity;
import net.cavitos.workshop.model.entity.ProductStockEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.ProductCategoryRepository;
import net.cavitos.workshop.model.repository.ProductRepository;
import net.cavitos.workshop.model.repository.ProductStockRepository;
import net.cavitos.workshop.sequence.domain.SequenceType;
import net.cavitos.workshop.sequence.provider.SequenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static net.cavitos.workshop.domain.model.status.ActiveStatus.ACTIVE;
import static net.cavitos.workshop.factory.BusinessExceptionFactory.createBusinessException;

@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    private final SequenceGenerator sequenceGenerator;

    private final ProductStockRepository productStockRepository;

    private final Clock systemClock;

    public ProductService(final ProductRepository productRepository,
                          final ProductCategoryRepository productCategoryRepository,
                          final ProductStockRepository productStockRepository,
                          final SequenceGenerator sequenceGenerator,
                          final Clock systemClock) {

        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.sequenceGenerator = sequenceGenerator;
        this.productStockRepository = productStockRepository;
        this.systemClock = systemClock;
    }

    public Page<ProductEntity> search(final String tenant,
                                      final String storable,
                                      final String category,
                                      final String text,
                                      final int active,
                                      final int page,
                                      final int size) {

        LOGGER.info("Retrieve all products for tenant={} with text={}, category={}, storable={},active={}",
                tenant, text, category, storable, active);

        final var pageable = PageRequest.of(page, size);

        final var evaluate = !storable.equalsIgnoreCase("%");
        var isStorable = evaluate && storable.equalsIgnoreCase("Y");

        return productRepository.search("%" + text + "%", evaluate, isStorable, category, active, tenant, pageable);
    }

    public ProductEntity findById(final String tenant, final String id) {

        LOGGER.info("Get product_id={} for tenant={}", id, tenant);

        final var entity = productRepository.findById(id)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Product not found"));

        if (!entity.getTenant().equalsIgnoreCase(tenant)) {

            LOGGER.error("product_id={} is not associated with tenant={}", id, tenant);
            throw createBusinessException(HttpStatus.NOT_FOUND, "Product not found");
        }

        return entity;
    }

    public ProductEntity add(final String tenant, final Product product) {

        LOGGER.info("Add a new product with name={} for tenant={}", product.getName(), tenant);

        verifyExistingCodeAndTypeForTenant(tenant, product);
        final var categoryEntity = findProductCategory(tenant, product.getCategory().getId());
        final var code = calculateCode(categoryEntity, tenant);

        var entity = ProductEntity.builder()
                .id(TimeBasedGenerator.generateTimeBasedId())
                .storable(product.isStorable())
                .name(product.getName())
                .code(code)
                .description(product.getDescription())
                .minimalQuantity(product.getMinimalQuantity())
                .tenant(tenant)
                .active(ACTIVE.value())
                .created(systemClock.instant())
                .productCategoryEntity(categoryEntity)
                .build();

        productRepository.save(entity);

        return entity;
    }

    public ProductEntity update(final String tenant, final String id, final Product product) {

        LOGGER.info("Trying to update product_id={} for tenant={}", id, tenant);

        final var entity = productRepository.findById(id)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Product not found"));

        if (!entity.getTenant().equalsIgnoreCase(tenant)) {

            LOGGER.error("product_id={} is not associated with tenant={}", id, tenant);
            throw createBusinessException(HttpStatus.NOT_FOUND, "Product not found");
        }

        final var category = product.getCategory();
        final var categoryEntity = findProductCategory(tenant, category.getId());

        final var currentCategoryEntity = entity.getProductCategoryEntity();
        var code = !currentCategoryEntity.getId().equalsIgnoreCase(category.getId()) ?
                calculateCode(categoryEntity, tenant)
                : entity.getCode();

        entity.setActive(product.getActive());
        entity.setName(product.getName());
        entity.setCode(code);
        entity.setDescription(product.getDescription());
        entity.setStorable(product.isStorable());
        entity.setMinimalQuantity(product.getMinimalQuantity());
        entity.setUpdated(systemClock.instant());
        entity.setProductCategoryEntity(categoryEntity);

        return productRepository.save(entity);
    }

    public List<ProductEntity> loadProducts(final String tenant) {

        try {
            LOGGER.info("Retrieve all products for tenant={}", tenant);
            return productRepository.findByTenantAndActive(tenant, 1);
        } catch (final Exception exception) {

            LOGGER.error("Error al cargar los productos", exception);
            return Collections.emptyList();
        }
    }

    public Page<ProductStockEntity> getProductStock(final String text,
                                                    final String category,
                                                    final String tenant,
                                                    final int page,
                                                    final int size) {

        LOGGER.info("Retrieve product stock for tenant={} with text={}, category={}", tenant, text, category);

        final var pageable = PageRequest.of(page, size);

        return productStockRepository.getProductStock("%" + text + "%", category, tenant, pageable);
    }

    // ----------------------------------------------------------------------------------------------------

    private void verifyExistingCodeAndTypeForTenant(final String tenant, final Product product) {

        final var existingProductHolder = productRepository.findByCodeEqualsIgnoreCaseAndTenant(product.getCode(),
                tenant);

        if (existingProductHolder.isPresent()) {

            LOGGER.error("Product with code={}, storable={} already exists for tenant={}", product.getCode(), product.isStorable(), tenant);
            throw createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Product already exists");
        }
    }

    private ProductCategoryEntity findProductCategory(final String tenant, final String productCategoryId) {

        final var categoryEntity =  productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Product Category not found"));

        if (!categoryEntity.getTenant().equalsIgnoreCase(tenant)) {

            throw createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Product Category not found");
        }

        return categoryEntity;
    }

    private String calculateCode(final String type, final String tenant) {

        final var productType = ProductType.valueOf(type);

        return switch (productType) {
            case PRODUCT -> sequenceGenerator.nextValue(SequenceType.PRODUCT, tenant);
            case SERVICE -> sequenceGenerator.nextValue(SequenceType.SERVICE, tenant);
        };
    }

    private String calculateCode(final ProductCategoryEntity categoryEntity, final String tenant) {

        final var sequenceEntity = categoryEntity.getSequenceEntity();

        if (sequenceEntity != null) {
            return sequenceGenerator.nextValue(sequenceEntity.getPrefix(), tenant);
        }

        return sequenceGenerator.nextValue(SequenceType.UNKNOWN, tenant);
    }
}
