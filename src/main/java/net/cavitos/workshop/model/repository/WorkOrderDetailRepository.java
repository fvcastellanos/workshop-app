package net.cavitos.workshop.model.repository;

import net.cavitos.workshop.model.entity.ProductEntity;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.model.entity.WorkOrderEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface WorkOrderDetailRepository extends CrudRepository<WorkOrderDetailEntity, String>,
                                                   PagingAndSortingRepository<WorkOrderDetailEntity, String> {

    Optional<WorkOrderDetailEntity> findByWorkOrderEntityAndProductEntityAndTenant(WorkOrderEntity workOrderEntity,
                                                                                   ProductEntity productEntity,
                                                                                   String tenant);

    @Query("""
                select orderDetail from WorkOrderDetailEntity orderDetail
                where orderDetail.workOrderEntity.id = :workOrderId
                    and orderDetail.productEntity.id = :productId
                    and tenant = :tenant
           """)
    Optional<WorkOrderDetailEntity> findDetailByWorkOrder(String workOrderId,
                                                          String productId,
                                                          String tenant);

    @Query("""
                select orderDetail from WorkOrderDetailEntity orderDetail
                where productEntity.id = :productId
                    and orderDetail.invoiceDetailEntity.id = :invoiceDetailId
                    and tenant = :tenant
           """)
    Optional<WorkOrderDetailEntity> findDetailByInvoiceDetail(String invoiceDetailId,
                                                              String productId,
                                                              String tenant);

    Optional<WorkOrderDetailEntity> findByIdAndTenant(String id, String tenant);

    @Query("""
               select orderDetail from WorkOrderDetailEntity orderDetail
               where orderDetail.workOrderEntity.id = :orderId
           """)
    List<WorkOrderDetailEntity> getOrderDetails(String orderId);

}
