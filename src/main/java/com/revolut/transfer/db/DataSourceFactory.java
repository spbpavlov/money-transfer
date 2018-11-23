package com.revolut.transfer.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceFactory {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:h2:mem:prod;INIT=runscript from 'classpath:schema.sql'");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");
        ds = new HikariDataSource(config);
    }

    private DataSourceFactory() {}

    public static DataSource getDataSource() {
        return ds;
    }

}
