package net.cavitos.workshop.views.contact;

import net.cavitos.workshop.model.entity.ContactEntity;
import net.cavitos.workshop.transformer.ContactTransformer;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.model.transformer.StatusTransformer;

public class AddModal extends DialogBase<ContactEntity> {

    private ContactEntity contactEntity;

    @Override
    protected void openDialog(boolean isEdit, String tenant, ContactEntity entity) {

        this.isEdit = isEdit;
        this.setHeaderTitle(isEdit ? "Modificar Contacto" : "Agregar Contacto");
        this.tenant = tenant;

//        binder.refreshFields();
//
//        statusField.setValue(StatusTransformer.toView(1)); // Active status
//        statusField.setReadOnly(!isEdit);

        if (isEdit) {
            contactEntity = entity;
//            binder.readBean(ContactTransformer.toWeb(contactEntity));
        }

        this.open();
    }
}
