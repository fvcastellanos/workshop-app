package net.cavitos.workshop.model.repository;

import net.cavitos.workshop.model.entity.CarBrandEntity;
import net.cavitos.workshop.model.entity.CarLineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CarLineRepository extends CrudRepository<CarLineEntity, String>,
                                           PagingAndSortingRepository<CarLineEntity, String> {

    Page<CarLineEntity> findByCarBrandAndTenantAndActiveAndNameContainsIgnoreCase(CarBrandEntity carBrandEntity,
                                                                                  String tenant,
                                                                                  int active,
                                                                                  String name,
                                                                                  Pageable pageable);

    @Query("select carLine from CarLineEntity carLine where carLine.tenant = :tenant and carLine.active = :active " +
            "and upper(carLine.name) like upper(:text)")
    Page<CarLineEntity> search(String tenant,
                               String text,
                               int active,
                               Pageable pageable);

    Optional<CarLineEntity> findByCarBrandAndNameAndTenant(CarBrandEntity carBrandEntity, String name, String tenant);

    Optional<CarLineEntity> findByIdAndTenant(String id, String tenant);
}
