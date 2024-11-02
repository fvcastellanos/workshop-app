package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.status.ActiveStatus;
import net.cavitos.workshop.domain.model.type.ContactType;
import net.cavitos.workshop.domain.model.web.Contact;
import net.cavitos.workshop.model.entity.ContactEntity;

public final class ContactTransformer {

    private ContactTransformer() {
    }

    public static Contact toWeb(final ContactEntity contactEntity) {

        final var active = ActiveStatus.of(contactEntity.getActive())
                .name();

        final var type = ContactType.of(contactEntity.getType())
                .name();

        final var provider = new Contact();
        provider.setCode(contactEntity.getCode());
        provider.setType(type);
        provider.setName(contactEntity.getName());
        provider.setDescription(contactEntity.getDescription());
        provider.setContact(contactEntity.getContact());
        provider.setTaxId(contactEntity.getTaxId());
        provider.setActive(active);

        return provider;
    }
}
