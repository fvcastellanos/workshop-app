package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.ProductCategory;
import net.cavitos.workshop.domain.model.web.common.CommonSequence;
import net.cavitos.workshop.model.entity.ProductCategoryEntity;

import static java.util.Objects.nonNull;

public final class ProductCategoryTransformer     {

    private ProductCategoryTransformer() {
    }

    public static ProductCategory toWeb(final ProductCategoryEntity entity) {

        final var productCategory = new ProductCategory();
        productCategory.setCode(entity.getCode());
        productCategory.setName(entity.getName());
        productCategory.setDescription(entity.getDescription());
        productCategory.setActive(entity.getActive());

        final var sequenceEntity = entity.getSequenceEntity();
        if (nonNull(sequenceEntity)) {

            final var sequence = new CommonSequence();
            sequence.setId(sequenceEntity.getId());
            sequence.setPrefix(sequenceEntity.getPrefix() + " - " + sequenceEntity.getDescription());

            productCategory.setSequence(sequence);
        }

        return  productCategory;
    }
}
