package com.interop.data;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.datafaker.Faker;

public class Generator {

    private static Faker faker = new Faker();
    private static Random rand = new Random();

    private Generator() {
    }

    public static LocalDate getPartyDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 4);
        Date start = cal.getTime();
        cal.add(Calendar.MONTH, 80);
        Date end = cal.getTime();
        Date fk = faker.date().between(start, end);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(fk.toString().substring(0, 10), f);
        return date;
    }

    public static String getAddress() {
        return faker.address().fullAddress() + faker.address().country();
    }

    public static String getGames() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document xml = builder.newDocument();

        Element games = xml.createElement("games");
        xml.appendChild(games);

        for (int i = 0; i <= rand.nextInt(20) + 1; i++) {
            Element game = xml.createElement("game");
            Element name = xml.createElement("name");
            name.setTextContent(faker.gameOfThrones().character());
            game.appendChild(name);
            Element info = xml.createElement("info");
            game.appendChild(info);
            Element director = xml.createElement("director");
            director.setTextContent(faker.name().fullName());
            info.appendChild(director);
            Element score = xml.createElement("score");
            score.setTextContent(String.valueOf(rand.nextDouble()));
            info.appendChild(score);
            games.appendChild(game);
        }
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(xml), new StreamResult(writer));

        return writer.getBuffer().toString();
    }

    public static String getNote() {
        return faker.yoda().quote();
    }

    public static String getTown() {
        return faker.address().cityName();
    }

    public static String getVenueType() {
        List<String> types = new ArrayList<>();
        types.add("House");
        types.add("Condo");
        types.add("Apartment");
        types.add("Studio");
        types.add("Skyscraper");
        return types.get(rand.nextInt(types.size()));
    }

    public static String getJsonPerson() {
        String jsonPerson = new JSONObject()
                .put("person",
                        new JSONObject()
                                .put("name",
                                        new JSONObject().put("first-name", faker.name().firstName()).put("last-name",
                                                faker.name().lastName()))
                                .put("address", faker.address().fullAddress()))
                .toString();
        return jsonPerson;
    }

    public static String getFood() {
        return faker.food().dish();
    }

    public static String getGameName() {
        return faker.name().fullName() + "'s" + "Game";
    }

    public static String getGameGenre() {
        return faker.name().fullName() + "'s" + "Genre";
    }

    public static String getFirstName() {
        return faker.name().firstName();
    }

    public static String getLastName() {
        return faker.name().lastName();
    }

    public static String getRoomName() {
        return faker.name().firstName() + "'s Room";
    }

    public static String getNickname() {
        return faker.name().username();
    }

    public static String getGameMode() {
        return faker.gameOfThrones().character() + "'s Mode";
    }
}
