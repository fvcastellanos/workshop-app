package net.cavitos.workshop.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.cavitos.workshop.views.layouts.MainLayout;

@PageTitle("Marcas de Vehículos")
@Route(value = "car-brands", layout = MainLayout.class)
public class CarBrandView extends VerticalLayout {

    public CarBrandView() {
        add(new H1("Welcome to your new application"));
        add(new Paragraph("This is the home view"));

        add(new Paragraph("You can edit this view in src/main/java/net/cavitos/workshop/views/HomeView.java"));

    }
}
