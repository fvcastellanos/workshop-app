package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.model.web.Invoice;
import net.cavitos.workshop.domain.model.web.common.CommonContact;
import net.cavitos.workshop.model.entity.ContactEntity;
import net.cavitos.workshop.model.entity.InvoiceEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.ContactRepository;
import net.cavitos.workshop.model.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static net.cavitos.workshop.factory.BusinessExceptionFactory.createBusinessException;
import static net.cavitos.workshop.factory.DateTimeFactory.buildInstantFrom;
import static net.cavitos.workshop.factory.DateTimeFactory.getUTCNow;

@Service
public class InvoiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceService.class);

    private final InvoiceRepository invoiceRepository;
    private final ContactRepository contactRepository;

    public InvoiceService(final InvoiceRepository invoiceRepository,
                          final ContactRepository contactRepository) {

        this.invoiceRepository = invoiceRepository;
        this.contactRepository = contactRepository;
    }

    public Page<InvoiceEntity> search(final String tenant,
                                      final String type,
                                      final String status,
                                      final String text,
                                      final int page,
                                      final int size) {

        LOGGER.info("Search invoices with type={}, status={}, text={} for tenant={}",
                type, status, text, tenant);

        final var pageable = PageRequest.of(page, size);

        return invoiceRepository.search("%" + text + "%", type, status, tenant, pageable);
    }

    public InvoiceEntity findById(final String tenant, final String id) {

        LOGGER.info("Retrieve invoice_id={} for tenant={}", id, tenant);

        return invoiceRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    public InvoiceEntity add(final String tenant, final Invoice invoice) {

        LOGGER.info("Add new Invoice={} for tenant={}", invoice, tenant);

        final var contact = invoice.getContact();

        verifyExistingInvoice(tenant, invoice);

        final var contactEntity = findContactEntity(tenant, contact);

        final var invoiceDate = buildInstantFrom(invoice.getInvoiceDate());
        final var effectiveDate = buildInstantFrom(invoice.getEffectiveDate());

        var entity = InvoiceEntity.builder()
                .id(TimeBasedGenerator.generateTimeBasedId())
                .contactEntity(contactEntity)
                .suffix(invoice.getSuffix())
                .number(invoice.getNumber())
                .imageUrl(invoice.getImageUrl())
                .type(invoice.getType())
                .status(invoice.getStatus())
                .tenant(tenant)
                .invoiceDate(invoiceDate)
                .effectiveDate(effectiveDate)
                .created(getUTCNow())
                .updated(getUTCNow())
                .build();

        invoiceRepository.save(entity);

        return entity;
    }

    public InvoiceEntity update(final String tenant, final String id, final Invoice invoice) {

        LOGGER.info("Update invoice={} for tenant={}", invoice, tenant);

        final var entity = findById(tenant, id);

        var contactEntity = entity.getContactEntity();
        final var contact = invoice.getContact();

        if (!contactEntity.getCode().equalsIgnoreCase(contact.getId())) {

            contactEntity = findContactEntity(tenant, contact);
        }

        if (!entity.getNumber().equalsIgnoreCase(invoice.getNumber())) {

            verifyExistingInvoice(tenant, invoice);
        }

        entity.setNumber(invoice.getNumber());
        entity.setSuffix(invoice.getSuffix());
        entity.setInvoiceDate(buildInstantFrom(invoice.getInvoiceDate()));
        entity.setEffectiveDate(buildInstantFrom(invoice.getEffectiveDate()));
        entity.setImageUrl(invoice.getImageUrl());
        entity.setStatus(invoice.getStatus());
        entity.setType(invoice.getType());
        entity.setContactEntity(contactEntity);
        entity.setUpdated(getUTCNow());

        invoiceRepository.save(entity);

        return entity;
    }

    // ------------------------------------------------------------------------------------------------------
    private ContactEntity findContactEntity(final String tenant, final CommonContact contact) {

        return contactRepository.findByIdAndTenant(contact.getId(), tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Contact not found"));
    }

    private void verifyExistingInvoice(final String tenant, final Invoice invoice) {

        final var contact = invoice.getContact();
        final var existingInvoiceHolder = invoiceRepository.findBySuffixEqualsIgnoreCaseAndNumberEqualsIgnoreCaseAndContactEntityCodeEqualsIgnoreCaseAndTenant(invoice.getSuffix(),
                invoice.getNumber(), contact.getId(), tenant);

        if (existingInvoiceHolder.isPresent()) {

            LOGGER.error("invoice_suffix={}, invoice_number={} already exists for tenant={}", invoice.getSuffix(),
                    invoice.getNumber(), tenant);

            throw createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Invoice already exists");
        }
    }
}
