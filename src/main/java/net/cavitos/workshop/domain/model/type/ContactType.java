package net.cavitos.workshop.domain.model.type;

public enum ContactType {

    PROVIDER("P"),
    CUSTOMER("C"),
    UNKNOWN("U");

    private final String type;

    ContactType(String type) {
        this.type = type;
    }

    public String value() {

        return type;
    }

    public static ContactType of(final String value) {

        return switch (value) {
            case "C" -> CUSTOMER;
            case "P" -> PROVIDER;
            default -> UNKNOWN;
        };
    }
}
