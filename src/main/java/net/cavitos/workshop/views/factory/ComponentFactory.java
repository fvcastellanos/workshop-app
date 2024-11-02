package net.cavitos.workshop.views.factory;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import net.cavitos.workshop.views.model.Status;

public final class ComponentFactory {

    private ComponentFactory() {
    }

    public static TextField buildTextSearchField(final String width) {

        final var textField = new TextField();
        textField.setLabel("Texto");
        textField.setWidth(width);
        textField.setAutofocus(true);

        return textField;
    }

    public static Select<Status> buildStatusSelect(final String width, final Status defaultStatus) {

        final var active = new Status(1, "Activo");
        final var inactive = new Status(0, "Inactivo");

        final var select = new Select<Status>();
        select.setLabel("Activo");
        select.setWidth(width);
        select.setItemLabelGenerator(Status::getLabel);
        select.setItems(active, inactive);
        select.setValue(defaultStatus);

        return select;
    }

    public static Button buildCloseDialogButton(ComponentEventListener<ClickEvent<Button>> event) {

        return new Button("Cerrar", event);
    }

    public static Button buildSaveDialogButton(ComponentEventListener<ClickEvent<Button>> event) {

        final var button = new Button("Guardar Cambios", event);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return button;
    }

}
