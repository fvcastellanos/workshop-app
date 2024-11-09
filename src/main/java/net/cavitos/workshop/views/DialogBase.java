package net.cavitos.workshop.views;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import net.cavitos.workshop.model.entity.CarBrandEntity;

import java.util.function.Consumer;

public abstract class DialogBase<T> extends Dialog {

    protected boolean isEdit;
    protected String tenant;
    protected Consumer<T> onSaveEvent;

    protected DialogBase() {
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
    }

    public void addOnSaveEvent(final Consumer<T> onSaveEvent) {

        this.onSaveEvent = onSaveEvent;
    }

    public void openDialogForEdit(final String tenant, final T entity) {

        openDialog(true, tenant, entity);
    }

    public void openDialogForNew(final String tenant) {

        openDialog(false, tenant, null);
    }

    protected abstract void openDialog(final boolean isEdit, final String tenant, final T entity);

    protected void showErrorNotification(String message) {
        final var notification = new Notification(message, 5000);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();
    }

}
