package net.cavitos.workshop.views.factory;

import com.vaadin.flow.component.combobox.ComboBox;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;

public final class ProductDropDownFactory {

    private ProductDropDownFactory() {
    }

    public static ComboBox<CommonProduct> buildProductDropDown(final String width) {

        ComboBox.ItemFilter<CommonProduct> filter = (product, filterString) -> {
            final var filterLower = filterString.toLowerCase();
            return product.getName().toLowerCase().contains(filterLower) || product.getCode().toLowerCase().contains(filterLower);
        };

        return ComponentFactory.buildComboBox("Producto", width, CommonProduct::getName);
    }
}
