package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.model.web.ProductCategory;
import net.cavitos.workshop.domain.model.web.common.CommonSequence;
import net.cavitos.workshop.model.entity.ProductCategoryEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.ProductCategoryRepository;
import net.cavitos.workshop.sequence.domain.SequenceType;
import net.cavitos.workshop.sequence.model.entity.SequenceEntity;
import net.cavitos.workshop.sequence.provider.SequenceGenerator;
import net.cavitos.workshop.sequence.service.SequenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

import static net.cavitos.workshop.factory.BusinessExceptionFactory.createBusinessException;

@Service
public class ProductCategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductCategoryService.class);

    private final ProductCategoryRepository productCategoryRepository;
    private final SequenceService sequenceService;
    private final SequenceGenerator sequenceGenerator;
    private final Clock systemClock;

    public ProductCategoryService(final ProductCategoryRepository productCategoryRepository,
                                  final SequenceService sequenceService,
                                  final SequenceGenerator sequenceGenerator,
                                  final Clock systemClock) {

        this.productCategoryRepository = productCategoryRepository;
        this.sequenceService = sequenceService;
        this.sequenceGenerator = sequenceGenerator;
        this.systemClock = systemClock;
    }

    public Page<ProductCategoryEntity> search(final String tenant,
                                              final int active,
                                              final String text,
                                              final int page,
                                              final int size) {

        LOGGER.info("Search product categories for text: {} and tenant: {}", text, tenant);

        final var pageable = PageRequest.of(page, size);

        return productCategoryRepository.search("%" + text + "%", active, tenant, pageable);
    }

    public List<ProductCategoryEntity> getProductCategories(final String tenant, final int active) {

        LOGGER.info("Retrieve product categories for tenant: {} and active: {}", tenant, active);

        return productCategoryRepository.findByTenantAndActive(tenant, active);
    }

    public ProductCategoryEntity findById(final String tenant, final String id) {

        LOGGER.info("Retrieve product category with id: {} for tenant: {}", id, tenant);

        return getById(tenant, id);
    }

    public ProductCategoryEntity add(final String tenant, final ProductCategory productCategory) {

        LOGGER.info("Add a new product category for tenant: {}", tenant);

        verifyProductCategoryName(tenant, productCategory.getName());

        final var sequenceEntity = findSequence(productCategory.getSequence(), tenant);
        final var code = sequenceGenerator.nextValue(SequenceType.PRODUCT_CATEGORY, tenant);

        var entity = ProductCategoryEntity.builder()
                .id(TimeBasedGenerator.generateTimeBasedId())
                .code(code)
                .name(productCategory.getName())
                .description(productCategory.getDescription())
                .sequenceEntity(sequenceEntity)
                .created(systemClock.instant())
                .tenant(tenant)
                .active(1)
                .build();

        return productCategoryRepository.save(entity);
    }

    public ProductCategoryEntity update(final String tenant, final String id, final ProductCategory productCategory) {

        final var entity = getById(tenant, id);

        if (!entity.getName().equalsIgnoreCase(productCategory.getName())) {

            verifyProductCategoryName(tenant, productCategory.getName());
        }

        final var sequenceEntity = findSequence(productCategory.getSequence(), tenant);

        entity.setName(productCategory.getName());
        entity.setDescription(productCategory.getDescription());
        entity.setUpdated(systemClock.instant());
        entity.setActive(productCategory.getActive());
        entity.setSequenceEntity(sequenceEntity);

        return productCategoryRepository.save(entity);
    }

    // --------------------------------------------------------------------------------

    private void verifyProductCategoryName(final String tenant, final String name) {

        productCategoryRepository.findByTenantAndName(tenant, name)
                .ifPresent(existingEntity -> {

                    LOGGER.error("Product Category with name: {} already exists for tenant: {}",
                            name, tenant);

                    throw createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY,
                                "Product Category with name: %s already exists", name);
                });
    }

    private ProductCategoryEntity getById(final String tenant, final String id) {

        var entity = productCategoryRepository.findById(id)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Product Category not found"));

        if (!entity.getTenant().equalsIgnoreCase(tenant)) {

            LOGGER.error("Product Category with id: {} is not associated to tenant: {}", id, tenant);
            throw createBusinessException(HttpStatus.NOT_FOUND, "Product Category not found");
        }

        return entity;
    }

    private SequenceEntity findSequence(final CommonSequence sequence, final String tenant) {

        return sequenceService.getById(sequence.getId(), tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Sequence not found"));
    }
}
