package net.cavitos.workshop.views.brand;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.CarLine;
import net.cavitos.workshop.model.entity.CarBrandEntity;
import net.cavitos.workshop.model.entity.CarLineEntity;
import net.cavitos.workshop.service.CarLineService;
import net.cavitos.workshop.transformer.CarLineTransformer;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.model.Status;
import net.cavitos.workshop.views.model.transformer.StatusTransformer;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class AddModelDialog extends DialogBase<CarLineEntity> {

    private final Select<Status> statusField;
    private final Binder<CarLine> binder;

    private final CarLineService carLineService;

    private CarLineEntity carLineEntity;
    private CarBrandEntity carBrandEntity;

    public AddModelDialog(final CarLineService carLineService) {
        super();

        this.setWidth("40%");

        this.binder = new Binder<>(CarLine.class);
        this.carLineService = carLineService;

        final var contentLayout = new VerticalLayout();
        contentLayout.setWidthFull();

        final var nameField = new TextField();
        nameField.setLabel("Nombre");
        nameField.setWidth("100%");
        nameField.setAutofocus(true);

        final var descriptionField = new TextArea("Descripción");
        descriptionField.setWidth("100%");

        this.statusField = ComponentFactory.buildStatusSelect("100%", StatusTransformer.toView(1));

        contentLayout.add(nameField);
        contentLayout.add(descriptionField);
        contentLayout.add(statusField);

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> this.close()),
                ComponentFactory.buildSaveDialogButton(event -> this.saveChanges())
        );

        binder.forField(nameField)
                .asRequired("El nombre es requerido")
                .withValidator(name -> name.length() >= 2, "Longitud mínima 2 caracteres")
                .withValidator(name -> name.length() <= 100, "Longitud máxima 100 caracteres")
                .bind(CarLine::getName, CarLine::setName);

        binder.forField(descriptionField)
                .withValidator(description -> description.length() <= 255, "Longitud máxima 255 caracteres")
                .bind(CarLine::getDescription, CarLine::setDescription);

        binder.forField(statusField)
                .asRequired("El estado es requerido")
                .withConverter(StatusTransformer::toDomain, StatusTransformer::toView)
                .bind(CarLine::getActive, CarLine::setActive);

        add(contentLayout);
        add(footerLayout);
    }

    public void openDialogForNew(final String tenant, final CarBrandEntity carBrandEntity) {
        this.carBrandEntity = carBrandEntity;
        this.openDialogForNew(tenant);
    }

    @Override
    protected void openDialog(boolean isEdit, final String tenant, CarLineEntity entity) {

        this.isEdit = isEdit;
        this.setHeaderTitle(isEdit ? "Modificar Modelo" : "Agregar Modelo");
        this.tenant = tenant;

        binder.refreshFields();

        statusField.setValue(StatusTransformer.toView(1)); // Active status
        statusField.setReadOnly(!isEdit);

        if (isEdit) {
            carLineEntity = entity;
            carBrandEntity = carLineEntity.getCarBrand();
            binder.readBean(CarLineTransformer.toWeb(carLineEntity));
        }

        this.open();
    }

    private void saveChanges() {

        final var validationResult = binder.validate();

        if (validationResult.isOk()) {

            try {

                final var carLine = new CarLine();
                binder.writeBeanIfValid(carLine);

                final var carBrandId = carBrandEntity.getId();

                final var entity = isEdit ? carLineService.update(tenant, carBrandId, carLineEntity.getId(), carLine)
                        : carLineService.add(tenant, carBrandId, carLine);

                if (nonNull(onSaveEvent)) {
                    onSaveEvent.accept(entity);
                }

                close();
            } catch (Exception exception) {
                showErrorNotification(exception.getMessage());
            }
        }

    }

}
