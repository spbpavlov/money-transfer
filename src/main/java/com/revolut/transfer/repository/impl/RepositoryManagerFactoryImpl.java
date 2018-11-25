package com.revolut.transfer.repository.impl;

import com.revolut.transfer.repository.RepositoryManager;
import com.revolut.transfer.repository.RepositoryManagerFactory;
import com.revolut.transfer.repository.impl.sql2o.RepositoryManagerImpl;

public class RepositoryManagerFactoryImpl implements RepositoryManagerFactory {

    @Override
    public RepositoryManager getRepositoryManager(int isolationLevel) {
        return new RepositoryManagerImpl(isolationLevel);
    }

}
