package com.revolut.transfer.repository;

import com.revolut.transfer.model.Transfer;

public interface TransferRepository {

    Transfer create(Transfer transfer);

}
