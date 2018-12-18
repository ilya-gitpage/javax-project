package transfer;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import transfer.exceptions.handlers.AccountNotFoundExceptionHandler;
import transfer.exceptions.handlers.ServerExceptionHandler;
import transfer.exceptions.handlers.ProcessingAccountExceptionHandler;
import transfer.filters.CorsFilter;
import transfer.web.bind.AccountApplicationBinder;
import transfer.web.AccountController;

import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Properties;

@Slf4j
@Provider
public class ApplicationWeb {

    private static final String BASE_URI = "http://localhost:8080/";
    private static final String JDBC_PROPERTIES_FILE_NAME = "database/jdbc.properties";
    private static final String CHANGE_LOG = "database/liquibase/db-changelog-master.xml";

    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static HttpServer startServer() throws IOException {
        runLiquibase();
        ResourceConfig config = new ResourceConfig()
                .register(new AccountApplicationBinder())
                .register(new CorsFilter())
                .register(new AccountController())
                .register(new AccountNotFoundExceptionHandler())
                .register(new ServerExceptionHandler())
                .register(new ProcessingAccountExceptionHandler())
                .register(JacksonJsonProvider.class);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }

    private static void runLiquibase() throws IOException {
        InputStream input;
        Properties properties = new Properties();
        input = ApplicationWeb.class.getClassLoader().getResourceAsStream(JDBC_PROPERTIES_FILE_NAME);
        if (input == null) {
            throw new RuntimeException("Sorry, unable to find " + JDBC_PROPERTIES_FILE_NAME);
        }
        properties.load(input);
        try (Connection c = DriverManager.getConnection(properties.getProperty("url"),
                properties.getProperty("username"),
                properties.getProperty("password")))  {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
            Liquibase liquibase = new Liquibase(CHANGE_LOG, new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts());
        } catch (LiquibaseException | SQLException e) {
            throw new NoSuchElementException(e.getMessage());
        }
    }
}