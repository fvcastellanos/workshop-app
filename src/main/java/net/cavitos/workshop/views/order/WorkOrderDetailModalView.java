package net.cavitos.workshop.views.order;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import net.cavitos.workshop.domain.model.web.WorkOrderDetail;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.service.InventoryMovementService;
import net.cavitos.workshop.service.PriceService;
import net.cavitos.workshop.service.ProductService;
import net.cavitos.workshop.service.WorkOrderDetailService;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.factory.ProductDropDownFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkOrderDetailModalView extends WorkOrderBaseModal {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderDetailModalView.class);

    private final PriceService priceService;
    private final ProductService productService;
    private final InventoryMovementService inventoryMovementService;

    private NumberField quantityField;
    private ComboBox<CommonProduct> productField;
    private NumberField unitPriceField;
    private NumberField salePriceField;
    private TextArea notesField;

    public WorkOrderDetailModalView(final WorkOrderDetailService workOrderDetailService,
                                    final ProductService productService,
                                    final PriceService priceService,
                                    final InventoryMovementService inventoryMovementService) {

        super(workOrderDetailService);

        this.productService = productService;
        this.priceService = priceService;
        this.inventoryMovementService = inventoryMovementService;

        buildContent();
    }

    @Override
    protected void openDialog(boolean isEdit, String tenant, WorkOrderDetail entity) {

        this.isEdit = isEdit;
        setHeaderTitle(isEdit ? "Modificar Detalle" : "Agregar Detalle");

        this.tenant = tenant;

        binder.refreshFields();

        ProductDropDownFactory.addProducts(loadProducts(), productField);
        quantityField.setValue(1.0);

        if (isEdit) {

            this.workOrderDetailEntity = entity;
            binder.readBean(entity);
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
        quantityField.addValueChangeListener(event -> updateSalePrice(true));

        productField = ProductDropDownFactory.buildProductDropDown("70%");
        productField.addValueChangeListener(event -> retrieveProductUnitPrice());

        salePriceField = new NumberField("Precio de Venta");
        salePriceField.setWidth("50%");
        salePriceField.setMin(0);

        unitPriceField = new NumberField("Precio Unitario");
        unitPriceField.setWidth("50%");
        unitPriceField.setMin(0);
        unitPriceField.addValueChangeListener(event -> updateSalePrice(false));

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
            row2,
            notesField
        );

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> close()),
                ComponentFactory.buildSaveDialogButton(event -> saveChanges())
        );

        add(controls, footerLayout);

        bindComponents();
    }

    private void bindComponents() {

        binder.forField(quantityField)
                .asRequired("Cantidad es requerida")
                .withValidator(quantityField -> quantityField > 0, "La cantidad debe ser mayor que 0")
                .bind(WorkOrderDetail::getQuantity, WorkOrderDetail::setQuantity);

        binder.forField(productField)
                .asRequired("Producto es requerido")
                .bind(WorkOrderDetail::getProduct, WorkOrderDetail::setProduct);

        binder.forField(unitPriceField)
                .asRequired("Precio Unitario es requerido")
                .withValidator(unitPriceField -> unitPriceField > 0, "El precio unitario debe ser mayor que 0")
                .bind(WorkOrderDetail::getUnitPrice, WorkOrderDetail::setUnitPrice);

        binder.forField(salePriceField)
                .asRequired("Precio de Venta es requerido")
                .withValidator(salePriceField -> salePriceField >= (quantityField.getValue() * unitPriceField.getValue()),
                        "El precio venta debe ser mayor que el costo unitario" )
                .bind(WorkOrderDetail::getSalePrice, WorkOrderDetail::setSalePrice);

        binder.forField(notesField)
                .bind(WorkOrderDetail::getNotes, WorkOrderDetail::setNotes);
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

    private void updateSalePrice(boolean mandatory) {

        final var unitPrice = unitPriceField.getValue();
        final var quantity = quantityField.getValue();
        final var price = salePriceField.getValue();

        if (mandatory) {

            if (unitPrice != null && quantity != null) {

                final var salePrice = priceService.calculatePrice(unitPrice * quantity, tenant);
                salePriceField.setValue(salePrice);
            }

            return;
        }

        if (Objects.isNull(price)) {

            final var salePrice = priceService.calculatePrice(unitPrice * quantity, tenant);
            salePriceField.setValue(salePrice);
        }
    }

    private void retrieveProductUnitPrice() {

        final var product = productField.getValue();

        if (Objects.nonNull(product) && product.isStorable()) {

            final var unitPrice = inventoryMovementService.findLatestUnitPrice(product.getCode(), tenant);
            unitPriceField.setValue(unitPrice);
            updateSalePrice(true);
        }
    }
}
