package com.interop.data;

import java.sql.Connection;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException {

        Database database = new Database();

        Connection conn = database.getConnection();

        Filler filler = new Filler(conn);

        filler.createTables();

        System.out.println("Done!");

    }
}
