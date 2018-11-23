package com.revolut.transfer.repository;

import java.util.Optional;

public interface CrudRepository<T, ID> {

    <S extends T> S create(S var1);

    Optional<T> findById(ID var1);

    Iterable<T> findAllById(Iterable<ID> var1);

    void deleteById(ID var1);

}