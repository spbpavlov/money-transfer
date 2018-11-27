package com.revolut.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.revolut.transfer.db.DataSourceTestFactory;
import com.revolut.transfer.dto.AccountDTO;
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
import java.util.HashMap;

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

        app.setServiceContext(serviceContext);
        app.initRoutes();
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

            st.executeUpdate("ALTER SEQUENCE transfer_seq RESTART WITH 1");
            st.executeUpdate("ALTER SEQUENCE account_seq RESTART WITH 1");

            st.executeUpdate("INSERT INTO account (customerId, currency, balance, active) VALUES " +
                    "(1, 'USD', 100000, TRUE), " +
                    "(1, 'RUB', 500000, TRUE), " +
                    "(1, 'BTC', 0, TRUE)");

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
    public void deleteAccountByIdBalanceNotEmptyTest() throws UnirestException, IOException {
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

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(getApiURI() + "/accounts/customer/{customerId}")
                .routeParam("customerId", "2")
                .body(mapper.writeValueAsString(accountToCreateDTO))
                .asJson();

        assertEquals(400, createdJsonResponse.getStatus());

    }

    @Test
    public void createAccountTest() throws UnirestException, IOException {

        final AccountDTO accountToCreateDTO = new AccountDTO();
        accountToCreateDTO.setCurrency("USD");
        accountToCreateDTO.setBalance("234.5");

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(getApiURI() + "/accounts/customer/{customerId}")
                .routeParam("customerId", "2")
                .body(mapper.writeValueAsString(accountToCreateDTO))
                .asJson();

        assertEquals(201, createdJsonResponse.getStatus());
        assertNotNull(createdJsonResponse.getBody());

        final AccountDTO createdAccountDTO = mapper.readValue(createdJsonResponse.getBody().toString(), AccountDTO.class);

        assertEquals("4", createdAccountDTO.getId());
        assertEquals("2", createdAccountDTO.getCustomerId());
        assertEquals("USD", createdAccountDTO.getCurrency());
        assertEquals("234.50", createdAccountDTO.getBalance());
        assertEquals("true", createdAccountDTO.getActive());

        // Check that account really was crated

        final HttpResponse<JsonNode> jsonResponse = Unirest.get(getApiURI() + "/accounts/{accountId}")
                .routeParam("accountId", createdAccountDTO.getId())
                .asJson();

        final AccountDTO accountDTO = mapper.readValue(jsonResponse.getBody().toString(), AccountDTO.class);

        assertEquals("4", accountDTO.getId());
        assertEquals("2", accountDTO.getCustomerId());
        assertEquals("USD", accountDTO.getCurrency());
        assertEquals("234.50", accountDTO.getBalance());
        assertEquals("true", accountDTO.getActive());

    }

    @Test
    public void createTransferTest() throws UnirestException, IOException {

        final HashMap<String, Object> transferToCreate = new HashMap<>();
        transferToCreate.put("withdrawalAccountId", 1);
        transferToCreate.put("withdrawalAccountCurrency", "USD");
        transferToCreate.put("withdrawalAmount", 15);
        transferToCreate.put("depositAccountId", 2);
        transferToCreate.put("depositAccountCurrency", "RUB");
        transferToCreate.put("depositAmount", 1003.95);

        final HttpResponse<JsonNode> createdJsonResponse = Unirest.post(getApiURI() + "/accounts/transfers")
                .body(mapper.writeValueAsString(transferToCreate))
                .asJson();

        assertEquals(201, createdJsonResponse.getStatus());
        assertNotNull(createdJsonResponse.getBody());

//        final TransferDTO createdTransferDTO = mapper.readValue(createdJsonResponse.getBody().toString(), TransferDTO.class);
//
//        assertEquals("4", createdAccountDTO.getId());
//        assertEquals("2", createdAccountDTO.getCustomerId());
//        assertEquals("USD", createdAccountDTO.getCurrency());
//        assertEquals("234.50", createdAccountDTO.getBalance());
//        assertEquals("true", createdAccountDTO.getActive());
//
//        // Check that account really was crated
//
//        final HttpResponse<JsonNode> jsonResponse = Unirest.get(getApiURI() + "/accounts/{accountId}")
//                .routeParam("accountId", createdAccountDTO.getId())
//                .asJson();
//
//        final AccountDTO accountDTO = mapper.readValue(jsonResponse.getBody().toString(), AccountDTO.class);
//
//        assertEquals("4", accountDTO.getId());
//        assertEquals("2", accountDTO.getCustomerId());
//        assertEquals("USD", accountDTO.getCurrency());
//        assertEquals("234.50", accountDTO.getBalance());
//        assertEquals("true", accountDTO.getActive());

    }


    private String getApiURI() {
        return HOST + ":" + PORT + "/api";
    }

}
