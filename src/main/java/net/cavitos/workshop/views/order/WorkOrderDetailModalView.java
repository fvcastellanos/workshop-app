package net.cavitos.workshop.views.order;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.pro.licensechecker.Product;

import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.service.ProductService;
import net.cavitos.workshop.service.WorkOrderDetailService;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.factory.ProductDropDownFactory;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkOrderDetailModalView extends DialogBase<WorkOrderDetailEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderDetailModalView.class);

    private final WorkOrderDetailService workOrderDetailService;
    private final ProductService productService;

    private WorkOrderDetailEntity workOrderDetailEntity;

    private NumberField quantityField;
    private ComboBox<CommonProduct> productField;
    private NumberField unitPriceField;
    private NumberField salePriceField;
    private TextArea descriptionField;
    private TextArea notesField;

    public WorkOrderDetailModalView(final WorkOrderDetailService workOrderDetailService,
                                    final ProductService productService) {

        super();

        this.workOrderDetailService = workOrderDetailService;
        this.productService = productService;

        buildContent();
    }

    @Override
    protected void openDialog(boolean isEdit, String tenant, WorkOrderDetailEntity entity) {

        this.isEdit = isEdit;
        setHeaderTitle(isEdit ? "Modificar Detalle" : "Agregar Detalle");

        this.tenant = tenant;

        ProductDropDownFactory.addProducts(loadProducts(), productField);

        if (isEdit) {

            this.workOrderDetailEntity = entity;
        }

        open();
    }

    private void buildContent() {

        setWidth("50%");

        quantityField = new NumberField("Cantidad");
        quantityField.setAutofocus(true);
        quantityField.setWidth("30%");
        quantityField.setMin(1);
        quantityField.setStep(1);

        productField = ProductDropDownFactory.buildProductDropDown("70%");

        descriptionField = new TextArea("Descripción");
        descriptionField.setWidth("100%");

        unitPriceField = new NumberField("Precio Unitario");
        unitPriceField.setWidth("50%");
        unitPriceField.setMin(0);

        salePriceField = new NumberField("Precio de Venta");
        salePriceField.setWidth("50%");
        salePriceField.setMin(0);

        notesField = new TextArea("Notas");
        notesField.setWidth("100%");

        final var row1 = new HorizontalLayout(
            quantityField,
            productField
        );
        row1.setWidth("100%");

        final var row2 = new HorizontalLayout(
            unitPriceField,
            salePriceField
        );
        row2.setWidth("100%");

        final var controls = new VerticalLayout(
            row1,
            descriptionField,
            row2,
            notesField
        );

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> close()),
                ComponentFactory.buildSaveDialogButton(event -> saveChanges())
        );

        add(controls, footerLayout);

    }
    
    private void saveChanges() {
        LOGGER.info("Saving changes to work order detail");

        close();
    }

    private List<CommonProduct> loadProducts() {

        return productService.loadProducts(tenant)
                .stream()
                .map(entity -> {

                    final var product = new CommonProduct();
                    product.setCode(entity.getCode());
                    product.setStorable(entity.isStorable());
                    product.setName(entity.getName());

                    return product;
                }).toList();
    }
}
