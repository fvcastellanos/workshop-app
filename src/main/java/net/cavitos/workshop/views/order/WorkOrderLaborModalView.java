package net.cavitos.workshop.views.order;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import net.cavitos.workshop.domain.model.web.WorkOrderDetail;
import net.cavitos.workshop.service.WorkOrderDetailService;
import net.cavitos.workshop.transformer.WorkOrderDetailTransformer;
import net.cavitos.workshop.views.factory.ComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkOrderLaborModalView extends WorkOrderBaseModal {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderLaborModalView.class);

    private TextArea descriptionField;
    private NumberField salePriceField;
    private TextArea notesField;

    public WorkOrderLaborModalView(final WorkOrderDetailService workOrderDetailService,
                                   final WorkOrderDetailTransformer workOrderDetailTransformer) {

        super(workOrderDetailService, workOrderDetailTransformer);
        buildContent();
    }

    @Override
    protected void openDialog(boolean isEdit, String tenant, WorkOrderDetail entity) {

        this.isEdit = isEdit;
        this.tenant = tenant;

        setHeaderTitle(isEdit ? "Modificar Mano de Obra" : "Agregar Mano de Obra");

        if (isEdit) {

            this.workOrderDetailEntity = entity;
            this.binder.readBean(entity);
        }

        open();
    }

    private void buildContent() {

        setWidth("50%");

        descriptionField = new TextArea("Descripción");
        descriptionField.setWidthFull();
        descriptionField.setRequired(true);
        descriptionField.setMaxLength(200);
        descriptionField.setMinHeight("100px");
        descriptionField.setPlaceholder("Descripción del trabajo realizado");
        descriptionField.setErrorMessage("La descripción es requerida");

        salePriceField = new NumberField("Precio de Venta");
        salePriceField.setWidthFull();
        salePriceField.setRequiredIndicatorVisible(true);
        salePriceField.setMin(0);
        salePriceField.setStep(0.01);
        salePriceField.setPlaceholder("0.00");
        salePriceField.setErrorMessage("El precio de venta es requerido");

        notesField = new TextArea("Notas");
        notesField.setWidthFull();
        notesField.setMaxLength(300);
        notesField.setMinHeight("100px");
        notesField.setPlaceholder("Notas adicionales (opcional)");

        final var contentLayout = new VerticalLayout(
                descriptionField,
                salePriceField,
                notesField
        );

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> close()),
                ComponentFactory.buildSaveDialogButton(event -> saveChanges())
        );

        add(contentLayout, footerLayout);

        bindComponents();
    }

    private void bindComponents() {

        binder.forField(descriptionField)
                .asRequired("La descripción es requerida")
                .bind(WorkOrderDetail::getDescription, WorkOrderDetail::setDescription);

        binder.forField(salePriceField)
                .asRequired("El precio de venta es requerido")
                .withValidator(value -> value != null && value >= 0, "El precio de venta debe ser mayor o igual a 0")
                .bind(WorkOrderDetail::getSalePrice, WorkOrderDetail::setSalePrice);

        binder.forField(notesField)
                .bind(WorkOrderDetail::getNotes, WorkOrderDetail::setNotes);
    }
}
