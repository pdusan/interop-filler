package com.interop.data;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

public class Database {

    private DataSource datasource;
    private Connection connection;

    public Database() throws SQLException {
        this.datasource = createDatasource();
        this.connection = datasource.getConnection();
    }

    private static DataSource createDatasource() {
        final String url = "jdbc:postgresql://localhost:8001/";
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(url);
        dataSource.setUser("interop");
        dataSource.setPassword("interop");
        dataSource.setDatabaseName("interop");
        return dataSource;
    }

    public DataSource getDataSource() {
        return this.datasource;
    }

    public Connection getConnection() {
        return this.connection;
    }
}
