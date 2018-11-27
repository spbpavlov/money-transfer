package com.revolut.transfer;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class AppTest {

    private static App app;

    @BeforeClass
    public static void setUp() {
        app = new App(7000);
        app.initRoutes();
    }

    @AfterClass
    public static void tearDown() {
        app.stop();
    }



}
