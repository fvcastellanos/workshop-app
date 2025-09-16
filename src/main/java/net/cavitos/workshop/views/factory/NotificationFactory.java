package net.cavitos.workshop.views.factory;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public final class NotificationFactory {

    private NotificationFactory() {
    }

    public static void showNotification(final String message, final int duration, final NotificationVariant notificationVariant) {

        final var notification = new Notification();
        notification.addThemeVariants(notificationVariant);
        notification.setDuration(duration);
        notification.setPosition(Notification.Position.TOP_STRETCH);

        final var div = new Div(new Text(message));

        final var closeButton = new Button(VaadinIcon.CLOSE.create(), event -> notification.close());

        final var horizontalLayout = new HorizontalLayout(closeButton, div);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(horizontalLayout);

        notification.open();
    }

    public static void showPersistentNotification(final String message, final NotificationVariant notificationVariant) {
        showNotification(message, 0, notificationVariant);
    }
}
