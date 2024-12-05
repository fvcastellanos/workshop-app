package net.cavitos.workshop.views.model.transformer;

import net.cavitos.workshop.views.model.TypeOption;

public final class FuelLevelTransformer {

    private FuelLevelTransformer() {
    }

    public static double toDomain(final TypeOption typeOption) {

        final var value = typeOption.getValue();
        return Double.parseDouble(value);
    }

    public static TypeOption toView(final double fuelLevel) {

        if (fuelLevel == 0D) {
            return new TypeOption("Vacío", "0");
        }

        if (fuelLevel == 0.25D) {
            return new TypeOption("1/4", "0.25");
        }

        if (fuelLevel == 0.5D) {
            return new TypeOption("1/2", "0.5");
        }

        if (fuelLevel == 0.75D) {
            return new TypeOption("3/4", "0.75");
        }

        return new TypeOption("Lleno", "1");
    }
}
