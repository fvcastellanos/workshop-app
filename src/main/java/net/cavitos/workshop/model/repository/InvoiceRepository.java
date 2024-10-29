package net.cavitos.workshop.model.repository;

import net.cavitos.workshop.model.entity.InvoiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface InvoiceRepository extends CrudRepository<InvoiceEntity, String>,
                                           PagingAndSortingRepository<InvoiceEntity, String> {

    @Query("""
            select invoice from InvoiceEntity invoice
            where invoice.tenant = :tenant
                and invoice.status = :status
                and invoice.type = :type
                and (
                        UPPER(invoice.contactEntity.name) like UPPER(:text)
                            or UPPER(invoice.contactEntity.taxId) like UPPER(:text)
                            or UPPER(invoice.number) like UPPER(:text)
                    )
           """)
    Page<InvoiceEntity> search(String text,
                               String type,
                               String status,
                               String tenant,
                               Pageable pageable);

    Optional<InvoiceEntity> findByIdAndTenant(String id, String tenant);

    Optional<InvoiceEntity> findBySuffixEqualsIgnoreCaseAndNumberEqualsIgnoreCaseAndContactEntityCodeEqualsIgnoreCaseAndTenant(String suffix,
                                                                                                                               String number,
                                                                                                                               String contact,
                                                                                                                               String tenant);
}
