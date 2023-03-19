package com.interop.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Filler {

    private Connection connection;

    public Filler(Connection connection) {
        this.connection = connection;
    }

    public void createTables() throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            stmt.executeUpdate("DROP SCHEMA public CASCADE");
        } catch (Exception e) {
        } finally {
            stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS public");

            stmt.executeUpdate("""
                    CREATE TABLE public.games
                    (
                        name character varying(256) COLLATE pg_catalog."default" NOT NULL,
                        tester character varying(256) COLLATE pg_catalog."default",
                        length character varying(256) COLLATE pg_catalog."default",
                        size real NOT NULL,
                        players integer NOT NULL,
                        review_score real NOT NULL,
                        date date NOT NULL,
                        CONSTRAINT games_pkey PRIMARY KEY (name)
                    )""");
            stmt.executeUpdate("""
                    CREATE TABLE public.friends
                    (
                       first_name character varying(256) NOT NULL,
                       last_name character varying(256) NOT NULL,
                       address character varying(256) NOT NULL,
                       beer_preference real NOT NULL,
                       liked_games xml,
                       age integer,
                       inv_date date NOT NULL,
                       inv_address character varying(256) NOT NULL,
                       party_date date NOT NULL,
                       PRIMARY KEY (first_name, last_name, address)
                    ) ;
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE public.invitations
                        (
                            date date NOT NULL,
                            address character varying(256) NOT NULL,
                            available_games xml NOT NULL,
                            attending boolean NOT NULL,
                            special_note character varying(256),
                            respond_by date NOT NULL,
                            PRIMARY KEY (date, address)
                        );
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE public.likesgames
                        (
                            name character varying(256) NOT NULL,
                            first_name character varying(256) NOT NULL,
                            last_name character varying(256) NOT NULL,
                            address character varying(256) NOT NULL,
                            PRIMARY KEY (name, first_name, last_name, address)
                        );
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE public.venue
                        (
                            address character varying(256) NOT NULL,
                            postal_code integer NOT NULL,
                            town character varying(256) NOT NULL,
                            parking_options boolean NOT NULL,
                            type character varying(256) NOT NULL,
                            owner json NOT NULL,
                            PRIMARY KEY (address, postal_code, town)
                        );
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE public.room
                        (
                            name character varying(256) NOT NULL,
                            address character varying(256) NOT NULL,
                            accessories character varying(256) NOT NULL,
                            has_neighbor boolean NOT NULL,
                            size real NOT NULL,
                            tables integer NOT NULL,
                            outlets integer NOT NULL,
                            PRIMARY KEY (name, address)
                        );
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE public.party
                        (
                            date date NOT NULL,
                            address character varying(256) NOT NULL,
                            attendees integer NOT NULL,
                            beer real NOT NULL,
                            host json NOT NULL,
                            games xml NOT NULL,
                            food character varying(256),
                            PRIMARY KEY (date, address)
                        );
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE public.matches
                        (
                            winner character varying(256) NOT NULL,
                            date date NOT NULL,
                            duration real NOT NULL,
                            participants integer NOT NULL,
                            mode character varying(256),
                            score real,
                            loser character varying(256),
                            PRIMARY KEY (winner, date)
                        );
                    """);
        }
    }
}
