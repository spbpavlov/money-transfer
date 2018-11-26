package com.revolut.transfer.repository.impl.sql2o;

import com.revolut.transfer.model.Account;
import com.revolut.transfer.model.Transfer;
import com.revolut.transfer.repository.TransferRepository;
import lombok.NonNull;
import org.sql2o.Connection;
import org.sql2o.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

class TransferRepositoryImpl implements TransferRepository {

    private final Connection con;

    TransferRepositoryImpl(Connection con) {
        this.con = con;
    }

    @Override
    public Transfer create(Transfer transfer) {

        final String sql =
                "INSERT INTO transfer (executedTimestamp, withdrawalAccountId, withdrawalAmount, depositAccountId, depositAmount) " +
                "VALUES (:executedTimestamp, :withdrawalAccountId, :withdrawalAmount, :depositAccountId, :depositAmount)";

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

    @Override
    public List<Transfer> findWithdrawalByAccountId(long accountId, Timestamp start, Timestamp end) {

        final String sql =
                "SELECT id, executedTimestamp, depositAccountId, withdrawalAmount " +
                        "FROM transfer AS transfer " +
                        "WHERE withdrawalAccountId = :withdrawalAccountId " +
                        (Objects.nonNull(start) ? "AND executedTimestamp >= :start " : "") +
                        (Objects.nonNull(end) ? "AND executedTimestamp <= :end " : "");

        final Query query =  con.createQuery(sql)
                .addParameter("withdrawalAccountId", accountId);

        if (Objects.nonNull(start)) {
            query.addParameter("start", start);
        }

        if (Objects.nonNull(end)) {
            query.addParameter("end", end);
        }

        return query.executeAndFetch(Transfer.class);

    }

    @Override
    public List<Transfer> findDepositByAccountId(long accountId, Timestamp start, Timestamp end) {

        final String sql =
                "SELECT  id, executedTimestamp, withdrawalAccountId, depositAmount " +
                        "FROM transfer AS transfer " +
                        "WHERE depositAccountId = :depositAccountId " +
                        (Objects.nonNull(start) ? "AND executedTimestamp >= :start " : "") +
                        (Objects.nonNull(end) ? "AND executedTimestamp <= :end " : "");


        final Query query = con.createQuery(sql)
                .addParameter("depositAccountId", accountId);

        if (Objects.nonNull(start)) {
            query.addParameter("start", start);
        }

        if (Objects.nonNull(end)) {
            query.addParameter("end", end);
        }

        return query.executeAndFetch(Transfer.class);

    }

}
