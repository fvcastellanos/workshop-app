package net.cavitos.workshop.model.repository;

import net.cavitos.workshop.model.entity.ProductStockEntity;
import net.cavitos.workshop.model.entity.composite.CodeTenantId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface ProductStockRepository extends Repository<ProductStockEntity, CodeTenantId> {

    @Query("""
            select productStock
            from ProductStockEntity productStock
            where productStock.id.tenant = :tenant
                and productStock.productEntity.productCategoryEntity.id like :category
                and (UPPER(productStock.productEntity.code) like UPPER(:text) or UPPER(productStock.productEntity.name) like UPPER(:text))
            order by productStock.id.code
            """)
    Page<ProductStockEntity> getProductStock(String text, String category, String tenant, Pageable pageable);
}
