package net.cavitos.workshop.views;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import net.cavitos.workshop.model.entity.CarBrandEntity;

import java.util.function.Consumer;

public abstract class DialogBase<T> extends Dialog {

    protected boolean isEdit;
    protected Consumer<T> onSaveEvent;

    protected DialogBase() {
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
    }

    public void addOnSaveEvent(final Consumer<T> onSaveEvent) {

        this.onSaveEvent = onSaveEvent;
    }

    public void openDialogForEdit(final T entity) {

        openDialog(true, entity);
    }

    public void openDialogForNew() {

        openDialog(false, null);
    }

    protected abstract void openDialog(final boolean isEdit, final T entity);

    protected void showErrorNotification(String message) {
        final var notification = new Notification(message, 3000);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();
    }

}
