package com.revolut.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.revolut.transfer.db.DataSourceTestFactory;
import com.revolut.transfer.dto.AccountDTO;
import com.revolut.transfer.dto.AccountOperationDTO;
import com.revolut.transfer.dto.TransferDTO;
import com.revolut.transfer.repository.RepositoryManagerFactory;
import com.revolut.transfer.repository.impl.sql2o.RepositoryManagerFactoryImpl;
import com.revolut.transfer.service.ServiceContext;
import com.revolut.transfer.service.impl.ServiceContextImpl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AppTest {

    private static final String HOST = "http://localhost";
    private static final int PORT = 7001;
    private static final ObjectMapper mapper = new ObjectMapper();

    private static App app;

    @BeforeClass
    public static void setUp() {

        app = new App(PORT);
        final RepositoryManagerFactory repositoryManagerFactory
                = new RepositoryManagerFactoryImpl(DataSourceTestFactory.getDataSource());
        final ServiceContext serviceContext = new ServiceContextImpl(repositoryManagerFactory);
        app.initRoutes(serviceContext);

    }

    @AfterClass
    public static void tearDown() throws IOException {
        app.stop();
        Unirest.shutdown();
    }

    @Before
    public void setUpData() throws SQLException {

        final DataSource dataSource = DataSourceTestFactory.getDataSource();

        try (Connection connection = dataSource.getConnection()) {

            Statement st = connection.createStatement();

            st.executeUpdate("DELETE FROM transfer");
            st.executeUpdate("DELETE FROM account");

            st.executeUpdate("INSERT INTO account (id, customerId, currency, balance, active) VALUES " +
                    "(1, 1, 'USD', 100000, TRUE), " +
                    "(2, 1, 'RUB', 500000, TRUE), " +
                    "(3, 1, 'BTC', 0, TRUE), " +
                    "(4, 2, 'BTC', 0, FALSE)");

            st.executeUpdate("ALTER SEQUENCE transfer_seq RESTART WITH 1");
            st.executeUpdate("ALTER SEQUENCE account_seq RESTART WITH 5");

        }

    }

    @Test
    public void getAccountByIdNotFoundTest() throws UnirestException {
        final HttpResponse<JsonNode> jsonResponse = Unirest.get(getApiURI() + "/accounts/{accountId}")
                .routeParam("accountId", "5")
                .asJson();
         assertEquals(404, jsonResponse.getStatus());
    }

    @Test
    public void getAccountByIdInvalidIdTest() throws UnirestException {
        final HttpResponse<JsonNode> jsonResponse = Unirest.get(getApiURI() + "/accounts/{accountId}")
                .routeParam("accountId", "1x")
                .asJson();
        assertEquals(400, jsonResponse.getStatus());
    }

    @Test
    public void getAccountByIdTest() throws UnirestException, IOException {

        final HttpResponse<JsonNode> jsonResponse = Unirest.get(getApiURI() + "/accounts/{accountId}")
                .routeParam("accountId", "1")
                .asJson();

        assertEquals(200, jsonResponse.getStatus());
        assertNotNull(jsonResponse.getBody());

        AccountDTO accountDTO = mapper.readValue(jsonResponse.getBody().toString(), AccountDTO.class);

        assertEquals("1", accountDTO.getId());
        assertEquals("1", accountDTO.getCustomerId());
        assertEquals("USD", accountDTO.getCurrency());
        assertEquals("1000.00", accountDTO.getBalance());
        assertEquals("true", accountDTO.getActive());

    }

    @Test
    public void deleteAccountByIdBalanceNotEmptyTest() throws UnirestException {
        final HttpResponse<JsonNode> jsonResponse = Unirest.delete(getApiURI() + "/accounts/{accountId}")
                .routeParam("accountId", "1")
                .asJson();
        assertEquals(400, jsonResponse.getStatus());
    }

    @Test
    public void deleteAccountByIdBalanceIsEmptyTest() throws UnirestException, IOException {

        // Check that account is active and have no money

        final HttpResponse<JsonNode> readJsonResponse = Unirest.get(getApiURI() + "/accounts/{accountId}")
                .routeParam("accountId", "3")
                .asJson();

        AccountDTO accountDTO = mapper.readValue(readJsonResponse.getBody().toString(), AccountDTO.class);
        assertEquals("0.00000000", accountDTO.getBalance());
        assertEquals("true", accountDTO.getActive());

        // Deactivate it

        final HttpResponse<JsonNode> deleteJsonResponse = Unirest.delete(getApiURI() + "/accounts/{accountId}")
                .routeParam("accountId", "3")
                .asJson();

        assertEquals(200, deleteJsonResponse.getStatus());
        assertNotNull(deleteJsonResponse.getBody());

        accountDTO = mapper.readValue(deleteJsonResponse.getBody().toString(), AccountDTO.class);

        assertEquals("3", accountDTO.getId());
        assertEquals("1", accountDTO.getCustomerId());
        assertEquals("BTC", accountDTO.getCurrency());
        assertEquals("0.00000000", accountDTO.getBalance());
        assertEquals("false", accountDTO.getActive());

        // Check that account really was deactivated

        final HttpResponse<JsonNode> checkJsonResponse = Unirest.get(getApiURI() + "/accounts/{accountId}")
                .routeParam("accountId", "3")
                .asJson();

        accountDTO = mapper.readValue(checkJsonResponse.getBody().toString(), AccountDTO.class);
        assertEquals("false", accountDTO.getActive());

    }

    @Test
    public void findAccountByCustomerIdNotFoundTest() throws UnirestException {
        final HttpResponse<JsonNode> jsonResponse = Unirest.get(getApiURI() + "/accounts/customer/{customerId}")
                .routeParam("customerId", "5")
                .asJson();

        assertEquals(200, jsonResponse.getStatus());
        assertNotNull(jsonResponse.getBody());
        assertEquals("[]", jsonResponse.getBody().toString());

    }

    @Test
    public void findAccountByCustomerIdInvalidIdTest() throws UnirestException {
        final HttpResponse<JsonNode> jsonResponse = Unirest.get(getApiURI() + "/accounts/customer/{customerId}")
                .routeParam("customerId", "1x")
                .asJson();
        assertEquals(400, jsonResponse.getStatus());
    }

    @Test
    public void findAccountByCustomerIdTest() throws UnirestException, IOException {

        final HttpResponse<JsonNode> jsonResponse = Unirest.get(getApiURI() + "/accounts/customer/{customerId}")
                .routeParam("customerId", "1")
                .asJson();

        assertEquals(200, jsonResponse.getStatus());
        assertNotNull(jsonResponse.getBody());

        AccountDTO[] accountDTOs = mapper.readValue(jsonResponse.getBody().toString(), AccountDTO[].class);

        assertEquals(3, accountDTOs.length);

    }

    @Test
    public void createAccountInvalidCurrencyTest() throws UnirestException, IOException {

        final AccountDTO accountToCreateDTO = new AccountDTO();
        accountToCreateDTO.setCurrency("XYZ");
        accountToCreateDTO.setBalance("234.5");

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(
                getApiURI() + "/accounts/customer/{customerId}")
                .routeParam("customerId", "2")
                .body(mapper.writeValueAsString(accountToCreateDTO))
                .asJson();

        assertEquals(400, createdJsonResponse.getStatus());

    }

    @Test
    public void createAccountInvalidBalanceTest() throws UnirestException, IOException {

        final AccountDTO accountToCreateDTO = new AccountDTO();
        accountToCreateDTO.setCurrency("RUB");
        accountToCreateDTO.setBalance("43.544");

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(
                getApiURI() + "/accounts/customer/{customerId}")
                .routeParam("customerId", "2")
                .body(mapper.writeValueAsString(accountToCreateDTO))
                .asJson();

        assertEquals(400, createdJsonResponse.getStatus());

    }

    @Test
    public void createAccountNoBodyTest() throws UnirestException {

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(
                getApiURI() + "/accounts/customer/{customerId}")
                .routeParam("customerId", "2")
                .asJson();

        assertEquals(400, createdJsonResponse.getStatus());

    }

    @Test
    public void createAccountTest() throws UnirestException, IOException {

        final String customerId = "2";

        final AccountDTO accountToCreateDTO = new AccountDTO();
        accountToCreateDTO.setCurrency("USD");
        accountToCreateDTO.setBalance("234.5");

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(
                getApiURI() + "/accounts/customer/{customerId}")
                .routeParam("customerId", customerId)
                .body(mapper.writeValueAsString(accountToCreateDTO))
                .asJson();

        assertEquals(201, createdJsonResponse.getStatus());
        assertNotNull(createdJsonResponse.getBody());

        final AccountDTO createdAccountDTO = mapper.readValue(
                createdJsonResponse.getBody().toString(), AccountDTO.class);

        assertEquals("5", createdAccountDTO.getId());
        assertEquals(customerId, createdAccountDTO.getCustomerId());
        assertEquals(accountToCreateDTO.getCurrency(), createdAccountDTO.getCurrency());
        assertEquals("234.50", createdAccountDTO.getBalance());
        assertEquals("true", createdAccountDTO.getActive());

        // Check that account really was crated

        final HttpResponse<JsonNode> checkJsonResponse = Unirest.get(getApiURI() + "/accounts/{accountId}")
                .routeParam("accountId", createdAccountDTO.getId())
                .asJson();

        final AccountDTO accountDTO = mapper.readValue(checkJsonResponse.getBody().toString(), AccountDTO.class);

        assertEquals(createdAccountDTO.getId(), accountDTO.getId());
        assertEquals(createdAccountDTO.getCustomerId(), accountDTO.getCustomerId());
        assertEquals(createdAccountDTO.getCurrency(), accountDTO.getCurrency());
        assertEquals(createdAccountDTO.getBalance(), accountDTO.getBalance());
        assertEquals(createdAccountDTO.getActive(), accountDTO.getActive());

    }

    @Test
    public void createTransferSameAccountsTest() throws UnirestException, IOException {

        final TransferDTO transferToCreateDTO = new TransferDTO();
        transferToCreateDTO.setWithdrawalAccountId("1");
        transferToCreateDTO.setWithdrawalAccountCurrency("USD");
        transferToCreateDTO.setWithdrawalAmount("15.1");
        transferToCreateDTO.setDepositAccountId("1");
        transferToCreateDTO.setDepositAccountCurrency("USD");
        transferToCreateDTO.setDepositAmount("15.1");

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(getApiURI() + "/transfers")
                .body(mapper.writeValueAsString(transferToCreateDTO))
                .asJson();

        assertEquals(400, createdJsonResponse.getStatus());

    }

    @Test
    public void createTransferInvalidCurrencyTest() throws UnirestException, IOException {

        final TransferDTO transferToCreateDTO = new TransferDTO();
        transferToCreateDTO.setWithdrawalAccountId("1");
        transferToCreateDTO.setWithdrawalAccountCurrency("XYZ");
        transferToCreateDTO.setWithdrawalAmount("123");
        transferToCreateDTO.setDepositAccountId("2");
        transferToCreateDTO.setDepositAccountCurrency("ZYX");
        transferToCreateDTO.setDepositAmount("321");

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(getApiURI() + "/transfers")
                .body(mapper.writeValueAsString(transferToCreateDTO))
                .asJson();

        assertEquals(400, createdJsonResponse.getStatus());

    }

    @Test
    public void createTransferInvalidAmountTest() throws UnirestException, IOException {

        final TransferDTO transferToCreateDTO = new TransferDTO();
        transferToCreateDTO.setWithdrawalAccountId("1");
        transferToCreateDTO.setWithdrawalAccountCurrency("USD");
        transferToCreateDTO.setWithdrawalAmount("15.123");
        transferToCreateDTO.setDepositAccountId("2");
        transferToCreateDTO.setDepositAccountCurrency("RUB");
        transferToCreateDTO.setDepositAmount("1003.9");

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(getApiURI() + "/transfers")
                .body(mapper.writeValueAsString(transferToCreateDTO))
                .asJson();

        assertEquals(400, createdJsonResponse.getStatus());

    }

    @Test
    public void createTransferNotEnouthMoneyTest() throws UnirestException, IOException {

        final TransferDTO transferToCreateDTO = new TransferDTO();
        transferToCreateDTO.setWithdrawalAccountId("3");
        transferToCreateDTO.setWithdrawalAccountCurrency("BTC");
        transferToCreateDTO.setWithdrawalAmount("1");
        transferToCreateDTO.setDepositAccountId("2");
        transferToCreateDTO.setDepositAccountCurrency("RUB");
        transferToCreateDTO.setDepositAmount("200000");

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(getApiURI() + "/transfers")
                .body(mapper.writeValueAsString(transferToCreateDTO))
                .asJson();

        assertEquals(400, createdJsonResponse.getStatus());

    }

    @Test
    public void createTransferDeactivatedAccountTest() throws UnirestException, IOException {

        final TransferDTO transferToCreateDTO = new TransferDTO();
        transferToCreateDTO.setWithdrawalAccountId("4");
        transferToCreateDTO.setWithdrawalAccountCurrency("BTC");
        transferToCreateDTO.setWithdrawalAmount("1");
        transferToCreateDTO.setDepositAccountId("2");
        transferToCreateDTO.setDepositAccountCurrency("RUB");
        transferToCreateDTO.setDepositAmount("200000");

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(getApiURI() + "/transfers")
                .body(mapper.writeValueAsString(transferToCreateDTO))
                .asJson();

        assertEquals(400, createdJsonResponse.getStatus());

    }

    @Test
    public void createTransferWrongCurrencyTest() throws UnirestException, IOException {

        final TransferDTO transferToCreateDTO = new TransferDTO();
        transferToCreateDTO.setWithdrawalAccountId("1");
        transferToCreateDTO.setWithdrawalAccountCurrency("RUB");
        transferToCreateDTO.setWithdrawalAmount("15.1");
        transferToCreateDTO.setDepositAccountId("2");
        transferToCreateDTO.setDepositAccountCurrency("RUB");
        transferToCreateDTO.setDepositAmount("1003.9");

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(getApiURI() + "/transfers")
                .body(mapper.writeValueAsString(transferToCreateDTO))
                .asJson();

        assertEquals(400, createdJsonResponse.getStatus());

    }

    @Test
    public void createTransferNoBodyTest() throws UnirestException {

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(getApiURI() + "/transfers")
                .asJson();

        assertEquals(400, createdJsonResponse.getStatus());

    }

    @Test
    public void createTransferTest() throws UnirestException, IOException {

        final TransferDTO transferToCreateDTO = new TransferDTO();
        transferToCreateDTO.setWithdrawalAccountId("1");
        transferToCreateDTO.setWithdrawalAccountCurrency("USD");
        transferToCreateDTO.setWithdrawalAmount("15.1");
        transferToCreateDTO.setDepositAccountId("2");
        transferToCreateDTO.setDepositAccountCurrency("RUB");
        transferToCreateDTO.setDepositAmount("1003.9");

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(getApiURI() + "/transfers")
                .body(mapper.writeValueAsString(transferToCreateDTO))
                .asJson();

        assertEquals(201, createdJsonResponse.getStatus());
        assertNotNull(createdJsonResponse.getBody());

        final TransferDTO createdTransferDTO = mapper.readValue(createdJsonResponse.getBody().toString(),
                TransferDTO.class);

        assertEquals("1", createdTransferDTO.getId());
        assertNotNull(createdTransferDTO.getExecutedTimestamp());
        assertEquals(transferToCreateDTO.getWithdrawalAccountId(), createdTransferDTO.getWithdrawalAccountId());
        assertEquals(transferToCreateDTO.getWithdrawalAccountCurrency(), createdTransferDTO.getWithdrawalAccountCurrency());
        assertEquals("15.10", createdTransferDTO.getWithdrawalAmount());
        assertEquals(transferToCreateDTO.getDepositAccountId(), createdTransferDTO.getDepositAccountId());
        assertEquals(transferToCreateDTO.getDepositAccountCurrency(), createdTransferDTO.getDepositAccountCurrency());
        assertEquals("1003.90", createdTransferDTO.getDepositAmount());

        // 1. Check that the withdrawal took place.

        // 1.1 Account balance

        final HttpResponse<JsonNode> checkJsonWithdrawalAccountResponse = Unirest.get(
                getApiURI() + "/accounts/{accountId}")
                .routeParam("accountId", "1")
                .asJson();

        final AccountDTO withdrawalAccountDTO = mapper.readValue(
                checkJsonWithdrawalAccountResponse.getBody().toString(), AccountDTO.class);
        assertEquals("984.90", withdrawalAccountDTO.getBalance());

        // 1.2 Account withdrawal operation

        final HttpResponse<JsonNode> checkJsonWithdrawalResponse = Unirest.get(
                getApiURI() + "/accounts/{accountId}/withdrawals")
                .routeParam("accountId", "1")
                .asJson();

        final AccountOperationDTO[] withdrawalsAccountOperationDTO = mapper.readValue(
                checkJsonWithdrawalResponse.getBody().toString(), AccountOperationDTO[].class);

        assertEquals(1, withdrawalsAccountOperationDTO.length);

        final AccountOperationDTO withdrawalAccountOperationDTO = withdrawalsAccountOperationDTO[0];

        assertEquals(createdTransferDTO.getId(), withdrawalAccountOperationDTO.getId());
        assertEquals(createdTransferDTO.getExecutedTimestamp(), withdrawalAccountOperationDTO.getExecutedTimestamp());
        assertEquals(createdTransferDTO.getDepositAccountId(), withdrawalAccountOperationDTO.getCorrespondentAccountId());
        assertEquals(createdTransferDTO.getWithdrawalAmount(), withdrawalAccountOperationDTO.getAmount());

        // 2. Check that the deposit took place.

        // 2.1 Account balance

        final HttpResponse<JsonNode> checkJsonDepositAccountResponse = Unirest.get(
                getApiURI() + "/accounts/{accountId}")
                .routeParam("accountId", "2")
                .asJson();

        final AccountDTO depositAccountDTO = mapper.readValue(
                checkJsonDepositAccountResponse.getBody().toString(), AccountDTO.class);
        assertEquals("6003.90", depositAccountDTO.getBalance());

        // 2.2 Account deposit operation

        final HttpResponse<JsonNode> checkJsonDepositResponse = Unirest.get(
                getApiURI() + "/accounts/{accountId}/deposits")
                .routeParam("accountId", "2")
                .asJson();

        final AccountOperationDTO[] depositsAccountOperationDTO = mapper.readValue(
                checkJsonDepositResponse.getBody().toString(), AccountOperationDTO[].class);

        assertEquals(1, depositsAccountOperationDTO.length);

        final AccountOperationDTO depositAccountOperationDTO = depositsAccountOperationDTO[0];

        assertEquals(createdTransferDTO.getId(), depositAccountOperationDTO.getId());
        assertEquals(createdTransferDTO.getExecutedTimestamp(), depositAccountOperationDTO.getExecutedTimestamp());
        assertEquals(createdTransferDTO.getWithdrawalAccountId(), depositAccountOperationDTO.getCorrespondentAccountId());
        assertEquals(createdTransferDTO.getDepositAmount(), depositAccountOperationDTO.getAmount());
    }


    private String getApiURI() {
        return HOST + ":" + PORT + "/api";
    }

}
