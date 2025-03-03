package net.cavitos.workshop.views.movement;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.InventoryMovementType;
import net.cavitos.workshop.model.entity.InventoryMovementTypeEntity;
import net.cavitos.workshop.service.InventoryMovementTypeService;
import net.cavitos.workshop.transformer.InventoryMovementTypeTransformer;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.model.Status;
import net.cavitos.workshop.views.model.TypeOption;
import net.cavitos.workshop.views.model.transformer.StatusTransformer;
import net.cavitos.workshop.views.model.transformer.TypeTransformer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.nonNull;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildTypeSelect;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MovementTypeModalView extends DialogBase<InventoryMovementTypeEntity> {

    private final InventoryMovementTypeService movementTypeService;

    private final Binder<InventoryMovementType> binder;

    private TextField codeField;
    private TextField nameField;
    private TextArea descriptionField;
    private Select<TypeOption> movementTypeField;
    private Select<Status> statusField;

    private InventoryMovementTypeEntity movementTypeEntity;

    public MovementTypeModalView(final InventoryMovementTypeService movementTypeService) {
        super();

        this.binder = new Binder<>(InventoryMovementType.class);

        this.movementTypeService = movementTypeService;

        buildContent();
    }

    @Override
    protected void openDialog(boolean isEdit, String tenant, InventoryMovementTypeEntity entity) {

        this.isEdit = isEdit;
        setHeaderTitle(isEdit ? "Modificar Tipo de Movimiento" : "Agregar Tipo de Movimiento");

        this.tenant = tenant;

        binder.refreshFields();

        codeField.setVisible(isEdit);
        statusField.setReadOnly(!isEdit);

        statusField.setValue(StatusTransformer.toView(1)); // Active status
        movementTypeField.setValue(TypeTransformer.toInventoryMovementTypeView("I"));

        if (isEdit) {

            movementTypeEntity = entity;
            binder.readBean(InventoryMovementTypeTransformer.toWeb(entity));
        }

        open();
    }

    private void buildContent() {

        setWidth("40%");

        final var movementTypes = List.of(
                new TypeOption("Entrada", "I"),
                new TypeOption( "Salida", "O")
        );

        movementTypeField = buildTypeSelect("100%", "Tipo de Movimiento", movementTypes, "I");

        codeField = new TextField("Código");
        codeField.setWidth("100%");
        codeField.setReadOnly(true);

        nameField = new TextField("Nombre");
        nameField.setWidth("100%");
        nameField.setAutofocus(true);

        descriptionField = new TextArea("Descripción");
        descriptionField.setWidth("100%");

        statusField = ComponentFactory.buildStatusSelect("100%", StatusTransformer.toView(1));

        final var contentLayout = new VerticalLayout(
                movementTypeField,
                codeField,
                nameField,
                descriptionField,
                statusField
        );

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> this.close()),
                ComponentFactory.buildSaveDialogButton(event -> this.saveChanges())
        );

        add(contentLayout, footerLayout);

        bindComponents();
    }

    private void bindComponents() {

        binder.forField(codeField)
                .bind(InventoryMovementType::getCode, InventoryMovementType::setCode);

        binder.forField(nameField)
                .asRequired("El nombre es requerido")
                .withValidator(name -> name.length() >= 2, "Longitud mínima 2 caracteres")
                .withValidator(name -> name.length() <= 100, "Longitud máxima 100 caracteres")
                .bind(InventoryMovementType::getName, InventoryMovementType::setName);

        binder.forField(descriptionField)
                .withValidator(description -> description.length() <= 300, "Longitud máxima 300 caracteres")
                .bind(InventoryMovementType::getDescription, InventoryMovementType::setDescription);

        binder.forField(movementTypeField)
                .asRequired("El tipo de movimiento es requerido")
                .withConverter(TypeTransformer::toDomain, TypeTransformer::toInventoryMovementTypeView)
                .bind(InventoryMovementType::getType, InventoryMovementType::setType);

        binder.forField(statusField)
                .asRequired("El estado es requerido")
                .withConverter(StatusTransformer::toDomain, StatusTransformer::toView)
                .bind(InventoryMovementType::getActive, InventoryMovementType::setActive);
    }

    private void saveChanges() {

        final var validationResult = binder.validate();

        if (validationResult.isOk()) {

            try {
                final var movementType = new InventoryMovementType();
                binder.writeBeanIfValid(movementType);

                final var entity = isEdit ? movementTypeService.update(movementTypeEntity.getId(), movementType, tenant)
                        : movementTypeService.add(movementType, tenant);

                if (nonNull(onSaveEvent)) {
                    onSaveEvent.accept(entity);
                }

                close();

            } catch (final Exception exception) {
                showErrorNotification(exception.getMessage());
            }
        }
    }
}
