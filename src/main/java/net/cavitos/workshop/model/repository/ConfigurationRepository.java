package net.cavitos.workshop.model.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import net.cavitos.workshop.model.entity.ConfigurationEntity;

public interface ConfigurationRepository extends CrudRepository<ConfigurationEntity, String> {
    
    List<ConfigurationEntity> findAllByTenant(String tenant);
}
