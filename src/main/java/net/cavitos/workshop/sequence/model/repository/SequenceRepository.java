package net.cavitos.workshop.sequence.model.repository;

import net.cavitos.workshop.sequence.model.entity.SequenceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface SequenceRepository extends CrudRepository<SequenceEntity, String>,
        PagingAndSortingRepository<SequenceEntity, String> {

    Optional<SequenceEntity> findByPrefixAndTenant(String prefix, String tenant);

    @Query("""
           select sequence
              from SequenceEntity sequence
              where sequence.tenant = :tenant
                and (UPPER(sequence.prefix) like UPPER(:text) or UPPER(sequence.description) like UPPER(:text))
              order by sequence.prefix, sequence.tenant
           """)
    Page<SequenceEntity> search(String text, String tenant, Pageable pageable);

    Optional<SequenceEntity> findByIdAndTenant(String id, String tenant);

    List<SequenceEntity> findByTenant(String tenant);
}
