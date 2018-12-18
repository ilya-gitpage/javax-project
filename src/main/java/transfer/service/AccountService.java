package transfer.service;

import transfer.dao.AccountDao;
import transfer.exceptions.AccountNotFoundException;
import transfer.exceptions.ProcessingAccountException;
import transfer.model.Account;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountService {

    private static final String NOT_ENOUGH_MONEY_ERROR_MSG = "Not enough money on account %s. Not enough %s";
    private static final String NEGATIVE_AMOUNT_ERROR_MSG = "Amount is negative: %s";
    private static final String ACCOUNT_EXISTS_ERROR_MESSAGE = "Account %s has already exists.";
    @Inject
    private AccountDao accountDao;

    public void insert(String accountNumber, BigDecimal amount) {
        Optional<Account> checkingAccount = accountDao.getAccountByNumber(accountNumber);
        if (checkingAccount.isPresent()) {
            throw new ProcessingAccountException(String.format(ACCOUNT_EXISTS_ERROR_MESSAGE, accountNumber));
        }
        accountDao.insert(new Account(accountNumber, amount));
    }

    public int delete(String accountNumber) {
        return accountDao.delete(accountNumber);
    }

    public List<Account> getAll() {
        return accountDao.getAllAccounts();
    }

    public void transfer(String accountNumberFrom, String accountNumberTo, BigDecimal amount) {
        if (!checkAmount(amount)) {
            throw new ProcessingAccountException(String.format(NEGATIVE_AMOUNT_ERROR_MSG, amount));
        }
        Account accountFrom = accountDao.getAccountByNumber(accountNumberFrom).orElseThrow(() -> new AccountNotFoundException(accountNumberFrom));
        Account accountTo = accountDao.getAccountByNumber(accountNumberTo).orElseThrow(() -> new AccountNotFoundException(accountNumberTo));

        BigDecimal amountFrom = accountFrom.getAmount();
        BigDecimal amountTo = accountTo.getAmount();
        if (amountFrom.compareTo(amount) < 0) {
            throw new ProcessingAccountException(String.format(NOT_ENOUGH_MONEY_ERROR_MSG, accountFrom.getAccountNumber(), amountTo.subtract(amountFrom)));
        }
        accountFrom.setAmount(amountFrom.subtract(amount));
        accountTo.setAmount(amountTo.add(amount));
        accountDao.update(accountFrom, accountTo);
    }

    private boolean checkAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }
}