package p3111.redgry.lab7.utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.LinkedHashMap;
import java.util.Map;

import p3111.redgry.lab7.Config;
import p3111.redgry.lab7.collection.*;
import p3111.redgry.lab7.helpers.StorageService;

import lombok.extern.log4j.Log4j2;

/* обновленная initdb:

    DROP TABLE IF EXISTS users CASCADE;
    DROP TABLE IF EXISTS person CASCADE;
    DROP TABLE IF EXISTS coordinates CASCADE;
    DROP TABLE IF EXISTS location CASCADE;

    CREATE TABLE users
    (
        id         SERIAL              PRIMARY KEY,
        username   VARCHAR(255)        NOT NULL UNIQUE
    );

    CREATE TABLE person
    (
        id              SERIAL         PRIMARY KEY,
        name            VARCHAR(255)   NOT NULL,
        user_id         INT            NOT NULL,
        creationDate    DATE           NOT NULL,
        height          BIGINT         NOT NULL,
        passportID      VARCHAR(255)   NOT NULL UNIQUE,
        hairColor       VARCHAR(255),
        birthday        DATE           NOT NULL,
        coordinates_id  INT, 
        location_id     INT, 

        FOREIGN KEY (user_id)
            REFERENCES users (id) 
                    ON DELETE CASCADE,
        FOREIGN KEY (coordinates_id)
            REFERENCES coordinates (id)
                    ON DELETE SET NULL,
        FOREIGN KEY (location_id)
            REFERENCES location (id)
                    ON DELETE SET NULL
    );

    CREATE TABLE coordinates
    (
        id    SERIAL               PRIMARY KEY,
        x     BIGINT               NOT NULL,
        y     DOUBLE PRECISION     NOT NULL
    );

    CREATE TABLE location
    (
        id      SERIAL            PRIMARY KEY,
        name    VARCHAR(255),
        x       DOUBLE PRECISION,
        y       DOUBLE PRECISION,
        z       DOUBLE PRECISION
    );
 */

@Log4j2
public class DataBaseManagerv2 {

    private String login;
    private String password;

    public DataBaseManagerv2() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            // log.error("Ошибка загрузки драйвера Postgresql", e);
            System.err.println("Ошибка загрузки драйвера Postgresql");
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    public DataBaseManagerv2(String login, String password) {
        this();
        this.login = login;
        this.password = password;
    }

    public boolean login() {
        try (ResultSet rs = DriverManager
                .getConnection(Config.DB_PG_URL, this.login, this.password)
                .createStatement()
                .executeQuery("SELECT * FROM person")) {
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка при авторизации");
            e.printStackTrace(System.err);
            return false;

        }
    }

    public boolean login(String login, String password) {
        this.login = login;
        this.password = password;
        return this.login();
    }

    public static boolean addUser(String login, String password) {
        try (Connection connection = DriverManager.getConnection(
                Config.DB_PG_URL,
                Config.DB_ROOT_USER,
                Config.DB_ROOT_PASSWORD)) {
            connection.setAutoCommit(false);

            String createRoleQuery = String.format(
                    "CREATE ROLE \"%s\" WITH LOGIN NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION PASSWORD '%s';",
                    login, password);

            try (PreparedStatement createRoleStmt = connection.prepareStatement(createRoleQuery)) {
                createRoleStmt.executeUpdate();
            }

            String grantPermissionsQuery = String.format("""
                GRANT USAGE, SELECT ON TABLE person_id_seq TO \"%s\";
                GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE person TO \"%s\";

                GRANT USAGE, SELECT ON TABLE coordinates_id_seq TO \"%s\";
                GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE coordinates TO \"%s\";

                GRANT USAGE, SELECT ON TABLE location_id_seq TO \"%s\";
                GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE location TO \"%s\";
                """, login, login, login, login, login, login, login, login);

            try (PreparedStatement grantPermissionsStmt = connection.prepareStatement(grantPermissionsQuery)) {
                grantPermissionsStmt.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Ошибка при записи нового пользователя в БД");
            e.printStackTrace(System.err);
            return false;
        }
    }

    public boolean addElementToDb(Person person) {
        try (Connection connection = DriverManager.getConnection(Config.DB_PG_URL, login, password)) {

            int coordinatesId = insertCoordinates(connection, person.getCoordinates());
            int locationId = insertLocation(connection, person.getLocation());

            PreparedStatement personStmt = connection.prepareStatement(
                    "INSERT INTO person (name, creationDate, height, passportID, hairColor, birthday, coordinates_id, location_id) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            personStmt.setString(1, person.getName());
            // personStmt.setString(2, login);
            personStmt.setDate(2, Date.valueOf(person.getCreationDate()));
            personStmt.setLong(3, person.getHeight());
            personStmt.setString(4, person.getPassportID());
            personStmt.setString(5, person.getHairColor() != null ? person.getHairColor().toString() : null);
            personStmt.setDate(6, person.getBirthday() != null ? Date.valueOf(person.getBirthday()) : null);
            personStmt.setInt(7, coordinatesId);
            personStmt.setInt(8, locationId);

            personStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // log.error("Ошибка при добавлении элемента в базу данных", e);
            System.err.println("Ошибка при добавлении элемента в базу данных");
            e.printStackTrace(System.err);
            return false;
        }
    }

    public boolean updateElementInDataBase(Person person) {
        try (Connection connection = DriverManager.getConnection(Config.DB_PG_URL, this.login, this.password)) {

            PreparedStatement coordinatesStmt = connection.prepareStatement(
                    "UPDATE coordinates SET x = ?, y = ? WHERE id = " +
                            "(SELECT coordinates_id FROM person WHERE id = ?)");
            coordinatesStmt.setLong(1, person.getCoordinates().getX());
            coordinatesStmt.setDouble(2, person.getCoordinates().getY());
            coordinatesStmt.setLong(3, person.getId());
            coordinatesStmt.executeUpdate();

            PreparedStatement locationStmt = connection.prepareStatement(
                    "UPDATE location SET name = ?, x = ?, y = ?, z = ? WHERE id = " +
                            "(SELECT location_id FROM person WHERE id = ?)");
            locationStmt.setString(1, person.getLocation().getName());
            locationStmt.setDouble(2, person.getLocation().getX());
            locationStmt.setDouble(3, person.getLocation().getY());
            locationStmt.setDouble(4, person.getLocation().getZ());
            locationStmt.setLong(5, person.getId());
            locationStmt.executeUpdate();

            PreparedStatement personStmt = connection.prepareStatement(
                    "UPDATE person SET name = ?, height = ?, passportID = ?, hairColor = ?, birthday = ? " +
                            "WHERE id = ?");
            personStmt.setString(1, person.getName());
            personStmt.setLong(2, person.getHeight());
            personStmt.setString(3, person.getPassportID());
            personStmt.setString(4, person.getHairColor() != null ? person.getHairColor().toString() : null);
            personStmt.setDate(5, person.getBirthday() != null ? Date.valueOf(person.getBirthday()) : null);
            personStmt.setLong(6, person.getId());
            personStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            // log.error("Ошибка при обновлении элемента в базе данных", e);
            System.err.println("Ошибка при обновлении элемента в базе данных");
            e.printStackTrace(System.err);
            return false;
        }
    }

    public boolean removeFromDataBase(Person person) {
        try (Connection connection = DriverManager.getConnection(Config.DB_PG_URL, this.login, this.password)) {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM person WHERE id = ? AND user_id = (SELECT id FROM users WHERE username = ?)");
            stmt.setLong(1, person.getId());
            stmt.setString(2, this.login);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            log.error("Ошибка при удалении элемента из базы данных", e);
            System.err.println("Ошибка при удалении элемента из базы данных");
            e.printStackTrace(System.err);
            return false;
        }
    }

    public static void updateCollectionFromDataBase(StorageService storageService) {
        Map<Long, Person> data = new LinkedHashMap<>();
        try (Connection connection = DriverManager.getConnection(
                Config.DB_PG_URL,
                Config.DB_ROOT_USER,
                Config.DB_ROOT_PASSWORD)) {
            
            Statement stmt = connection.createStatement();
            
            ResultSet rs = stmt.executeQuery(
                    "SELECT p.id, p.name, p.creationDate, p.height, p.passportID, p.hairColor, p.birthday, " +
                            "c.x AS coordX, c.y AS coordY, " +
                            "l.name AS locationName, l.x AS locX, l.y AS locY, l.z AS locZ " +
                            "FROM person p " +
                            "JOIN coordinates c ON p.coordinates_id = c.id " +
                            "JOIN location l ON p.location_id = l.id");

            while (rs.next()) {

                Person person = new Person(
                        rs.getString("name"),

                        new Coordinates(
                                rs.getLong("coordX"),
                                rs.getDouble("coordY")),

                        rs.getLong("height"),

                        LocalDate.parse(
                                rs.getString("birthday"),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd")),

                        rs.getString("passportID"),

                        Color.byOrdinal(rs.getString("hairColor")),

                        new Location(
                                rs.getDouble("locX"),
                                rs.getDouble("locY"),
                                rs.getDouble("locZ"),
                                rs.getString("locationName"))

                );
                person.setId(rs.getLong("id"));
                person.setCreationDate(rs.getString("creationDate"));

                data.put(person.getId(), person);
            }

            storageService.clear();
            storageService.addAll(data);
        } catch (SQLException e) {
            // log.error("Ошибка при обновлении коллекции из базы данных", e);
            System.err.println("Ошибка при обновлении коллекции из базы данных");
            e.printStackTrace(System.err);
        }
    }

    private int insertCoordinates(Connection connection, Coordinates coordinates) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO coordinates (x, y) VALUES (?, ?) RETURNING id");
        stmt.setLong(1, coordinates.getX());
        stmt.setDouble(2, coordinates.getY());
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getInt("id");
    }

    private int insertLocation(Connection connection, Location location) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO location (name, x, y, z) VALUES (?, ?, ?, ?) RETURNING id");
        stmt.setString(1, location.getName());
        stmt.setDouble(2, location.getX());
        stmt.setDouble(3, location.getY());
        stmt.setDouble(4, location.getZ());
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getInt("id");
    }

}
