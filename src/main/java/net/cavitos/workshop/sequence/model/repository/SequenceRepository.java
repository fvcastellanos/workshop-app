package net.cavitos.workshop.sequence.model.repository;

import net.cavitos.workshop.sequence.model.entity.SequenceEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SequenceRepository extends CrudRepository<SequenceEntity, Long> {

    Optional<SequenceEntity> findByPrefix(String prefix);
}
