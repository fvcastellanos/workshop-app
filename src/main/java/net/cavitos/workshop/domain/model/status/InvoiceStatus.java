package net.cavitos.workshop.domain.model.status;

public enum InvoiceStatus {

    ACTIVE("A"),
    CLOSED("C"),
    CANCELLED("D"),
    UNKNOWN("U");

    private final String value;

    InvoiceStatus(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static InvoiceStatus of(final String value) {

        return switch (value) {
            case "A" -> ACTIVE;
            case "C" -> CLOSED;
            case "D" -> CANCELLED;
            default -> UNKNOWN;
        };
    }
}
