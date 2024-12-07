package net.cavitos.workshop.views.model.transformer;

import net.cavitos.workshop.views.model.TypeOption;

import java.util.List;

public final class TypeTransformer {

    private TypeTransformer() {
    }

    public static String toDomain(final TypeOption typeOption) {

        return typeOption.getValue();
    }

    public static TypeOption toDistanceMeasurementView(final String value) {

        return value.equalsIgnoreCase("K") ? new TypeOption("Kilómetros", "K") :
                new TypeOption("Millas", "M");
    }

    public static TypeOption toClientView(final String value) {

        return value.equalsIgnoreCase("C") ? new TypeOption("Cliente", "C") :
                new TypeOption("Proveedor", "P");
    }

    public static TypeOption toProductView(final String value) {

            return value.equalsIgnoreCase("P") ? new TypeOption("Producto", "P") :
                    new TypeOption("Servicio", "S");
    }

    public static TypeOption toInventoryMovementTypeView(final String value) {

        return value.equalsIgnoreCase("I") ? new TypeOption("Entrada", "I") :
                new TypeOption("Salida", "O");
    }

    public static TypeOption toWorkOrderStatusView(final String value) {

        return switch (value) {
            case "P" -> new TypeOption("En Proceso", "P");
            case "A" -> new TypeOption("Cancelada", "A");
            case "C" -> new TypeOption("Cerrada", "C");
            default -> new TypeOption("Entregada", "D");
        };
    }

    public static TypeOption toInvoiceView(final String value) {

        return switch (value) {
            case "A" -> new TypeOption("Activa", "A");
            case "C" -> new TypeOption("Cancelada", "C");
            default -> new TypeOption("Anulada", "D");
        };
    }

    public static TypeOption toInvoiceType(final String value) {

        return value.equalsIgnoreCase("P") ? new TypeOption("Proveedor", "P") :
                new TypeOption("Cliente", "C");
    }
}
