package net.cavitos.workshop.model.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import net.cavitos.workshop.model.entity.ApplicationConfigurationEntity;

public interface ApplicationConfigurationRepository extends CrudRepository<ApplicationConfigurationEntity, String> {
    
    Optional<ApplicationConfigurationEntity> findByTenant(String tenant);
}
