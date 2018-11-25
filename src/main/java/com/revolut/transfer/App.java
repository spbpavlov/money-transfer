package com.revolut.transfer;

import com.revolut.transfer.controller.AccountController;
import com.revolut.transfer.controller.TransferController;
import io.javalin.Javalin;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.patch;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class App {

    public static void main(String[] args) {

        Javalin app = Javalin.create()
                .port(7000)
                .start();

        app.routes(() -> {
            path("api", () -> {
                path("accounts", () -> {
                    path("customer/:customer-id", () -> {
                        get(AccountController.getAll);
                        post(AccountController.create);
                    });
                    path(":account-id", () -> {
                        get(AccountController.getOne);
                        delete(AccountController.deactivate);
                        path("deposits/:start/:end", () -> {
                            get(AccountController.getDeposits);
                        });
                        path("withdrawals/:start/:end", () -> {
                            get(AccountController.getWithdrawals);
                        });
                    });
                });
                path("transfers", () -> {
                    post(TransferController.create);
                });
            });
        });

        app.exception(Exception.class, (e, ctx) -> {
            final Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            ctx.status(400)
               .json(error);
        });

    }

    // todo API reference ?swagger (get /api)
    // todo README.MD
    // todo test coverage
    // todo DTO at service layer
    // todo check if balance will be amount

}


