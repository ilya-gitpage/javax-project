package transfer.exceptions.handlers;

import transfer.exceptions.ProcessingAccountException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class ProcessingAccountExceptionHandler implements ExceptionMapper<ProcessingAccountException> {

    @Override
    public Response toResponse(ProcessingAccountException exception) {
        return Response.status(BAD_REQUEST).entity(exception.getMessage()).build();
    }
}