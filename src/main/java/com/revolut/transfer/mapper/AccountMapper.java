package com.revolut.transfer.mapper;

import com.revolut.transfer.dto.AccountDTO;
import com.revolut.transfer.model.Account;
import com.revolut.transfer.model.Currency;
import lombok.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AccountMapper {

    public static AccountDTO accountToAccountDTO(@NonNull Account account) {

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(Long.toString(account.getId()));
        accountDTO.setCustomerId(Long.toString(account.getCustomerId()));
        accountDTO.setCurrency(account.getCurrency().toString());
        accountDTO.setBalance(AmountMapper.longToString(account.getBalance(), account.getCurrency()));
        accountDTO.setActive(Boolean.toString(account.isActive()));
        return accountDTO;

    }

    public static Account accountDTOToAccount(@NonNull AccountDTO accountDTO) {

        Account account = new Account();
        account.setId(Objects.isNull(accountDTO.getId()) ? 0 : Long.parseLong(accountDTO.getId()));
        account.setCustomerId(Objects.isNull(accountDTO.getCustomerId()) ? 0 : Long.parseLong(accountDTO.getCustomerId()));
        account.setCurrency(Currency.valueOf(accountDTO.getCurrency()));
        account.setBalance(Objects.isNull(accountDTO.getBalance()) ? 0 :
                AmountMapper.stringToLong(accountDTO.getBalance(), account.getCurrency()));
        account.setActive(Objects.isNull(accountDTO.getActive()) ? true : Boolean.valueOf(accountDTO.getActive()));

        return account;

    }

    public static List<AccountDTO> accountToAccountDTO(@NonNull List<Account> accounts) {

        return accounts.stream()
                .map(AccountMapper::accountToAccountDTO)
                .collect(Collectors.toList());

    }

    public static List<Account> accountDTOToAccount(@NonNull List<AccountDTO> accountsDTO) {

        return accountsDTO.stream()
                .map(AccountMapper::accountDTOToAccount)
                .collect(Collectors.toList());

    }

}
