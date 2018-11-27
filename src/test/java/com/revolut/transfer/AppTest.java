package com.revolut.transfer;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.revolut.transfer.db.DataSourceTestFactory;
import com.revolut.transfer.repository.RepositoryManagerFactory;
import com.revolut.transfer.repository.impl.sql2o.RepositoryManagerFactoryImpl;
import com.revolut.transfer.service.ServiceContext;
import com.revolut.transfer.service.impl.ServiceContextImpl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;

public class AppTest {

    private static final String HOST = "http://localhost";
    private static final int PORT = 7001;

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
    public static void tearDown() {
        app.stop();
    }

    @Before
    public void setUpData() {
        final DataSource dataSource = DataSourceTestFactory.getDataSource();

    //    dataSource.getConnection()

    }

    @Test
    public void getAccountByIdTest() throws UnirestException {
        Unirest.get(getApiURI() + "/{method}")
                .routeParam("method", "get")
                .queryString("name", "Mark")
                .asJson();
    }

    private String getApiURI() {
        return HOST + ":" + PORT + "/api";
    }

}
