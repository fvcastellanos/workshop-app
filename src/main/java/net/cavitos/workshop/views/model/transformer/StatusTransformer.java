package net.cavitos.workshop.views.model.transformer;

import net.cavitos.workshop.views.model.Status;

public final class StatusTransformer {

    private StatusTransformer() {
    }

    public static Status toView(final int value) {

        return value == 1 ? new Status(1, "Activo") : new Status(0, "Inactivo");
    }

    public static int toDomain(final Status status) {

        return status.getValue();
    }

}
