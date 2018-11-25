package com.revolut.transfer.repository;

public interface RepositoryManagerFactory {

    RepositoryManager getRepositoryManager(int isolationLevel);

}
