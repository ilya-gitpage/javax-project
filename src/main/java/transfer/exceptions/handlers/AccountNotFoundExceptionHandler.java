package transfer.exceptions.handlers;

import transfer.exceptions.AccountNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class AccountNotFoundExceptionHandler implements ExceptionMapper<AccountNotFoundException> {

    @Override
    public Response toResponse(AccountNotFoundException exception) {
        return Response.status(NOT_FOUND).entity(exception.getMessage()).build();
    }
}