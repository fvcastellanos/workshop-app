package net.cavitos.workshop.model.repository;

import net.cavitos.workshop.model.entity.ApplicationUserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ApplicationUserRepository extends CrudRepository<ApplicationUserEntity, String>,
                                        PagingAndSortingRepository<ApplicationUserEntity, String> {

    Optional<ApplicationUserEntity> findByUserIdAndProvider(String userId, String provider);
}
