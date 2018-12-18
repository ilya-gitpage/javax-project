package transfer.web.bind;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import transfer.dao.AccountDao;
import transfer.service.AccountService;

import javax.inject.Singleton;

public class AccountApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(AccountDao.class).to(AccountDao.class).in(Singleton.class);
        bind(AccountService.class).to(AccountService.class).in(Singleton.class);
    }
}