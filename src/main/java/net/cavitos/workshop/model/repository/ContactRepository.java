package net.cavitos.workshop.model.repository;

import net.cavitos.workshop.model.entity.ContactEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContactRepository extends CrudRepository<ContactEntity, String>,
                                           PagingAndSortingRepository<ContactEntity, String> {

    Optional<ContactEntity> findByIdAndTenant(String id, String tenant);

    @Query("select contact " + 
           "from ContactEntity contact " +
           "where contact.tenant = :tenant " + 
           " and contact.active = :active " +
           " and contact.type like :type " + 
           " and (UPPER(contact.code) like UPPER(:text) " +
           "       or UPPER(contact.name) like UPPER(:text) " +
           "       or UPPER(contact.taxId) like UPPER(:text) " +
           "     )")
    Page<ContactEntity> search(@Param("tenant") String tenant, @Param("active") int active, @Param("type") String type,
                               @Param("text") String text, Pageable pageable);
}
