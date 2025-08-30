package net.cavitos.workshop.views.order;

import static java.util.Objects.nonNull;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.WorkOrderDetail;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.service.PriceService;
import net.cavitos.workshop.service.ProductService;
import net.cavitos.workshop.service.WorkOrderDetailService;
import net.cavitos.workshop.transformer.WorkOrderDetailTransformer;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.factory.ProductDropDownFactory;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkOrderDetailModalView extends DialogBase<WorkOrderDetail> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderDetailModalView.class);

    private final Binder<WorkOrderDetail> binder;

    private final PriceService priceService;
    private final WorkOrderDetailService workOrderDetailService;
    private final ProductService productService;

    private WorkOrderDetail workOrderDetailEntity;

    private NumberField quantityField;
    private ComboBox<CommonProduct> productField;
    private NumberField unitPriceField;
    private NumberField salePriceField;
    private TextArea descriptionField;
    private TextArea notesField;

    private String workOrderId;

    public WorkOrderDetailModalView(final WorkOrderDetailService workOrderDetailService,
                                    final ProductService productService,
                                    final PriceService priceService) {

        super();

        this.workOrderDetailService = workOrderDetailService;
        this.productService = productService;
        this.priceService = priceService;

        this.binder = new Binder<>(WorkOrderDetail.class);

        buildContent();
    }

    public void setWorkOrderId(final String workOrderId) {
        this.workOrderId = workOrderId;
    }

    @Override
    protected void openDialog(boolean isEdit, String tenant, WorkOrderDetail entity) {

        this.isEdit = isEdit;
        setHeaderTitle(isEdit ? "Modificar Detalle" : "Agregar Detalle");

        this.tenant = tenant;

        binder.refreshFields();

        ProductDropDownFactory.addProducts(loadProducts(), productField);

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

        productField = ProductDropDownFactory.buildProductDropDown("70%");

        descriptionField = new TextArea("Descripción");
        descriptionField.setWidth("100%");

        salePriceField = new NumberField("Precio de Venta");
        salePriceField.setWidth("50%");
        salePriceField.setMin(0);

        unitPriceField = new NumberField("Precio Unitario");
        unitPriceField.setWidth("50%");
        unitPriceField.setMin(0);
        unitPriceField.addValueChangeListener(event -> updateSalePrice());

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

        binder.forField(descriptionField)
                .bind(WorkOrderDetail::getDescription, WorkOrderDetail::setDescription);

        binder.forField(unitPriceField)
                .asRequired("Precio Unitario es requerido")
                .bind(WorkOrderDetail::getUnitPrice, WorkOrderDetail::setUnitPrice);

        binder.forField(salePriceField)
                .asRequired("Precio de Venta es requerido")
                .bind(WorkOrderDetail::getSalePrice, WorkOrderDetail::setSalePrice);

        binder.forField(notesField)
                .bind(WorkOrderDetail::getNotes, WorkOrderDetail::setNotes);
    }
    
    private void saveChanges() {

        try {

            var validationResult = binder.validate();

            if (validationResult.isOk()) {

                final var orderDetail = new WorkOrderDetail();
                binder.writeBeanIfValid(orderDetail);

                final var entity = isEdit ? workOrderDetailService.updateOrderDetail(workOrderDetailEntity.getOrderId(), workOrderDetailEntity.getId(), orderDetail, tenant)
                        : workOrderDetailService.addOrderDetail(tenant, workOrderId, orderDetail);

                if (nonNull(onSaveEvent)) {

                    onSaveEvent.accept(WorkOrderDetailTransformer.toWeb(entity));
                }

                close();
            }
        } catch (Exception exception) {
            LOGGER.error("Unable to save work order detail", exception);
            showErrorNotification(exception.getMessage());
        }
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

    private void updateSalePrice() {

        final var unitPrice = unitPriceField.getValue();
        final var quantity = quantityField.getValue();

        if (unitPrice != null && quantity != null) {

            final var salePrice = priceService.calculatePrice(unitPrice * quantity, tenant);
            salePriceField.setValue(salePrice);
        }
    }
}
