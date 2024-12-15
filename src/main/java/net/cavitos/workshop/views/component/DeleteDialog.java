package net.cavitos.workshop.views.component;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import net.cavitos.workshop.views.factory.ComponentFactory;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public abstract class DeleteDialog<T> extends Dialog {

    protected Consumer<T> onDeleteEvent;

    private final Text dialogText;

    private T entity;
    private String tenant;

    public DeleteDialog() {
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        dialogText = new Text("");

        buildContent();
    }

    public void openDialog(final String tenant, final T entity) {

        this.entity = entity;
        this.tenant = tenant;
        dialogText.setText(getEntityName());
        open();
    }

    public void addOnDeleteEvent(final Consumer<T> onDeleteEvent) {
        this.onDeleteEvent = onDeleteEvent;
    }

    protected T getEntity() {

        return entity;
    }

    protected String getTenant() {

        return tenant;
    }

    protected abstract String getEntityName();

    protected abstract void deleteEntity(final T entity);

    private void buildContent() {

        setHeaderTitle("Eliminar");
        setWidth("40%");

        final var icon = new Icon(VaadinIcon.WARNING);
        icon.setSize("4em");
        icon.setColor("red");

        final var leftSpan = new Span(icon);
        leftSpan.setWidth("20%");
        leftSpan.getStyle().setTextAlign(Style.TextAlign.CENTER);

        final var rightSpan = new Span(dialogText);
        rightSpan.setWidth("80%");
        rightSpan.getStyle().setTextAlign(Style.TextAlign.CENTER);
        rightSpan.getStyle().set("display", "flex");
        rightSpan.getStyle().set("align-items", "center");

        final var content = new HorizontalLayout(
                leftSpan,
                rightSpan
        );

        final var contentLayout = new VerticalLayout(content);

        final var deleteButton = new Button("Eliminar", event -> {

            deleteEntity(entity);

            if (nonNull(onDeleteEvent)) {
                onDeleteEvent.accept(entity);
            }

            close();
        });

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> close()),
                deleteButton
        );

        add(contentLayout, footerLayout);
    }
}
