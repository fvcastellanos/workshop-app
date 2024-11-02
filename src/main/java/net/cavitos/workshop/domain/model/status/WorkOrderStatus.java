package net.cavitos.workshop.domain.model.status;

public enum WorkOrderStatus {

    IN_PROGRESS("P"),
    CANCELLED("A"),
    CLOSED("C"),
    DELIVERED("D"),
    UNKNOWN("U");

    private final String status;

    WorkOrderStatus(final String status) {

        this.status = status;
    }

    public String value() {

        return status;
    }

    public static WorkOrderStatus of(final String value) {

        return switch (value) {
            case "P" -> IN_PROGRESS;
            case "A" -> CANCELLED;
            case "C" -> CLOSED;
            case "D" -> DELIVERED;
            default -> UNKNOWN;
        };
    }
}
