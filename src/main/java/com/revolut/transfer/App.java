package com.revolut.transfer;

import com.revolut.transfer.controller.AccountController;
import com.revolut.transfer.controller.TransferController;
import com.revolut.transfer.db.DataSourceFactory;
import com.revolut.transfer.repository.RepositoryManagerFactory;
import com.revolut.transfer.repository.impl.sql2o.RepositoryManagerFactoryImpl;
import com.revolut.transfer.service.ServiceContext;
import com.revolut.transfer.service.impl.ServiceContextImpl;
import io.javalin.Javalin;
import io.javalin.core.util.SwaggerRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class App {

    private static Logger logger = LoggerFactory.getLogger(App.class);
    private ServiceContext serviceContext;

    private final Javalin javalinApp;

    public static void main(String[] args) {
        final App app = new App(7000);

        final RepositoryManagerFactory repositoryManagerFactory
                = new RepositoryManagerFactoryImpl(DataSourceFactory.getDataSource());
        final ServiceContext serviceContext = new ServiceContextImpl(repositoryManagerFactory);
        app.setServiceContext(serviceContext);
        app.initRoutes();
    }

    public App (int port) {

        this.javalinApp = Javalin.create()
                .enableWebJars()
                .port(port)
                .start();

    }

    public void setServiceContext(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    public void initRoutes() {

        final SwaggerRenderer swaggerRenderer = new SwaggerRenderer("swagger.yaml");
        final AccountController accountController = new AccountController(serviceContext);
        final TransferController transferController = new TransferController(serviceContext);

        this.javalinApp.routes(() -> {
            get("/", ctx -> ctx.html("Welcome to Revolut transfer API!<br> Spec is <a href='/spec'>here</a>"));
            get("/spec", swaggerRenderer);
            path("api", () -> {
                path("accounts", () -> {
                    path("customer/:customer-id", () -> {
                        get(accountController::getAll);
                        post(accountController::create);
                    });
                    path(":account-id", () -> {
                        get(accountController::getOne);
                        delete(accountController::deactivate);
                        path("deposits", () -> {
                            get(transferController::getDeposits);
                        });
                        path("withdrawals", () -> {
                            get(transferController::getWithdrawals);
                        });
                    });
                });
                path("transfers", () -> {
                    post(transferController::create);
                });
            });
        });

        this.javalinApp.exception(Exception.class, (e, ctx) -> {
            logger.error(e.getMessage());
            final Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            if (e instanceof NoSuchElementException) {
                ctx.status(404);
            } else {
                ctx.status(400);
            }
            ctx.json(error);
        });

    }

    public void stop() {
        this.javalinApp.stop();
    }

}


