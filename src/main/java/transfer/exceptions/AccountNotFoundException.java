package transfer.exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String id) {
        super(String.format("Account with id: %s is not found.", id));
    }
}