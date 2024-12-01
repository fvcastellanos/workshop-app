package net.cavitos.workshop.views.model.transformer;

import net.cavitos.workshop.domain.model.web.common.CommonProductCategory;
import net.cavitos.workshop.views.model.TypeOption;

public final class TypeTransformer {

    private TypeTransformer() {
    }

    public static String toDomain(final TypeOption typeOption) {

        return typeOption.getValue();
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
}
