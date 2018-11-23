package com.revolut.transfer;

import com.revolut.transfer.controller.AccountController;
import com.revolut.transfer.controller.TransferController;
import io.javalin.Javalin;

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
                get(ctx -> ctx.html("Welcome to Revolut Transfer API!"));
                path("accounts/:customer-id", () -> {
                    get(AccountController.getAll);
                    post(AccountController.create);
                    path(":account-id", () -> {
                        get(AccountController.getOne);
                        patch(":amount", AccountController.update);
                        delete(AccountController.delete);
                    });
                });
                path("transfers", () -> {
                    post(TransferController.create);
                });
            });
        });

    }

}
