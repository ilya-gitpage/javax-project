package transfer.dao;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import transfer.dao.mappers.AccountMapper;
import transfer.model.Account;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AccountDao {

    private static final String DEFAULT_CONFIG_FILE_NAME = "database/mybatis-config.xml";
    private SqlSessionFactory sqlSessionFactory;

    @PostConstruct
    public void init() {
        try {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader(DEFAULT_CONFIG_FILE_NAME));
        } catch (IOException e) {
            throw new RuntimeException("Error when reading database properties.");
        }
    }

    public void insert(Account account) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            accountMapper.insert(account);
            sqlSession.commit();
        }
    }

    public Optional<Account> getAccountByNumber(String accountNumber) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            return Optional.ofNullable(accountMapper.getByAccountNumber(accountNumber));
        }
    }

    public List<Account> getAllAccounts() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            return accountMapper.getAll();
        }
    }

    public int delete(String accountNumber) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            int rows = accountMapper.delete(accountNumber);
            sqlSession.commit();
            return rows;
        }
    }

    public void update(Account accountFrom, Account accountTo) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            accountMapper.update(accountFrom);
            accountMapper.update(accountTo);
            sqlSession.commit();
        }
    }
}