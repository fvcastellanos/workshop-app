package net.cavitos.workshop.model.repository;

import net.cavitos.workshop.model.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, String>,
                                        PagingAndSortingRepository<UserEntity, String> {

    Optional<UserEntity> findByUserIdAndProvider(String userId, String provider);
}
