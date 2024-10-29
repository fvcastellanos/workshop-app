package net.cavitos.workshop.model.repository;

import net.cavitos.workshop.model.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<ProductEntity, String>,
                                           PagingAndSortingRepository<ProductEntity, String> {

    @Query("""
                select product
                from ProductEntity product
                where
                    product.tenant = :tenant
                    and product.active = :active
                    and product.type like :type
                    and product.productCategoryEntity.id like :category
                    and (UPPER(product.code) like UPPER(:text) or UPPER(product.name) like UPPER(:text))
            """)
    Page<ProductEntity> search(String text,
                               String type,
                               String category,
                               int active,
                               String tenant,
                               Pageable pageable);

    Optional<ProductEntity> findByCodeEqualsIgnoreCaseAndTenant(String code, String tenant);
}
