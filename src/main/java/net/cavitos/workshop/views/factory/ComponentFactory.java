package net.cavitos.workshop.views.factory;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.cavitos.workshop.views.model.Status;
import net.cavitos.workshop.views.model.TypeOption;
import org.apache.commons.collections4.ListUtils;

import java.util.List;

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

    public static Select<TypeOption> buildTypeSelect(final String width,
                                                     final String label,
                                                     final List<TypeOption> options,
                                                     final String selectedValue) {

        final var defaultOption = ListUtils.emptyIfNull(options).stream()
                .filter(option -> option.getValue().equalsIgnoreCase(selectedValue))
                .findFirst()
                .orElse(new TypeOption("", "Seleccione una opción"));

        final var select = new Select<TypeOption>();
        select.setLabel(label);
        select.setWidth(width);
        select.setItemLabelGenerator(TypeOption::getLabel);
        select.setItems(options);
        select.setValue(defaultOption);

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

    public static Button buildRedirectButton(String text, String route) {

        final var btnNavigate = new Button(text, buttonClickEvent -> {
            UI.getCurrent().navigate(route);
        });

        btnNavigate.setWidth("min-content");
        btnNavigate.setIcon(new SvgIcon("img/icons/left-arrow-svgrepo-com.svg"));

        return btnNavigate;
    }

    public static VerticalLayout buildSearchBox() {

        final var searchBox = new VerticalLayout();
        searchBox.setClassName("border");
        searchBox.setWidthFull();
        searchBox.setWidth("100%");
        searchBox.getStyle().set("flex-grow", "1");

        return searchBox;
    }

    public static HorizontalLayout buildSearchBody() {

        final var searchBody = new HorizontalLayout();
        searchBody.setWidth("100%");
        searchBody.setHeightFull();
        searchBody.addClassName(LumoUtility.Gap.MEDIUM);
        searchBody.getStyle().set("flex-grow", "1");

        return searchBody;
    }

    public static HorizontalLayout buildSearchFooter() {

        final var searchFooter = new HorizontalLayout();
        searchFooter.setWidthFull();
        searchFooter.addClassName(LumoUtility.Gap.MEDIUM);
        searchFooter.setWidth("100%");
        searchFooter.getStyle().set("flex-grow", "1");

        return searchFooter;
    }

    public static H3 buildSearchTitle(final String title) {

        final var searchTitle = new H3();
        searchTitle.setText(title);
        searchTitle.setWidth("max-content");

        return searchTitle;
    }

    public static <T> Grid<T> buildGrid(Class<T> beanType) {

        final var grid = new Grid<>(beanType, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("100%");
        grid.getStyle().set("flex-grow", "0");
        grid.addClassName("grid-border");

        return grid;
    }
}
