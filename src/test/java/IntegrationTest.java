import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import transfer.ApplicationWeb;
import transfer.web.request.AccountCreateRequest;
import transfer.web.request.AccountTransferRequest;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    private static final String BASE_URI = "http://localhost:8080/";
    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws IOException {
        server = ApplicationWeb.startServer();
        target = ClientBuilder.newClient().target(BASE_URI);
    }

    @After
    public void destroy() {
        server.shutdownNow();
    }

    @Test
    public void receiveAllAccountsTest() throws IOException {
        Response response = target.path("/transmitter/v1/accounts")
                .request()
                .method("GET");
        ObjectMapper mapper = new ObjectMapper();
        List<AccountCreateRequest> request = mapper.readValue(response.readEntity(String.class), List.class);
        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(request.size(), 3);
    }

    @Test
    public void addCustomerTest() {
        Response response = target.path("/transmitter/v1/account/")
                .request().buildPost(Entity.entity(mockRequest("40817810990011274426"), MediaType.APPLICATION_JSON)).invoke();
        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals("Account 40817810990011274426 has been created successfully.", response.readEntity(String.class));
    }

    @Test
    public void addExistsCustomerTest() {
        Response response = target.path("/transmitter/v1/account/")
                .request().buildPost(Entity.entity(mockRequest("40817810990011274427"), MediaType.APPLICATION_JSON)).invoke();
        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Account 40817810990011274427 has already exists.", response.readEntity(String.class));
    }

    @Test
    public void removeCustomerTest() {
        Response response = target.path("/transmitter/v1/account/40817810990011274426")
                .request()
                .method("DELETE");
        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals("1 row(s) have been deleted.", response.readEntity(String.class));
    }

    @Test
    public void removeAbsentCustomerTest() {
        Response response = target.path("/transmitter/v1/account/40817810990011274420")
                .request()
                .method("DELETE");
        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals("0 row(s) have been deleted.", response.readEntity(String.class));
    }

    @Test
    public void transferTest() {
        Response response = target.path("/transmitter/v1/transfer")
                .request().buildPost(Entity.entity(mockAccountTransferRequest("40817810990011274428", "40817810990011274429", new BigDecimal(500)), MediaType.APPLICATION_JSON)).invoke();
        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals("Transfer has been completed successfully.", response.readEntity(String.class));
    }

    @Test
    public void transferNotFoundTest() {
        Response response = target.path("/transmitter/v1/transfer")
                .request().buildPost(Entity.entity(mockAccountTransferRequest("40817810990011274435", "40817810990011274429", new BigDecimal(500)), MediaType.APPLICATION_JSON)).invoke();
        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Account with id: 40817810990011274435 is not found.", response.readEntity(String.class));
    }

    @Test
    public void transferNotAnoughMoneyTest() {
        Response response = target.path("/transmitter/v1/transfer")
                .request().buildPost(Entity.entity(mockAccountTransferRequest("40817810990011274428", "40817810990011274429", new BigDecimal(1400)), MediaType.APPLICATION_JSON)).invoke();
        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Not enough money on account 40817810990011274428. Not enough 300", response.readEntity(String.class));
    }

    private AccountCreateRequest mockRequest(String accountNumber) {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setAccountNumber(accountNumber);
        request.setAmount(new BigDecimal(400.00));
        return request;
    }

    private AccountTransferRequest mockAccountTransferRequest(String accountFrom, String accountTo, BigDecimal amount) {
        AccountTransferRequest request = new AccountTransferRequest();
        request.setAccountFrom(accountFrom);
        request.setAccountTo(accountTo);
        request.setAmount(amount);
        return request;
    }
}