package transfer.dao.mappers;

import transfer.model.Account;

import java.util.List;

public interface AccountMapper {
    void insert(Account account);
    Account getByAccountNumber(String accountNumber);
    int delete(String accountNumber);
    void update(Account account);
    List<Account> getAll();
}