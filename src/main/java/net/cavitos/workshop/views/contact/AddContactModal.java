package net.cavitos.workshop.views.contact;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.CarBrand;
import net.cavitos.workshop.domain.model.web.Contact;
import net.cavitos.workshop.model.entity.ContactEntity;
import net.cavitos.workshop.service.ContactService;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.model.Status;
import net.cavitos.workshop.views.model.TypeOption;
import net.cavitos.workshop.views.model.transformer.StatusTransformer;
import net.cavitos.workshop.views.model.transformer.TypeTransformer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddContactModal extends DialogBase<ContactEntity> {

    private final ContactService contactService;
    private final Binder<Contact> binder;

    private ContactEntity contactEntity;

    private Select<Status> statusField;

    public AddContactModal(final ContactService contactService) {
        super();

        this.contactService = contactService;
        this.binder = new Binder<>(Contact.class);

        buildContent();
    }

    @Override
    protected void openDialog(boolean isEdit, String tenant, ContactEntity entity) {

        this.isEdit = isEdit;
        this.setHeaderTitle(isEdit ? "Modificar Contacto" : "Agregar Contacto");
        this.tenant = tenant;

        binder.refreshFields();

        statusField.setValue(StatusTransformer.toView(1)); // Active status
        statusField.setReadOnly(!isEdit);

        if (isEdit) {
            contactEntity = entity;
//            binder.readBean(ContactTransformer.toWeb(contactEntity));
        }

        this.open();
    }

    private void buildContent() {

        setWidth("40%");

        final var contentLayout = new VerticalLayout();
        contentLayout.setWidthFull();

        final var typeSelect = ComponentFactory.buildTypeSelect("100%", "Tipo", List.of(
                new TypeOption("Cliente", "C"),
                new TypeOption("Proveedor", "P")
        ), "C");

        typeSelect.setAutofocus(true);

        final var nameField = new TextField();
        nameField.setLabel("Nombre");
        nameField.setWidth("100%");

        final var contactField = new TextField();
        contactField.setLabel("Contacto");
        contactField.setWidth("100%");

        final var taxIdField = new TextField();
        taxIdField.setLabel("NIT");
        taxIdField.setWidth("100%");

        final var descriptionField = new TextArea("Descripción");
        descriptionField.setWidth("100%");

        statusField = ComponentFactory.buildStatusSelect("100%", StatusTransformer.toView(1));

        binder.forField(typeSelect)
                .asRequired("El tipo es requerido")
                .withConverter(TypeTransformer::toDomain, TypeTransformer::toView)
                .bind(Contact::getType, Contact::setType);

        binder.forField(nameField)
                .asRequired("El nombre es requerido")
                .withValidator(name -> name.length() >= 2, "Longitud mínima 2 caracteres")
                .withValidator(name -> name.length() <= 100, "Longitud máxima 100 caracteres")
                .bind(Contact::getName, Contact::setName);

        binder.forField(descriptionField)
                .withValidator(description -> description.length() <= 300, "Longitud máxima 300 caracteres")
                .bind(Contact::getDescription, Contact::setDescription);

        binder.forField(statusField)
                .asRequired("El estado es requerido")
                .withConverter(StatusTransformer::toDomain, StatusTransformer::toView)
                .bind(Contact::getActive, Contact::setActive);


        contentLayout.add(
                typeSelect,
                nameField,
                contactField,
                taxIdField,
                descriptionField,
                statusField
        );

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> this.close())
//                ComponentFactory.buildSaveDialogButton(event -> this.saveChanges())
        );

        add(contentLayout);
        add(footerLayout);


    }

    private void buildValidationSchema() {


    }
}
