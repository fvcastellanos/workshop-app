package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.model.web.Contact;
import net.cavitos.workshop.model.entity.ContactEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.ContactRepository;
import net.cavitos.workshop.sequence.domain.SequenceType;
import net.cavitos.workshop.sequence.provider.SequenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static net.cavitos.workshop.domain.model.status.ActiveStatus.ACTIVE;
import static net.cavitos.workshop.factory.BusinessExceptionFactory.createBusinessException;

@Service
public class ContactService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactService.class);

    private final ContactRepository contactRepository;
    private final SequenceProvider sequenceProvider;

    public ContactService(final ContactRepository contactRepository,
                          final SequenceProvider sequenceProvider) {

        this.contactRepository = contactRepository;
        this.sequenceProvider = sequenceProvider;
    }

    public Page<ContactEntity> search(final String tenant,
                                      final String type,
                                      final int active,
                                      final String text,
                                      final int page,
                                      final int size) {

        LOGGER.info("Retrieve all contacts for tenant={} with text={}, type={}, active={}", tenant, text, type, active);

        final var pageable = PageRequest.of(page, size);
        return contactRepository.search(tenant, active, type, "%" + text + "%", pageable);
    }

    public ContactEntity getById(final String tenant, final String id) {

        LOGGER.info("Retrieve contact_id={} for tenant={}", id, tenant);

        final var entity = contactRepository.findById(id)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Contact not found"));

        if (!entity.getTenant().equalsIgnoreCase(tenant)) {

            LOGGER.error("provider_id={} is not associated to tenant={}", id, tenant);
            throw createBusinessException(HttpStatus.NOT_FOUND, "Provider not found");
        }

        return entity;
    }

    public ContactEntity add(final String tenant, final Contact contact) {

        LOGGER.info("Trying to add a new contact with name={}, type={} for tenant={}", contact.getName(), contact.getType(), tenant);

        verifyExistingCodeTypeForTenant(tenant, contact);

        final var providerEntity = ContactEntity.builder()
                .id(TimeBasedGenerator.generateTimeBasedId())
                .code(calculateCode(contact.getType()))
                .type(contact.getType())
                .name(contact.getName())
                .description(contact.getDescription())
                .taxId(contact.getTaxId())
                .contact(contact.getContact())
                .tenant(tenant)
                .active(ACTIVE.value())
                .created(Instant.now())
                .updated(Instant.now())
                .build();

        contactRepository.save(providerEntity);

        return providerEntity;
    }

    public ContactEntity update(final String tenant, final String id, final Contact contact) {

        LOGGER.info("trying to update contact_id={} for tenant={}", id, tenant);

        final var contactEntity = contactRepository.findById(id)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Contact not found"));

        if (!contactEntity.getTenant().equalsIgnoreCase(tenant)) {

            LOGGER.error("contact_id={} is not assigned to tenant={}", id, tenant);
            throw createBusinessException(HttpStatus.NOT_FOUND, "Contact not found");
        }

        final var type = contact.getType();

        var code = contactEntity.getCode();
        if (!type.equalsIgnoreCase(contactEntity.getType())) {

            code = calculateCode(contact.getType());
        }

        contactEntity.setName(contact.getName());
        contactEntity.setType(contact.getType());
        contactEntity.setCode(code);
        contactEntity.setDescription(contact.getDescription());
        contactEntity.setContact(contact.getContact());
        contactEntity.setTaxId(contact.getTaxId());
        contactEntity.setActive(contact.getActive());
        contactEntity.setUpdated(Instant.now());

        contactRepository.save(contactEntity);

        return contactEntity;
    }

    // ----------------------------------------------------------------------------------------------------

    private void verifyExistingCodeTypeForTenant(final String tenant, final Contact contact) {

        final var existingContactHolder = contactRepository.findByIdAndTenant(contact.getCode(),
                tenant);

        if (existingContactHolder.isPresent()) {

            LOGGER.error("contact_code={} and type={} already exists for tenant={}", contact.getCode(), contact.getType(), tenant);
            throw createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Another contact is using the code=%s for type=%s",
                    contact.getCode(), contact.getType());
        }
    }

    private String calculateCode(final String type) {

        return switch (type) {
            case "C" -> sequenceProvider.calculateNext(SequenceType.CUSTOMER);
            case "P" -> sequenceProvider.calculateNext(SequenceType.PROVIDER);
            default -> sequenceProvider.calculateNext(SequenceType.UNKNOWN);
        };
    }
}
