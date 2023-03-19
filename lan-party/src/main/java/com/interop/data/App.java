package com.interop.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

public class App {

    private static DataSource createDatasource() {
        final String url = "jdbc:postgresql://6294f0341f49:5432/javatest";
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(url);
        return dataSource;
    }

    public static void main(String[] args) throws SQLException {

        DataSource dataSource = createDatasource();

        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM birds");

        ResultSet res = stmt.executeQuery();

        while (res.next()) {
            System.out.printf("id:%d bird:%s description:%s%n", res.getLong("id"),
                    res.getString("bird"), res.getString("description"));
        }

        System.out.println("Done!");

    }
}
