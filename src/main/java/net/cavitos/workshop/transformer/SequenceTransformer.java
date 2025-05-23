package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.Sequence;
import net.cavitos.workshop.sequence.model.entity.SequenceEntity;

public final class SequenceTransformer {
    
    private SequenceTransformer() {
        // Utility class
    }

    public static Sequence toWeb(final SequenceEntity entity) {

        if (entity == null) {
            return null;
        }
        
        final var sequence = new Sequence();
        sequence.setId(entity.getId());
        sequence.setPrefix(entity.getPrefix());
        sequence.setPadSize(entity.getPadSize());
        sequence.setStepSize(entity.getStepSize());
        sequence.setDescription(entity.getDescription());

        return sequence;
    }
}
