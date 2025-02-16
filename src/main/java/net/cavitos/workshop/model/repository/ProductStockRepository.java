package net.cavitos.workshop.model.repository;

import net.cavitos.workshop.model.entity.ProductStockEntity;
import net.cavitos.workshop.model.entity.composite.CodeTenantId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface ProductStockRepository extends Repository<ProductStockEntity, CodeTenantId> {

    @Query("""
            select productStock
            from ProductStockEntity productStock
            where productStock.id.tenant = :tenant
            order by productStock.id.code
            """)
    List<ProductStockEntity> getProductStock(String tenant);
}
