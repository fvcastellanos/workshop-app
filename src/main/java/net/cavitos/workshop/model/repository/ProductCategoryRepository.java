package net.cavitos.workshop.model.repository;

import net.cavitos.workshop.model.entity.ProductCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends CrudRepository<ProductCategoryEntity, String>,
        PagingAndSortingRepository<ProductCategoryEntity, String> {

    @Query("""
            select productCategory
                from ProductCategoryEntity productCategory
                where productCategory.tenant = :tenant
                    and productCategory.active = :active
                    and (
                        UPPER(productCategory.code) like UPPER(:text)
                        or UPPER(productCategory.name) like UPPER(:text)
                    )
                order by productCategory.code, productCategory.name
            """)
    Page<ProductCategoryEntity> search(String text, int active, String tenant, Pageable pageable);

    List<ProductCategoryEntity> findByTenantAndActive(String tenant, int active);

    Optional<ProductCategoryEntity> findByTenantAndName(String tenant, String name);

    Optional<ProductCategoryEntity> findByTenantAndCode(String tenant, String code);
}
