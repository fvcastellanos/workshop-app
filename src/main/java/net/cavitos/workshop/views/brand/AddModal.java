package net.cavitos.workshop.views.brand;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.CarBrand;
import net.cavitos.workshop.factory.BusinessExceptionFactory;
import net.cavitos.workshop.model.entity.CarBrandEntity;
import net.cavitos.workshop.service.CarBrandService;
import net.cavitos.workshop.transformer.CarBrandTransformer;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.model.Status;
import net.cavitos.workshop.views.model.transformer.StatusTransformer;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@Component
public class AddModal extends DialogBase<CarBrandEntity> {

    private final Binder<CarBrand> binder;
    private final CarBrandService carBrandService;

    private final Select<Status> statusField;

    private Consumer<CarBrandEntity> onSaveEvent;

    private boolean isEdit;
    private CarBrandEntity carBrandEntity;

    public AddModal(final CarBrandService carBrandService) {

        super();

        this.binder = new Binder<>(CarBrand.class);
        this.carBrandService = carBrandService;
        this.isEdit = false;

        this.setWidth("40%");

        final var contentLayout = new VerticalLayout();
        contentLayout.setWidthFull();

        final var nameField = new TextField();
        nameField.setLabel("Nombre");
        nameField.setWidth("100%");
        nameField.setAutofocus(true);

        final var descriptionField = new TextArea("Descripción");
        descriptionField.setWidth("100%");

        this.statusField = ComponentFactory.buildStatusSelect("100%", StatusTransformer.toView(1));

        binder.forField(nameField)
                .asRequired("El nombre es requerido")
                .withValidator(name -> name.length() >= 2, "Longitud mínima 2 caracteres")
                .withValidator(name -> name.length() <= 100, "Longitud máxima 100 caracteres")
                .bind(CarBrand::getName, CarBrand::setName);

        binder.forField(descriptionField)
                .withValidator(description -> description.length() <= 300, "Longitud máxima 300 caracteres")
                .bind(CarBrand::getDescription, CarBrand::setDescription);

        binder.forField(statusField)
                .asRequired("El estado es requerido")
                .withConverter(StatusTransformer::toDomain, StatusTransformer::toView)
                        .bind(CarBrand::getActive, CarBrand::setActive);

        contentLayout.add(nameField);
        contentLayout.add(descriptionField);
        contentLayout.add(statusField);

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> this.close()),
                ComponentFactory.buildSaveDialogButton(event -> this.saveChanges())
        );

        add(contentLayout);
        add(footerLayout);
    }

    @Override
    protected void openDialog(final boolean isEdit, final CarBrandEntity carBrandEntity) {

        this.isEdit = isEdit;
        this.setHeaderTitle(isEdit ? "Modificar Marca" : "Agregar Marca");

        binder.refreshFields();

        statusField.setValue(StatusTransformer.toView(1)); // Active status
        statusField.setReadOnly(!isEdit);

        if (isEdit) {
            this.carBrandEntity = carBrandEntity;
            binder.readBean(CarBrandTransformer.toWeb(carBrandEntity));
        }

        this.open();
    }

    private void saveChanges() {

        final var validationResult = binder.validate();

        if (validationResult.isOk()) {

            try {

                final var carBrand = new CarBrand();
                binder.writeBeanIfValid(carBrand);

                final var entity = isEdit ? carBrandService.update("resta", carBrandEntity.getId(), carBrand)
                        : carBrandService.add("resta", carBrand);

                if (nonNull(onSaveEvent)) {
                    onSaveEvent.accept(entity);
                }

                this.close();

            } catch (Exception exception) {

//                notifi
                throw BusinessExceptionFactory.createBusinessException("No es posible guardar la marca de vehículo");
            }
        }

    }
}
