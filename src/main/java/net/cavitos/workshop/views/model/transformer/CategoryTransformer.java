package net.cavitos.workshop.views.model.transformer;

import net.cavitos.workshop.domain.model.web.common.CommonProductCategory;
import net.cavitos.workshop.views.model.TypeOption;

public final class CategoryTransformer {

    private CategoryTransformer() {
    }

    public static CommonProductCategory toDomain(final TypeOption typeOption) {

        var productCategory = new CommonProductCategory();
        productCategory.setId(typeOption.getValue());
        productCategory.setName(typeOption.getLabel());

        return productCategory;
    }

//    public static



}
