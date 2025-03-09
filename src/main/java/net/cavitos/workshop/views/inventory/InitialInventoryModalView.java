package net.cavitos.workshop.views.inventory;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.InventoryMovement;
import net.cavitos.workshop.domain.model.web.common.CommonOperationType;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.InventoryEntity;
import net.cavitos.workshop.service.InventoryMovementService;
import net.cavitos.workshop.service.ProductService;
import net.cavitos.workshop.transformer.InventoryMovementTransformer;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.factory.ProductDropDownFactory;
import net.cavitos.workshop.views.model.transformer.DateTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.nonNull;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InitialInventoryModalView extends DialogBase<InventoryEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitialInventoryModalView.class);

    private final ProductService productService;
    private final InventoryMovementService inventoryMovementService;
    private final ZonedDateTimeFactory zonedDateTimeFactory;

    private final Binder<InventoryMovement> binder;

    private final String initialInventoryCode;

    private DatePicker dateField;
    private NumberField quantityField;
    private ComboBox<CommonProduct> productField;
    private NumberField unitPriceField;
    private TextField descriptionField;
    private ComboBox<CommonOperationType> operationTypeField;

    private InventoryMovement inventoryMovement;

    public InitialInventoryModalView(final ProductService productService,
                                     final InventoryMovementService inventoryMovementService,
                                     final ZonedDateTimeFactory zonedDateTimeFactory,
                                     @Value("${initial.inventory.movement-type.code:MI-01}") final String initialInventoryCode) {

        super();

        this.productService = productService;
        this.inventoryMovementService = inventoryMovementService;
        this.zonedDateTimeFactory = zonedDateTimeFactory;
        this.initialInventoryCode = initialInventoryCode;
        this.binder = new Binder<>(InventoryMovement.class);

        buildContent();
    }

    @Override
    protected void openDialog(boolean isEdit, String tenant, InventoryEntity entity) {

        this.isEdit = isEdit;
        setHeaderTitle(this.isEdit ? "Modificar Movimiento" : "Agregar Movimiento");
        this.tenant = tenant;

        binder.refreshFields();

        productField.setItems(loadProducts());

        final var operationType = new CommonOperationType();
        operationType.setCode(initialInventoryCode);
        operationType.setName("INVENTARIO INICIAL");

        operationTypeField.setItems(List.of(operationType));
        operationTypeField.setValue(operationType);

        descriptionField.setValue("Inventario Inicial");

        if (isEdit) {

            inventoryMovement = InventoryMovementTransformer.toWeb(entity, zonedDateTimeFactory);
            binder.readBean(inventoryMovement);
        }

        open();
    }

    private void buildContent() {

        setWidth("40%");

        operationTypeField = ComponentFactory.buildComboBox("Tipo de Operación", "100%", CommonOperationType::getName);
        operationTypeField.setReadOnly(true);

        dateField = ComponentFactory.buildDatePicker("Fecha", "100%");

        quantityField = new NumberField("Cantidad");
        quantityField.setWidth("100%");
        quantityField.setMin(1);
        quantityField.setStep(1);

        productField = ComponentFactory.buildComboBox("Producto", "100%", CommonProduct::getName);
        productField = ProductDropDownFactory.buildProductDropDown("100%");

        unitPriceField = new NumberField("Precio Unitario");
        unitPriceField.setWidth("100%");
        unitPriceField.setMin(0);

        descriptionField = new TextField("Descripción");
        descriptionField.setReadOnly(true);
        descriptionField.setWidth("100%");

        final var contentLayout = new VerticalLayout(
                operationTypeField,
                dateField,
                quantityField,
                productField,
                unitPriceField,
                descriptionField
        );

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> close()),
                ComponentFactory.buildSaveDialogButton(event -> saveChanges())
        );

        bindComponents();

        add(contentLayout, footerLayout);
    }

    private void bindComponents() {

        binder.forField(operationTypeField)
                .bind(InventoryMovement::getOperationType, InventoryMovement::setOperationType);

        binder.forField(dateField)
                .asRequired("La fecha es requerida")
                .withConverter(DateTransformer::toDomain, DateTransformer::toView)
                .bind(InventoryMovement::getOperationDate, InventoryMovement::setOperationDate);

        binder.forField(quantityField)
                .asRequired("La cantidad es requerida")
                .bind(InventoryMovement::getQuantity, InventoryMovement::setQuantity);

        binder.forField(productField)
                .asRequired("El producto es requerido")
                .bind(InventoryMovement::getProduct, InventoryMovement::setProduct);

        binder.forField(unitPriceField)
                .asRequired("El precio unitario es requerido")
                .bind(InventoryMovement::getUnitPrice, InventoryMovement::setUnitPrice);

        binder.forField(descriptionField)
                .bind(InventoryMovement::getDescription, InventoryMovement::setDescription);
    }

    private List<CommonProduct> loadProducts() {

        return productService.loadProducts(tenant)
                .stream()
                .map(entity -> {

                    final var product = new CommonProduct();
                    product.setCode(entity.getCode());
                    product.setType(entity.getType());
                    product.setName(entity.getName());

                    return product;
                }).toList();
    }

    private void saveChanges() {

        try {

            final var validationResult = binder.validate();

            if (validationResult.isOk()) {

                final var movement = new InventoryMovement();
                binder.writeBeanIfValid(movement);

                InventoryEntity entity = null;

                if (isEdit) {

                    entity = inventoryMovementService.update(tenant, inventoryMovement.getId(), movement);
                } else {

                    // Set initial inventory operation type
                    final var operationType = new CommonOperationType();
                    operationType.setCode(initialInventoryCode);

                    movement.setOperationType(operationType);
                    movement.setDescription("Inventario Inicial");

                    entity = inventoryMovementService.add(tenant, movement);
                }

                if (nonNull(onSaveEvent)) {
                    onSaveEvent.accept(entity);
                }

                close();
            }
        } catch (final Exception exception) {
            LOGGER.error("Unable to save invoice detail: ", exception);
            showErrorNotification(exception.getMessage());
        }

    }
}
