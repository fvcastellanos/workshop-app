package net.cavitos.workshop.model.repository;

import net.cavitos.workshop.model.entity.WorkOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface WorkOrderRepository extends CrudRepository<WorkOrderEntity, String>,
                                             PagingAndSortingRepository<WorkOrderEntity, String> {

    @Query("select workOrder from WorkOrderEntity workOrder where workOrder.tenant = :tenant and workOrder.status like :status " +
            "and (UPPER(workOrder.number) like UPPER(:text) or UPPER(workOrder.plateNumber) like UPPER(:text))")
    Page<WorkOrderEntity> search(String tenant, String status, String text, Pageable pageable);

    Optional<WorkOrderEntity> findByNumberEqualsIgnoreCaseAndTenant(String number, String tenant);

    Optional<WorkOrderEntity> findByIdAndTenant(String id, String tenant);
}
