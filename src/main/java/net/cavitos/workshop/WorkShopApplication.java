package net.cavitos.workshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.AppShellConfigurator;

@Theme("resta")
@SpringBootApplication
public class WorkShopApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(WorkShopApplication.class, args);
    }
}
