package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.Invoice;
import net.cavitos.workshop.domain.model.web.common.CommonContact;
import net.cavitos.workshop.model.entity.ContactEntity;
import net.cavitos.workshop.model.entity.InvoiceEntity;

import static java.util.Objects.nonNull;
import static net.cavitos.workshop.factory.DateTimeFactory.buildStringFromInstant;

public final class InvoiceTransformer {

    private InvoiceTransformer() {
    }

    public static Invoice toWeb(final InvoiceEntity entity) {

        final var invoice = new Invoice();
        invoice.setType(entity.getType());
        invoice.setSuffix(entity.getSuffix());
        invoice.setNumber(entity.getNumber());
        invoice.setStatus(entity.getStatus());
        invoice.setImageUrl(entity.getImageUrl());
        invoice.setInvoiceDate(buildStringFromInstant(entity.getInvoiceDate()));
        invoice.setEffectiveDate(buildStringFromInstant(entity.getEffectiveDate()));

        if (nonNull(entity.getContactEntity())) {

            invoice.setContact(buildCommonContact(entity.getContactEntity()));
        }

        return  invoice;
    }

    // ----------------------------------------------------------------------------------------------------

    private static CommonContact buildCommonContact(final ContactEntity entity) {

        final var contact = new CommonContact();
        contact.setType(entity.getType());
        contact.setId(entity.getCode());
        contact.setName(entity.getName());
        contact.setTaxId(entity.getTaxId());

        return contact;
    }
}
