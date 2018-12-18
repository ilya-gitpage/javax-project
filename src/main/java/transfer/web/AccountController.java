package transfer.web;

import transfer.service.AccountService;
import transfer.web.request.AccountCreateRequest;
import transfer.web.request.AccountTransferRequest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transmitter/v1")
public class AccountController {

    @Inject
    private AccountService accountService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/accounts")
    public Response receiveAllAccounts() {
        return Response.ok(accountService.getAll()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/account")
    public Response addCustomer(AccountCreateRequest request) {
        accountService.insert(request.getAccountNumber(), request.getAmount());
        return Response.ok(String.format("Account %s has been created successfully.", request.getAccountNumber())).build();
    }

    @DELETE
    @Path("/account/{id}")
    public Response removeCustomer(@PathParam("id") String id) {
        int rows = accountService.delete(id);
        return Response.ok(String.format("%s row(s) have been deleted.", rows)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/transfer")
    public Response transferAmount(AccountTransferRequest request) {
        accountService.transfer(request.getAccountFrom(), request.getAccountTo(), request.getAmount());
        return Response.ok("Transfer has been completed successfully.").build();
    }
}