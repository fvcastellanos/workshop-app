package net.cavitos.workshop.views.model.transformer;

import static java.util.Objects.isNull;

public final class OdometerValueTransformer {

    private OdometerValueTransformer() {
    }

    public static double toDomain(final Integer value) {

        if (isNull(value)) {

            return 0D;
        }

        return value.doubleValue();
    }

    public static int toView(final Double value) {

        if (isNull(value)) {

            return 0;
        }

        return value.intValue();
    }
}
