package net.cavitos.workshop.model.repository;

import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceDetailRepository extends CrudRepository<InvoiceDetailEntity, String>,
                                                 PagingAndSortingRepository<InvoiceDetailEntity, String> {

    Optional<InvoiceDetailEntity> findByInvoiceEntityIdAndProductEntityIdAndTenant(String invoiceId,
                                                                                   String productId,
                                                                                   String tenant);

    Optional<InvoiceDetailEntity> findByIdAndTenant(String id, String tenant);

    @Query(
            """
                select invoiceDetail
                from InvoiceDetailEntity invoiceDetail
                where invoiceDetail.invoiceEntity.id = :invoiceId
            """
    )
    List<InvoiceDetailEntity> getInvoiceDetails(String invoiceId);
}
