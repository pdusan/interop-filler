package com.interop.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class Filler {

    private Connection connection;

    private Random rand = new Random();

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
                        name character varying(256) NOT NULL,
                        genre character varying(256),
                        length character varying(256),
                        size real NOT NULL,
                        players integer NOT NULL,
                        review_score real NOT NULL,
                        date date NOT NULL,
                        PRIMARY KEY (name)
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
                    CREATE TABLE public.venues
                        (
                            address character varying(256) UNIQUE NOT NULL,
                            postal_code integer NOT NULL,
                            town character varying(256) NOT NULL,
                            parking_options boolean NOT NULL,
                            type character varying(256) NOT NULL,
                            owner json NOT NULL,
                            PRIMARY KEY (address, postal_code, town)
                        );
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE public.rooms
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
                    CREATE TABLE public.parties
                        (
                            date date UNIQUE NOT NULL,
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

            stmt.executeUpdate("ALTER TABLE games ADD CONSTRAINT fk_date FOREIGN KEY(date) REFERENCES parties(date);");
            stmt.executeUpdate(
                    "ALTER TABLE friends ADD CONSTRAINT fk_date FOREIGN KEY(party_date) REFERENCES parties(date)");
            stmt.executeUpdate(
                    "ALTER TABLE friends ADD CONSTRAINT fk_inv FOREIGN KEY(inv_date, inv_address) REFERENCES invitations(date, address)");
            stmt.executeUpdate(
                    "ALTER TABLE likesgames ADD CONSTRAINT fk_name FOREIGN KEY(name) REFERENCES games(name)");
            stmt.executeUpdate(
                    "ALTER TABLE likesgames ADD CONSTRAINT fk_friend FOREIGN KEY(first_name, last_name, address) REFERENCES friends(first_name, last_name, address)");
            stmt.executeUpdate(
                    "ALTER TABLE rooms ADD CONSTRAINT fk_address FOREIGN KEY(address) REFERENCES venues(address)");
            stmt.executeUpdate(
                    "ALTER TABLE parties ADD CONSTRAINT fk_address FOREIGN KEY(address) REFERENCES venues(address)");
            stmt.executeUpdate("ALTER TABLE matches ADD CONSTRAINT fk_date FOREIGN KEY(date) REFERENCES parties(date)");
        }
    }

    public void insertData() throws SQLException, ParserConfigurationException, TransformerException {
        // INSERT INTO INVITATIONS
        for (int i = 0; i < 1000; i++) {
            SQLXML xml = connection.createSQLXML();
            xml.setString(Generator.getGames());
            String ins = "INSERT INTO invitations(date, address, available_games, attending, special_note, respond_by) VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS);
            stmt.setObject(1, Generator.getPartyDate(), java.sql.Types.DATE);
            stmt.setString(2, Generator.getAddress());
            stmt.setSQLXML(3, xml);
            stmt.setBoolean(4, rand.nextBoolean());
            stmt.setString(5, Generator.getNote());
            stmt.setObject(6, Generator.getPartyDate(), java.sql.Types.DATE);

            try {

                stmt.executeUpdate();
            } catch (Exception e) {
                continue;
            }
        }

        // INSERT INTO VENUES
        for (int i = 0; i < 1000; i++) {

            String ins = "INSERT INTO venues(address, postal_code, town, parking_options, type, owner) VALUES(?, ?, ?, ?, ?, to_json(?::json))";
            PreparedStatement stmt = connection.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, Generator.getAddress());
            stmt.setInt(2, rand.nextInt(5000) + 5000);
            stmt.setString(3, Generator.getTown());
            stmt.setBoolean(4, rand.nextBoolean());
            stmt.setString(5, Generator.getVenueType());
            stmt.setString(6, Generator.getJsonPerson());

            try {

                stmt.executeUpdate();
            } catch (Exception e) {
                continue;
            }
        }

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT address FROM venues");
        List<String> addresses = new ArrayList<>();
        while (rs.next()) {
            addresses.add(rs.getString("address"));
        }
        rs.close();
        st.close();
        // INSERT INTO PARTIES
        for (int i = 0; i < 1000; i++) {
            SQLXML xml = connection.createSQLXML();
            xml.setString(Generator.getGames());
            String ins = "INSERT INTO parties(date, address, attendees, beer, host, games, food) VALUES(?, ?, ?, ?, to_json(?::json), ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS);
            stmt.setObject(1, Generator.getPartyDate(), java.sql.Types.DATE);
            stmt.setString(2, addresses.get(rand.nextInt(addresses.size())));
            stmt.setInt(3, rand.nextInt(2) + 8);
            stmt.setDouble(4, rand.nextDouble() + 18 + rand.nextInt(2));
            stmt.setString(5, Generator.getJsonPerson());
            stmt.setSQLXML(6, xml);
            stmt.setString(7, Generator.getFood());

            try {

                stmt.executeUpdate();
            } catch (Exception e) {
                continue;
            }
        }

        st = connection.createStatement();
        rs = st.executeQuery("SELECT date FROM parties");
        List<LocalDate> dates = new ArrayList<>();
        while (rs.next()) {
            dates.add(LocalDate.parse(rs.getString("date")));
        }
        st.close();
        rs.close();
        // INSERT INTO GAMES
        for (int i = 0; i < 1000; i++) {
            String ins = "INSERT INTO games(name, genre, length, size, players, review_score, date) VALUES(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, Generator.getGameName());
            stmt.setString(2, Generator.getGameGenre());
            stmt.setString(3, String.valueOf(rand.nextInt(15) + 5) + "Hours ");
            stmt.setDouble(4, rand.nextDouble() + 20 + rand.nextInt(10));
            stmt.setInt(5, rand.nextInt(6) + 1);
            stmt.setDouble(6, rand.nextDouble() + rand.nextInt(10));
            stmt.setObject(7, dates.get(rand.nextInt(dates.size())), java.sql.Types.DATE);

            try {

                stmt.executeUpdate();
            } catch (Exception e) {
                continue;
            }
        }

        st = connection.createStatement();
        List<LocalDate> partyDate = new ArrayList<>();
        Map<LocalDate, String> invDetails = new HashMap<>();
        rs = st.executeQuery("SELECT date FROM parties");
        while (rs.next()) {
            partyDate.add(LocalDate.parse(rs.getString("date")));
        }
        rs = st.executeQuery("SELECT date, address FROM invitations");
        while (rs.next()) {
            invDetails.put(LocalDate.parse(rs.getString("date")), rs.getString("address"));
        }
        rs.close();
        st.close();
        List<LocalDate> mapDates = new ArrayList<LocalDate>(invDetails.keySet());
        // INSERT INTO FRIENDS
        for (int i = 0; i < 1000; i++) {
            LocalDate randDate = mapDates.get(rand.nextInt(mapDates.size()));
            SQLXML xml = connection.createSQLXML();
            xml.setString(Generator.getGames());
            String ins = "INSERT INTO friends(first_name, last_name, address, beer_preference, liked_games, age, inv_date, inv_address, party_date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, Generator.getFirstName());
            stmt.setString(2, Generator.getLastName());
            stmt.setString(3, Generator.getAddress());
            stmt.setDouble(4, rand.nextDouble() + rand.nextInt(3));
            stmt.setSQLXML(5, xml);
            stmt.setInt(6, rand.nextInt(8) + 20);
            stmt.setObject(7, randDate, java.sql.Types.DATE);
            stmt.setString(8, invDetails.get(randDate));
            stmt.setObject(9, partyDate.get(rand.nextInt(partyDate.size())), java.sql.Types.DATE);

            try {

                stmt.executeUpdate();
            } catch (Exception e) {
                continue;
            }
        }

        st = connection.createStatement();
        rs = st.executeQuery("SELECT name FROM games");
        List<String> gameNames = new ArrayList<>();
        while (rs.next()) {
            gameNames.add(rs.getString("name"));
        }
        rs = st.executeQuery("SELECT first_name, last_name, address FROM friends");
        // INSERT INTO LIKESGAMES
        for (int i = 0; i < 1000; i++) {

            String ins = "INSERT INTO likesgames(name, first_name, last_name, address) VALUES(?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, gameNames.get(rand.nextInt(gameNames.size())));
            if (rs.next()) {
                stmt.setString(2, rs.getString("first_name"));
                stmt.setString(3, rs.getString("last_name"));
                stmt.setString(4, rs.getString("address"));
            }

            try {

                stmt.executeUpdate();
            } catch (Exception e) {
                continue;
            }
        }
        rs.close();
        st.close();

        st = connection.createStatement();
        rs = st.executeQuery("SELECT address FROM venues");
        List<String> venueAddrs = new ArrayList<>();
        while (rs.next()) {
            venueAddrs.add(rs.getString("address"));
        }
        rs.close();
        st.close();
        // INSERT INTO ROOMS
        for (int i = 0; i < 1000; i++) {
            String ins = "INSERT INTO rooms(name, address, accessories, has_neighbor, size, tables, outlets) VALUES(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, Generator.getRoomName());
            stmt.setString(2, venueAddrs.get(rand.nextInt(venueAddrs.size())));
            stmt.setString(3, "Accessorie " + String.valueOf(rand.nextInt(20)));
            stmt.setBoolean(4, rand.nextBoolean());
            stmt.setDouble(5, rand.nextDouble() + 15 + rand.nextInt(5));
            stmt.setInt(6, rand.nextInt(5));
            stmt.setInt(7, rand.nextInt(10) + 5);

            try {

                stmt.executeUpdate();
            } catch (Exception e) {
                continue;
            }
        }

        // INSRT INTO MATCHES
        for (int i = 0; i < 1000; i++) {
            String ins = "INSERT INTO matches(winner, date, duration, participants, mode, score, loser) VALUES(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, Generator.getNickname());
            stmt.setObject(2, dates.get(rand.nextInt(dates.size())), java.sql.Types.DATE);
            stmt.setDouble(3, rand.nextDouble() + 10 + rand.nextInt(4));
            stmt.setInt(4, rand.nextInt(10));
            stmt.setString(5, Generator.getGameMode());
            stmt.setDouble(6, rand.nextDouble() + 200 + rand.nextInt(100));
            stmt.setString(7, Generator.getNickname());

            try {

                stmt.executeUpdate();
            } catch (Exception e) {
                continue;
            }
        }
    }
}
