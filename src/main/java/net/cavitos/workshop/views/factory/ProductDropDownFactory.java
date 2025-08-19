package net.cavitos.workshop.views.factory;

import java.util.List;

import com.vaadin.flow.component.combobox.ComboBox;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;

public final class ProductDropDownFactory {

    private ProductDropDownFactory() {
    }

    public static ComboBox<CommonProduct> buildProductDropDown(final String width) {

        return ComponentFactory.buildComboBox("Producto", width, CommonProduct::getName);
    }

    public static void addProducts(final List<CommonProduct> products, final ComboBox<CommonProduct> productField) {

        productField.setItems(buildProductFilter(), products);
    }

    private static ComboBox.ItemFilter<CommonProduct> buildProductFilter() {
        return (product, filterString) -> {
            final var filterLower = filterString.toLowerCase();
            return product.getName().toLowerCase().contains(filterLower) || product.getCode().toLowerCase().contains(filterLower);
        };
    }
}
