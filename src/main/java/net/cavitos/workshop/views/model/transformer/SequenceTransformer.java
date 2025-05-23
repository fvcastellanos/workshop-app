package net.cavitos.workshop.views.model.transformer;

import net.cavitos.workshop.domain.model.web.common.CommonSequence;
import net.cavitos.workshop.views.model.TypeOption;

public final class SequenceTransformer {

    private SequenceTransformer() {
    }

    public static CommonSequence toDomain(final TypeOption typeOption) {

        var sequence = new CommonSequence();
        sequence.setId(typeOption.getValue());
        sequence.setPrefix(typeOption.getLabel());

        return sequence;
    }

    public static TypeOption toView(final CommonSequence sequence) {

        return new TypeOption(sequence.getPrefix(), sequence.getId());
    }

}
