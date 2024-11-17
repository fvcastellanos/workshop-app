package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.Contact;
import net.cavitos.workshop.model.entity.ContactEntity;

public final class ContactTransformer {

    private ContactTransformer() {
    }

    public static Contact toWeb(final ContactEntity contactEntity) {

        final var contact = new Contact();
        contact.setCode(contactEntity.getCode());
        contact.setType(contactEntity.getType());
        contact.setName(contactEntity.getName());
        contact.setDescription(contactEntity.getDescription());
        contact.setContact(contactEntity.getContact());
        contact.setTaxId(contactEntity.getTaxId());
        contact.setActive(contactEntity.getActive());

        return contact;
    }
}
