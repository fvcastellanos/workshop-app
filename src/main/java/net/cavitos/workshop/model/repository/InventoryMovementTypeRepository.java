package net.cavitos.workshop.model.repository;

import net.cavitos.workshop.model.entity.InventoryMovementTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InventoryMovementTypeRepository extends CrudRepository<InventoryMovementTypeEntity, String> {

    @Query(
        """
            select movementType from InventoryMovementTypeEntity movementType
            where movementType.tenant = :tenant
                and movementType.active = :active
                and UPPER(movementType.type) like UPPER(:type)
                and (
                        UPPER(movementType.name) like UPPER(:text)
                        or UPPER(movementType.code) like UPPER(:text)
                    )
        """
    )
    Page<InventoryMovementTypeEntity> search(int active,
                                             String type,
                                             String text,
                                             String tenant,
                                             Pageable pageable);

    Optional<InventoryMovementTypeEntity> findByIdAndTenant(String id, String tenant);

    Optional<InventoryMovementTypeEntity> findByNameAndTenant(String name, String tenant);

    Optional<InventoryMovementTypeEntity> findByCodeAndTenant(String code, String tenant);
}
