package net.cavitos.workshop.views.sequences;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.Sequence;
import net.cavitos.workshop.sequence.model.entity.SequenceEntity;
import net.cavitos.workshop.sequence.service.SequenceService;
import net.cavitos.workshop.transformer.SequenceTransformer;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SequenceModalView extends DialogBase<SequenceEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceModalView.class);

    private final SequenceService sequenceService;

    private SequenceEntity sequenceEntity;

    private Binder<Sequence> binder;

    private TextField prefixTextField;
    private IntegerField padSizeField;
    private IntegerField stepSizeField;
    private TextArea descriptionTextArea;

    public SequenceModalView(final SequenceService sequenceService) {

        super();

        this.sequenceService = sequenceService;

        this.binder = new Binder<>(Sequence.class);

        buildContent();
    }

    @Override
    protected void openDialog(final boolean isEdit, final String tenant, final SequenceEntity entity) {

        this.isEdit = isEdit;
        setHeaderTitle(isEdit ? "Editar Secuencia" : "Nueva Secuencia");
        this.tenant = tenant;

        binder.refreshFields();

        stepSizeField.setValue(1);
        padSizeField.setValue(5);

        if (isEdit) {

            this.sequenceEntity = entity;
            binder.readBean(SequenceTransformer.toWeb(entity));
        }

        open();

    }

    private void buildContent() {

        setWidth("40%");

        prefixTextField = new TextField("Prefijo");
        prefixTextField.setWidth("100%");
        prefixTextField.setAutofocus(true);

        padSizeField = new IntegerField("Digitos");
        padSizeField.setWidth("100%");
        padSizeField.setMin(1);
        padSizeField.setStep(1);

        stepSizeField = new IntegerField("Incremento");
        stepSizeField.setWidth("100%");
        stepSizeField.setMin(1);
        stepSizeField.setStep(1);

        descriptionTextArea = new TextArea("Descripción");
        descriptionTextArea.setWidth("100%");

        final var contentLayout = new VerticalLayout(
                prefixTextField,
                padSizeField,
                stepSizeField,
                descriptionTextArea
        );

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> close()),
                ComponentFactory.buildSaveDialogButton(event -> saveChanges())
        );

        bindComponents();

        add(contentLayout, footerLayout);
    }

    private void bindComponents() {

        binder.forField(prefixTextField)
                .asRequired("El prefijo es requerido")
                .withValidator(value -> value.length() <= 5, "El prefijo no puede ser mayor a 5 caracteres")
                .bind(Sequence::getPrefix, Sequence::setPrefix);

        binder.forField(padSizeField)
                .asRequired("El número de dígitos es requerido")
                .withValidator(value -> value >= 1 && value <= 8, "El tamaño de dígitos debe estar entre 1 y 8")
                .bind(Sequence::getPadSize, Sequence::setPadSize);

        binder.forField(stepSizeField)
                .asRequired("El tamaño de incremento es requerido")
                .withValidator(value -> value >= 1 && value <= 10, "El tamaño de incremento debe estar entre 1 y 10")
                .bind(Sequence::getStepSize, Sequence::setStepSize);

        binder.forField(descriptionTextArea)
                .withValidator(value -> value.length() <= 300, "La descripción no puede ser mayor a 300 caracteres")
                .bind(Sequence::getDescription, Sequence::setDescription);
    }

    private void saveChanges() {

        try {
            final var validationResult = binder.validate();

            if (validationResult.isOk()) {

                final var sequence = new Sequence();
                binder.writeBeanIfValid(sequence);

               final var entity = isEdit ? sequenceService.update(sequenceEntity.getId(), sequence, tenant)
                       : sequenceService.add(sequence, tenant);

               if (onSaveEvent != null) {
                   onSaveEvent.accept(entity);
               }

                close();
            }
        } catch (final Exception exception) {

            LOGGER.error("Unable to save sequence", exception);
            showErrorNotification(exception.getMessage());
        }
    }
}
