package net.cavitos.workshop.domain.model.type;

public enum InventoryOperationType {

    INPUT("I"),
    OUTPUT("O"),
    UNKNOWN("U")
    ;

    private final String type;

    InventoryOperationType(final String type) {

        this.type = type;
    }

    public String type() {

        return type;
    }

    public static InventoryOperationType of(final String type) {

        return "I".equalsIgnoreCase(type) ? INPUT
                : OUTPUT;
    }
}
