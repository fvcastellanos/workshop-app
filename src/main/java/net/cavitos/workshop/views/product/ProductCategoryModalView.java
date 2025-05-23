package net.cavitos.workshop.views.product;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.ProductCategory;
import net.cavitos.workshop.model.entity.ProductCategoryEntity;
import net.cavitos.workshop.sequence.service.SequenceService;
import net.cavitos.workshop.service.ProductCategoryService;
import net.cavitos.workshop.transformer.ProductCategoryTransformer;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.model.Status;
import net.cavitos.workshop.views.model.TypeOption;
import net.cavitos.workshop.views.model.transformer.SequenceTransformer;
import net.cavitos.workshop.views.model.transformer.StatusTransformer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductCategoryModalView extends DialogBase<ProductCategoryEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductCategoryModalView.class);

    private final ProductCategoryService productCategoryService;
    private final SequenceService sequenceService;
    private final Binder<ProductCategory> binder;

    private Select<Status> statusField;
    private TextField nameField;
    private TextArea descriptionField;
    private TextField codeField;
    private Select<TypeOption> sequenceField;

    private ProductCategoryEntity productCategoryEntity;

    public ProductCategoryModalView(final ProductCategoryService productCategoryService,
                                    final SequenceService sequenceService) {
        super();

        this.productCategoryService = productCategoryService;
        this.sequenceService = sequenceService;
        this.binder = new Binder<>(ProductCategory.class);

        buildContent();
    }

    @Override
    protected void openDialog(boolean isEdit, String tenant, ProductCategoryEntity entity) {

        this.isEdit = isEdit;
        this.setHeaderTitle(isEdit ? "Modificar Categoría" : "Agregar Categoría");
        this.tenant = tenant;

        binder.refreshFields();

        statusField.setReadOnly(!isEdit);
        statusField.setValue(StatusTransformer.toView(1)); // Active status

        codeField.setVisible(isEdit);

        sequenceField.setItems(loadSequences());

        if (isEdit) {

            productCategoryEntity = entity;
            binder.readBean(ProductCategoryTransformer.toWeb(entity));
        }

        this.open();
    }

    private void buildContent() {

        setWidth("40%");

        codeField = new TextField("Código");
        codeField.setWidth("100%");
        codeField.setReadOnly(true);

        nameField = new TextField("Nombre");
        nameField.setWidth("100%");
        nameField.setAutofocus(true);

        final var initialItems = Collections.singletonList(new TypeOption("Seleccione", StringUtils.EMPTY));
        sequenceField = ComponentFactory.buildTypeSelect("100%", "Secuencia", initialItems, StringUtils.EMPTY);

        descriptionField = new TextArea("Descripción");
        descriptionField.setWidth("100%");

        statusField = ComponentFactory.buildStatusSelect("100%", StatusTransformer.toView(1));

        final var contentLayout = new VerticalLayout(
                codeField,
                nameField,
                sequenceField,
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
                .bind(ProductCategory::getCode, ProductCategory::setCode);

        binder.forField(nameField)
                .asRequired("El nombre es requerido")
                .withValidator(name -> name.length() >= 2, "Longitud mínima 2 caracteres")
                .withValidator(name -> name.length() <= 100, "Longitud máxima 100 caracteres")
                .bind(ProductCategory::getName, ProductCategory::setName);

        binder.forField(sequenceField)
                .withValidator(sequence -> nonNull(sequence) && !sequence.getValue().equals(StringUtils.EMPTY), "Seleccione una secuencia")
                .withConverter(SequenceTransformer::toDomain, SequenceTransformer::toView)
                .bind(ProductCategory::getSequence, ProductCategory::setSequence);

        binder.forField(descriptionField)
                .withValidator(description -> description.length() <= 300, "Longitud máxima 300 caracteres")
                .bind(ProductCategory::getDescription, ProductCategory::setDescription);

        binder.forField(statusField)
                .asRequired("El estado es requerido")
                .withConverter(StatusTransformer::toDomain, StatusTransformer::toView)
                .bind(ProductCategory::getActive, ProductCategory::setActive);
    }

    private void saveChanges() {

        final var validationResult = binder.validate();

        if (validationResult.isOk()) {

            try {
                final var productCategory = new ProductCategory();
                binder.writeBeanIfValid(productCategory);

                final var entity = isEdit ? productCategoryService.update(tenant, productCategoryEntity.getId(), productCategory)
                        : productCategoryService.add(tenant, productCategory);

                if (nonNull(onSaveEvent)) {

                    onSaveEvent.accept(entity);
                }

                close();

            } catch (final Exception exception) {

                LOGGER.error("Error saving product category", exception);
                showErrorNotification(exception.getMessage());
            }
        }
    }

    private List<TypeOption> loadSequences() {

        return sequenceService.getSequences(tenant)
                .stream()
                .map(sequenceEntity -> new TypeOption(sequenceEntity.getPrefix() + " - " + sequenceEntity.getDescription(),
                        sequenceEntity.getId()))
                .toList();
    }
}
