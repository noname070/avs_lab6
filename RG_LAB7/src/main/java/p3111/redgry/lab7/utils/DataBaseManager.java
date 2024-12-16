package p3111.redgry.lab7.utils;

import p3111.redgry.lab7.Config;
import p3111.redgry.lab7.collection.Color;
import p3111.redgry.lab7.collection.Coordinates;
import p3111.redgry.lab7.collection.Location;
import p3111.redgry.lab7.collection.Person;
import p3111.redgry.lab7.helpers.StorageService;

import java.io.Serializable;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

import lombok.extern.log4j.Log4j2;


@Log4j2
/**
 * ну очень плохо работает, переписал свой
 * @see #linkDataBaseManagerv2
 * @deprecated
 */
public class DataBaseManager implements Serializable {
    private final String URL = Config.DB_PG_URL;
    private String USER = Config.DB_ROOT_USER;
    private String PASSWORD = Config.DB_ROOT_PASSWORD;

    public DataBaseManager() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
            log.error("Ошибка загрузки драйвера Postgresql", e);
            System.exit(-1);
        }
    }

    public boolean addUser(String login, String password) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "CREATE ROLE " + login + " WITH\n" +
                    "  LOGIN\n" +
                    "  NOSUPERUSER\n" +
                    "  INHERIT\n" +
                    "  NOCREATEDB\n" +
                    "  NOCREATEROLE\n" +
                    "  NOREPLICATION\n" +
                    "  ENCRYPTED PASSWORD '" + password + "';\n" +
                    "ALTER ROLE " + login + " SET password_encryption TO 'scram-sha-256';";
    
            Statement statement = connection.createStatement();
            statement.execute(query);
            return true;
        } catch (SQLException e) {
            log.error("Ошибка при записи нового пользователя в БД", e);
            return false;
        }
    }
    

    public boolean login(String login, String password) {
        USER = login;
        PASSWORD = password;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM person");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addToDataBase(Person person) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO person (\"name\", \"Coordinates (X)\", \"Coordinates (Y)\", \"creationDate\", height, \"passportID\", \"hairColor\", \"location name\", \"location (X)\", \"location (Y)\", \"location (Z)\", \"user\", birthday ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            //preparedStatement.setLong(1, person.getId());
            preparedStatement.setString(1, person.getName());
            preparedStatement.setLong(2, person.getCoordinates().getX());
            preparedStatement.setDouble(3, person.getCoordinates().getY());
            preparedStatement.setDate(4, Date.valueOf(person.getCreationDate()));
            preparedStatement.setLong(5, person.getHeight());
            preparedStatement.setString(6, person.getPassportID());
            preparedStatement.setString(7, person.getHairColor().getRus());
            preparedStatement.setString(8, person.getLocation().getName());
            preparedStatement.setDouble(9, person.getLocation().getX());
            preparedStatement.setDouble(10, person.getLocation().getY());
            preparedStatement.setDouble(11, person.getLocation().getZ());
            preparedStatement.setString(12, USER);
            preparedStatement.setDate(13, Date.valueOf(person.getBirthday()));
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            // e.printStackTrace();
            log.error("Ошибка при добавлении пользователя в БД", e);
            return false;
        }
    }

    public boolean addToDataBase1(Person person) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO person (id, \"name\", \"Coordinates (X)\", \"Coordinates (Y)\", \"creationDate\", height, \"passportID\", \"hairColor\", \"location name\", \"location (X)\", \"location (Y)\", \"location (Z)\", \"user\", birthday ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            preparedStatement.setLong(1, person.getId());
            preparedStatement.setString(2, person.getName());
            preparedStatement.setLong(3, person.getCoordinates().getX());
            preparedStatement.setDouble(4, person.getCoordinates().getY());
            preparedStatement.setDate(5, Date.valueOf(person.getCreationDate()));
            preparedStatement.setLong(6, person.getHeight());
            preparedStatement.setString(7, person.getPassportID());
            preparedStatement.setString(8, person.getHairColor().getRus());
            preparedStatement.setString(9, person.getLocation().getName());
            preparedStatement.setDouble(10, person.getLocation().getX());
            preparedStatement.setDouble(11, person.getLocation().getY());
            preparedStatement.setDouble(12, person.getLocation().getZ());
            preparedStatement.setString(13, USER);
            preparedStatement.setDate(14, Date.valueOf(person.getBirthday()));
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            // e.printStackTrace();
            log.error("Ошибка при добавлении пользователя в БД", e);
            return false;
        }
    }

    public boolean updateElementInDataBase(Person person) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            
            if (addToDataBase1(person)){
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE person SET id = " + person.getId() + " WHERE id = (SELECT MAX(id) FROM person);");
                preparedStatement.executeUpdate();
                return true;
            } else {
                // System.out.println("Не удалось обновить элемент, обновлённый элемент не может быть добавлен в БД");
                log.warn("Не удалось обновить элемент, обновлённый элемент не может быть добавлен в БД");
                
            }
        } catch (SQLException e) {
            // System.out.println("");
            log.error("Ошибка при добавлении в БД", e);
            return true;
        }
        return false;
    }

    public boolean removeFromDataBase(Person person){
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM person WHERE id = " + person.getId());
            while (resultSet.next()){
                if (!resultSet.getString("user").equals(USER)){
                    return false;
                }
            }
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM person WHERE id = " + person.getId() + " AND \"user\" = '" + USER + "'");
            preparedStatement.executeUpdate();
            return true;
        }catch (SQLException e){
            // e.printStackTrace();
            log.error("Ошибка при удалении пользователя из БД", e);
            return false;
        }
    }

    public void updateCollectionFromDataBase(StorageService storageService){
        LinkedHashMap<Long, Person> linkedHashMap = new LinkedHashMap<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM person");
            while (resultSet.next()){
                Person person = new Person(resultSet.getString("name"), new Coordinates(resultSet.getLong("Coordinates (X)"), resultSet.getDouble("Coordinates (Y)")), resultSet.getLong("height"), LocalDate.parse(resultSet.getString("birthday"), DateTimeFormatter.ofPattern("yyyy-MM-dd")), resultSet.getString("passportID"), Color.byOrdinal(resultSet.getString("hairColor")), new Location(resultSet.getDouble("location (X)"), resultSet.getDouble("location (Y)"), resultSet.getDouble("location (Z)"), resultSet.getString("location name")));
                person.setId(resultSet.getLong("id"));
                person.setCreationByUser(resultSet.getString("user"));
                person.setCreationDate(String.valueOf(resultSet.getDate("creationDate")));
                linkedHashMap.put(person.getId(), person);
            }
            storageService.clear();
            storageService.addAll(linkedHashMap);
        } catch (SQLException e){
            // e.printStackTrace();
            log.error("Ошибка при обновлении коллекции в БД", e);
        }
    }

    public void setUSER(String USER){
        this.USER = USER;
    }

    public void setPASSWORD(String PASSWORD){
        this.PASSWORD = PASSWORD;
    }
}