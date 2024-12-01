package net.cavitos.workshop.views.component;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import net.cavitos.workshop.views.model.Pagination;

import java.util.function.Consumer;

public class Paginator extends VerticalLayout {

    private final static String CURRENT_PAGE = "Página %d de %d";

    private final Text currentPageText;

    private Pagination pagination;

    public Paginator(final Pagination pagination,
                     final Consumer<Pagination> onAction) {

        this.pagination = pagination;

        final var pageSizeComboBox = new ComboBox<Integer>();
        pageSizeComboBox.setWidth("100px");
        pageSizeComboBox.setItems(25, 50, 100, 200, 500);
        pageSizeComboBox.setValue(this.pagination.getSize());

        pageSizeComboBox.addValueChangeListener(event -> {

            this.pagination.setSize(event.getValue());
            this.pagination.setPage(0);
            onAction.accept(this.pagination);
            updatePageText();
        });


        this.currentPageText = new Text(CURRENT_PAGE.formatted(this.pagination.getPage() + 1,
                this.pagination.getTotalPages()));
        final var span = new Span(this.currentPageText);
        span.setClassName("paginator-text");

        final var previousButton = new Button();
        previousButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        previousButton.setIcon(new SvgIcon("img/icons/previous-back-svgrepo-com.svg"));
        previousButton.addClickListener(event -> {

            if (this.pagination.getPage() > 0) {
                this.pagination.setPage(this.pagination.getPage() - 1);
                onAction.accept(this.pagination);
                updatePageText();
            }
        });

        final var nextButton = new Button();
        nextButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        nextButton.setIcon(new SvgIcon("img/icons/right-arrow-svgrepo-com.svg"));
        nextButton.addClickListener(event -> {

            if (this.pagination.getPage() < this.pagination.getTotalPages() - 1) {
                this.pagination.setPage(this.pagination.getPage() + 1);
                onAction.accept(this.pagination);
                updatePageText();
            }
        });

        final var horizontalLayout = new HorizontalLayout(
                previousButton,
                pageSizeComboBox,
                nextButton,
                span
        );

        add(horizontalLayout);
    }

    public void setPagination(final Pagination pagination) {

        this.pagination = pagination;
        updatePageText();
    }

    private void updatePageText() {
        final var currentPage = pagination.getPage() + 1;
        this.currentPageText.setText(CURRENT_PAGE.formatted(currentPage, pagination.getTotalPages()));
    }

}
