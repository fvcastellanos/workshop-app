package net.cavitos.workshop.views.contact;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import net.cavitos.workshop.model.entity.CarLineEntity;
import net.cavitos.workshop.model.entity.ContactEntity;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.layouts.CRUDLayout;
import net.cavitos.workshop.views.layouts.MainLayout;
import net.cavitos.workshop.views.model.Status;
import net.cavitos.workshop.views.model.transformer.StatusTransformer;

import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchBody;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchFooter;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchTitle;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildStatusSelect;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildTextSearchField;

@PageTitle("Contactos")
@RolesAllowed({ "ROLE_user" })
@Route(value = "contacts", layout = MainLayout.class)
public class ContactView extends CRUDLayout {

    private TextField searchText;
    private Select<Status> searchStatus;
//    private final Grid<ContactEntity> grid;

    public ContactView() {
        super();

        final var btnSearch = new Button("Buscar", event -> {

//            performSearch();
        });
        btnSearch.setWidth("min-content");

        final var btnAdd = new Button("Agregar Contacto", event -> {

//            addModelDialog.openDialogForNew();
        });
        btnAdd.setWidth("min-content");
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        final var searchBody = buildSearchBody();
        searchBody.add(searchText);
        searchBody.add(searchStatus);

        final var searchFooter = buildSearchFooter();
        searchFooter.add(btnSearch);
        searchFooter.add(btnAdd);

        final var searchBox = ComponentFactory.buildSearchBox();
        searchBox.add(buildSearchBody());
//        searchBox.add(searchBody);
        searchBox.add(searchFooter);

        add(buildSearchTitle("Búsqueda"));
        add(searchBox);
    }

    private VerticalLayout buildSearchBody() {

        searchText = buildTextSearchField("100%");

        searchStatus = buildStatusSelect("20%", StatusTransformer.toView(1));
        searchStatus.setWidth("50%");

//        var type = new Select<Contact>()
        
        final var row1 = ComponentFactory.buildSearchBody();
        row1.add(searchText);

        final var row2 = ComponentFactory.buildSearchBody();
        row2.add(searchStatus);

        return  new VerticalLayout(row1, row2);
    }
}
