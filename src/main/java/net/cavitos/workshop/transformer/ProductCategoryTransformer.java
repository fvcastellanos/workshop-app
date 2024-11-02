package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.status.ActiveStatus;
import net.cavitos.workshop.domain.model.web.ProductCategory;
import net.cavitos.workshop.model.entity.ProductCategoryEntity;

public final class ProductCategoryTransformer     {

    private ProductCategoryTransformer() {
    }

    public static ProductCategory toWeb(final ProductCategoryEntity entity) {

        final var active = ActiveStatus.of(entity.getActive())
                .name();

        final var productCategory = new ProductCategory();
        productCategory.setCode(entity.getCode());
        productCategory.setName(entity.getName());
        productCategory.setDescription(entity.getDescription());
        productCategory.setActive(active);

        return  productCategory;
    }
}
