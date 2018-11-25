package com.revolut.transfer.repository.impl.sql2o;

import com.revolut.transfer.model.Transfer;
import com.revolut.transfer.repository.TransferRepository;
import org.sql2o.Connection;

class TransferRepositoryImpl implements TransferRepository {

    private final Connection con;

    TransferRepositoryImpl(Connection con) {
        this.con = con;
    }

    @Override
    public Transfer create(Transfer transfer) {

        final String sql =
                "INSERT INTO transfer (executedTimestamp, withdrawalAccountId, withdrawalAmount, depositAccountId, depositAmount) " +
                "VALUES (:executedTimestamp, :withdrawAccountId, :withdrawAmount, :depositAccountId, :depositAmount)";

        long id = (Long) con.createQuery(sql, true)
                .addParameter("executedTimestamp", transfer.getExecutedTimestamp())
                .addParameter("withdrawalAccountId", transfer.getWithdrawalAccountId())
                .addParameter("withdrawalAmount", transfer.getWithdrawalAmount())
                .addParameter("depositAccountId", transfer.getDepositAccountId())
                .addParameter("depositAmount", transfer.getDepositAmount())
                .executeUpdate()
                .getKey();

        transfer.setId(id);

        return transfer;

    }

}
