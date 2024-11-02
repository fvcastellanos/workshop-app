package net.cavitos.workshop.domain.model.status;

public enum ActiveStatus {

    ACTIVE(1),
    INACTIVE(0);

    private final int active;

    ActiveStatus(final int value) {

        this.active = value;
    }

    public int value() {

        return active;
    }

    public static ActiveStatus of(final int value) {

        return value == 1 ? ACTIVE
                : INACTIVE;
    }
}
