package net.cavitos.workshop.views.layouts;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class CRUDLayout extends VerticalLayout {

    protected static final int DEFAULT_PAGE = 0;
    protected static final int DEFAULT_SIZE = 1000;

    protected void showErrorNotification(String message) {
        final var notification = new Notification(message, 3000);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();
    }
}
