package com.revolut.transfer.repository.impl.sql2o;

import com.revolut.transfer.repository.RepositoryManager;
import com.revolut.transfer.repository.RepositoryManagerFactory;
import org.sql2o.Sql2o;

import javax.sql.DataSource;

public class RepositoryManagerFactoryImpl implements RepositoryManagerFactory {

    private final Sql2o sql2o;

    public RepositoryManagerFactoryImpl(DataSource dataSource) {
        this.sql2o = new Sql2o(dataSource);
    }

    @Override
    public RepositoryManager getRepositoryManager(int isolationLevel) {
        return new RepositoryManagerImpl(sql2o, isolationLevel);
    }

}
