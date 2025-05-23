package net.cavitos.workshop.views.sequences;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.sequence.model.entity.SequenceEntity;
import net.cavitos.workshop.sequence.service.SequenceService;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.layouts.CRUDLayout;
import net.cavitos.workshop.views.layouts.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

@PageTitle("Secuencias")
@RolesAllowed({ "ROLE_admin" })
@Route(value = "sequences", layout = MainLayout.class)
public class SequenceView extends CRUDLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceView.class);

    private final SequenceService sequenceService;

    private TextField searchTextField;

    private SequenceModalView modalView;

    private Grid<SequenceEntity> grid;


    public SequenceView(final AuthenticationContext authenticationContext,
                        final DatabaseUserService databaseUserService,
                        final SequenceService sequenceService,
                        final SequenceModalView modalView) {
        super(authenticationContext, databaseUserService);

        this.sequenceService = sequenceService;
        this.modalView = modalView;

        this.grid = buildGrid();

        add(
                ComponentFactory.buildSearchTitle("Búsqueda"),
                buildSearchBox(),
                grid
        );

        modalView.addOnSaveEvent(entity -> search());

        search();
    }

    @Override
    protected Page<SequenceEntity> performSearch() {

        final var text = searchTextField.getValue();

        final var result = sequenceService.search(text, getUserTenant(), DEFAULT_PAGE, DEFAULT_SIZE);

        grid.setItems(result.getContent());

        return result;
    }

    private VerticalLayout buildSearchBox() {

        final var searchBox = ComponentFactory.buildSearchBox();

        searchBox.add(
                buildSearchBody(),
                buildSearchFooter()
        );

        return searchBox;
    }

    private HorizontalLayout buildSearchFooter() {

        final var searchButton = new Button("Buscar", event -> performSearch());
        searchButton.setWidth("min-content");

        final var addButton = new Button("Agregar Secuencia", event -> {});
        addButton.setWidth("min-content");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(event -> modalView.openDialogForNew(tenant));

        final var searchFooter = ComponentFactory.buildSearchFooter();
        searchFooter.add(
                searchButton,
                addButton
        );

        return searchFooter;
    }

    private HorizontalLayout buildSearchBody() {

        searchTextField = ComponentFactory.buildTextSearchField("100%");

        final var searchBody = ComponentFactory.buildSearchBody();
        searchBody.add(searchTextField);

        return searchBody;
    }

    private Grid<SequenceEntity> buildGrid() {

        final var grid = ComponentFactory.buildGrid(SequenceEntity.class);

        grid.addColumn(new ComponentRenderer<>(sequenceEntity -> {

                    final var layout = new HorizontalLayout();
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    final var editImage = new Image("img/icons/edit-3-svgrepo-com.svg", "Edit");
                    editImage.setWidth("20px");
                    editImage.setHeight("20px");
                    editImage.getStyle().set("cursor", "pointer");

                    editImage.addClickListener(event -> {

                        LOGGER.info("Edit: {}", sequenceEntity.getPrefix());
                        modalView.openDialogForEdit(tenant, sequenceEntity);
                    });

                    layout.add(editImage);

                    return layout;
                }))
                .setHeader("#")
                .setResizable(true)
                .setWidth("5%");

        grid.addColumn("prefix")
                .setHeader("Prefijo")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("description")
                .setHeader("Descripción")
                .setSortable(true)
                .setResizable(true)
                .setWidth("20%");

        grid.addColumn("padSize")
                .setHeader("Dígitos")
                .setSortable(true)
                .setResizable(true)
                .setWidth("5%");

        grid.addColumn("stepSize")
                .setHeader("Incremento")
                .setSortable(true)
                .setResizable(true)
                .setWidth("5%");

        grid.addColumn("value")
                .setHeader("Valor")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        return grid;
    }
}
