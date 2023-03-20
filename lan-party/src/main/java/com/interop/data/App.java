package com.interop.data;

import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class App {
    public static void main(String[] args) throws SQLException, ParserConfigurationException, TransformerException {

        Database database = new Database();

        Connection conn = database.getConnection();

        Filler filler = new Filler(conn);

        filler.createTables();

        filler.insertData();

        System.out.println("Done!");

    }
}
