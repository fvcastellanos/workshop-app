package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.model.status.ActiveStatus;
import net.cavitos.workshop.domain.model.type.ProductType;
import net.cavitos.workshop.domain.model.web.Product;
import net.cavitos.workshop.model.entity.ProductCategoryEntity;
import net.cavitos.workshop.model.entity.ProductEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.ProductCategoryRepository;
import net.cavitos.workshop.model.repository.ProductRepository;
import net.cavitos.workshop.sequence.domain.SequenceType;
import net.cavitos.workshop.sequence.provider.SequenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static net.cavitos.workshop.domain.model.status.ActiveStatus.ACTIVE;
import static net.cavitos.workshop.factory.BusinessExceptionFactory.createBusinessException;

@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    private final SequenceProvider sequenceProvider;

    public ProductService(final ProductRepository productRepository,
                          final ProductCategoryRepository productCategoryRepository,
                          final SequenceProvider sequenceProvider) {

        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.sequenceProvider = sequenceProvider;
    }

    public Page<ProductEntity> search(final String tenant,
                                      final String type,
                                      final String category,
                                      final String text,
                                      final int active,
                                      final int page,
                                      final int size) {

        LOGGER.info("Retrieve all products for tenant={} with text={}, category={}, type={},active={}", tenant, text, category, type, active);

        final var pageable = PageRequest.of(page, size);

        return productRepository.search("%" + text + "%", type, category, active, tenant, pageable);
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

        var entity = ProductEntity.builder()
                .id(TimeBasedGenerator.generateTimeBasedId())
                .type(product.getType())
                .name(product.getName())
                .code(sequenceProvider.calculateNext(SequenceType.PRODUCT))
                .description(product.getDescription())
                .minimalQuantity(product.getMinimalQuantity())
                .tenant(tenant)
                .active(ACTIVE.value())
                .created(Instant.now())
                .updated(Instant.now())
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

        var productType = product.getType();

        var code = entity.getCode();
        if (!productType.equalsIgnoreCase(entity.getType())) {

            code = calculateCode(product.getType());
        }

        entity.setActive(product.getActive());
        entity.setName(product.getName());
        entity.setCode(code);
        entity.setDescription(product.getDescription());
        entity.setType(productType);
        entity.setMinimalQuantity(product.getMinimalQuantity());
        entity.setUpdated(Instant.now());
        entity.setProductCategoryEntity(categoryEntity);

        productRepository.save(entity);

        return entity;
    }

    // ----------------------------------------------------------------------------------------------------

    private void verifyExistingCodeAndTypeForTenant(final String tenant, final Product product) {

        final var existingProductHolder = productRepository.findByCodeEqualsIgnoreCaseAndTenant(product.getCode(),
                tenant);

        if (existingProductHolder.isPresent()) {

            LOGGER.error("Product with code={}, type={} already exists for tenant={}", product.getCode(), product.getType(), tenant);
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

    private String calculateCode(final String type) {

        final var productType = ProductType.valueOf(type);

        return switch (productType) {
            case PRODUCT -> sequenceProvider.calculateNext(SequenceType.PRODUCT);
            case SERVICE -> sequenceProvider.calculateNext(SequenceType.SERVICE);
        };
    }

}
