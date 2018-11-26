package com.revolut.transfer;

import com.revolut.transfer.controller.AccountController;
import com.revolut.transfer.controller.TransferController;
import io.javalin.Javalin;
import io.javalin.core.util.SwaggerRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class App {

    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        final SwaggerRenderer swaggerRenderer = new SwaggerRenderer("swagger.yaml");

        Javalin app = Javalin.create()
                .enableWebJars()
                .port(7000)
                .start();

        app.routes(() -> {
            get("/", ctx -> ctx.html("Welcome to Revolut transfer API!<br> Spec is <a href='/spec'>here</a>"));
            get("/spec", swaggerRenderer);
            path("api", () -> {
                path("accounts", () -> {
                    path("customer/:customer-id", () -> {
                        get(AccountController.getAll);
                        post(AccountController.create);
                    });
                    path(":account-id", () -> {
                        get(AccountController.getOne);
                        delete(AccountController.deactivate);
                        path("deposits", () -> {
                            get(TransferController.getDeposits);
                        });
                        path("withdrawals", () -> {
                            get(TransferController.getWithdrawals);
                        });
                    });
                });
                path("transfers", () -> {
                    post(TransferController.create);
                });
            });
        });

        app.exception(Exception.class, (e, ctx) -> {
            logger.error(e.getMessage());
            final Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            ctx.status(400)
                    .json(error);
        });

    }

    // todo API reference ?swagger (get /api)
    // todo test coverage

}


