package net.cavitos.workshop.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import net.cavitos.workshop.views.layouts.MainLayout;

@PermitAll
@PageTitle("Inicio")
@Route(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

    public HomeView() {

        add(new H1("Welcome to your new application"));
        add(new Paragraph("This is the home view"));

        add(new Paragraph("You can edit this view in src/main/java/net/cavitos/workshop/views/HomeView.java"));

    }
}
