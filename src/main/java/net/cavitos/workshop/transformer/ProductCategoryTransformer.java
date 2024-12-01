package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.ProductCategory;
import net.cavitos.workshop.model.entity.ProductCategoryEntity;

public final class ProductCategoryTransformer     {

    private ProductCategoryTransformer() {
    }

    public static ProductCategory toWeb(final ProductCategoryEntity entity) {

        final var productCategory = new ProductCategory();
        productCategory.setCode(entity.getCode());
        productCategory.setName(entity.getName());
        productCategory.setDescription(entity.getDescription());
        productCategory.setActive(entity.getActive());

        return  productCategory;
    }
}
