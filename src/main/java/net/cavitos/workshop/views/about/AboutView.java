package net.cavitos.workshop.views.about;

import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchBody;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchBox;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchTitle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import net.cavitos.workshop.views.layouts.MainLayout;

@RolesAllowed({ "ROLE_user" })
@PageTitle("Aceca de...")
@Route(value = "about", layout = MainLayout.class)
public class AboutView extends VerticalLayout{

    private static final Logger LOGGER = LoggerFactory.getLogger(AboutView.class);

    private static final String GIT_PROPERTIES_FILE = "git.properties";

    private final JdbcTemplate jdbcTemplate;

    private H3 pageTitle;

    private TextField gitBranch;
    private TextField gitCommit;
    private TextField buildVersion;
    private TextField gitTag;

    public AboutView(final DataSource dataSource) {

        jdbcTemplate = new JdbcTemplate(dataSource);
        buildApplicationInfo();
        buildApplicationServiceStatus();
    }

    private void buildApplicationInfo() {

        final var gitProperties = loadGitProperties();

        pageTitle = buildSearchTitle("Información de la Aplicación");

        gitBranch = new TextField("Rama de Git");
        gitBranch.setReadOnly(true);
        gitBranch.setWidth("50%");
        gitBranch.setValue(gitProperties.getProperty("git.branch", "Desconocida"));

        gitCommit = new TextField("Commit de Git");
        gitCommit.setReadOnly(true);
        gitCommit.setWidth("50%");
        gitCommit.setValue(gitProperties.getProperty("git.commit.id.abbrev", "Desconocido"));

        buildVersion = new TextField("Versión de Construcción");
        buildVersion.setReadOnly(true);
        buildVersion.setWidth("50%");
        buildVersion.setValue(gitProperties.getProperty("git.build.version", "Desconocida"));

        gitTag = new TextField("Etiqueta de Git");
        gitTag.setReadOnly(true);
        gitTag.setWidth("50%");
        gitTag.setValue(gitProperties.getProperty("git.tag", ""));

        final var firstRow = buildSearchBody();
        firstRow.add(gitBranch, gitCommit);

        final var secondRow = buildSearchBody();
        secondRow.add(buildVersion, gitTag);

        final var infoBody = new VerticalLayout(
            firstRow,
            secondRow
        );

        final var infoBox = buildSearchBox();
        infoBox.add(infoBody);

        add(pageTitle, infoBox);
    }

    private void buildApplicationServiceStatus() {

        final var statusTitle = buildSearchTitle("Estado del Servicio");

        final var databaseStatus = new Text("Base de Datos:");

        var statusIcon = isDatabaseAvailable() ? 
            new SvgIcon("img/icons/check-mark-button-svgrepo-com.svg") : 
            new SvgIcon("img/icons/cross-close-svgrepo-com.svg");

        final var statusBody = buildSearchBody();
        statusBody.add(databaseStatus, statusIcon);

        final var statusBox = buildSearchBox();
        statusBox.add(statusBody);

        add(statusTitle, statusBox);
    }

    private Properties loadGitProperties() {

        final var gitProps = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(GIT_PROPERTIES_FILE)) {

            if (inputStream == null) {
                LOGGER.warn("git.properties not found on classpath");
                return gitProps;
            }

            gitProps.load(inputStream);
            return gitProps;

        } catch (IOException exception) {

            LOGGER.error("Error loading git properties", exception);
            return new Properties();
        }
    }

    private boolean isDatabaseAvailable() {

        try {

            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;

        } catch (Exception exception) {

            LOGGER.error("Database is not available", exception);
            return false;
        }
    }

}
