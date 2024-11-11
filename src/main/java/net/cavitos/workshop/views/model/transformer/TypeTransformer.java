package net.cavitos.workshop.views.model.transformer;

import net.cavitos.workshop.views.model.TypeOption;

public final class TypeTransformer {

    private TypeTransformer() {
    }

    public static String toDomain(final TypeOption typeOption) {

        return typeOption.getValue();
    }

    public static TypeOption toView(final String value) {

//        return new ;

        return null;
    }
}
