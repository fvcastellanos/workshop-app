package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.Product;
import net.cavitos.workshop.domain.model.web.common.CommonProductCategory;
import net.cavitos.workshop.model.entity.ProductCategoryEntity;
import net.cavitos.workshop.model.entity.ProductEntity;

import java.util.Objects;

public final class ProductTransformer {

    private ProductTransformer() {
    }

    public static Product toWeb(final ProductEntity productEntity) {

        return getProduct(productEntity);
    }

    private static Product getProduct(ProductEntity productEntity) {

        final var product = new Product();
        product.setCode(productEntity.getCode());
        product.setName(productEntity.getName());
        product.setDescription(productEntity.getDescription());
        product.setActive(productEntity.getActive());
        product.setMinimalQuantity(productEntity.getMinimalQuantity());
        product.setStorable(productEntity.isStorable());

        final var productCategoryEntity = productEntity.getProductCategoryEntity();
        if (Objects.nonNull(productCategoryEntity)) {

            product.setCategory(buildCategory(productCategoryEntity));
        }

        return product;
    }

    private static CommonProductCategory buildCategory(ProductCategoryEntity productCategoryEntity) {

        final var category = new CommonProductCategory();
        category.setId(productCategoryEntity.getId());
        category.setName(productCategoryEntity.getName());

        return  category;
    }
}
