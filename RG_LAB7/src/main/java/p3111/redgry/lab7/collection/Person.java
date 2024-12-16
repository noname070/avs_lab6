package p3111.redgry.lab7.collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import p3111.redgry.lab7.helpers.StackPersonStorage;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Модель персонажей
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Person implements Comparable<Object>, Serializable {
    private long id; // Значение поля должно быть больше 0, Значение этого поля должно быть
                     // уникальным, Значение этого поля должно генерироваться автоматически
    private String name; // Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; // Поле не может быть null
    private java.time.LocalDate creationDate; // Поле не может быть null, Значение этого поля должно генерироваться
                                              // автоматически
    private Long height; // Поле может быть null, Значение поля должно быть больше 0
    private java.time.LocalDate birthday; // Поле может быть null
    private String passportID; // Поле может быть null
    private Color hairColor; // Поле не может быть null
    private Location location; // Поле не может быть null
    private String createdByUser;

    
    public void setCreationByUser(String s) {
        this.createdByUser = s;
    }

    public Person(String name,
            Coordinates coordinates,
            Long height,
            LocalDate birthday,
            String passportID,
            Color hairColor,
            Location location) {

        this.id = StackPersonStorage.numberGenerate();
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDate.now();
        this.height = height;
        this.birthday = birthday;
        this.passportID = passportID;
        this.hairColor = hairColor;
        this.location = location;
    }

    public Person(long id,
            String name,
            Coordinates coordinates,
            Long height,
            LocalDate birthday,
            String passportID,
            Color hairColor,
            Location location) {
                
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDate.now();
        this.height = height;
        this.birthday = birthday;
        this.passportID = passportID;
        this.hairColor = hairColor;
        this.location = location;
    }

    public Person(Person o) {
        this.id = o.getId();
        this.name = o.getName();
        this.coordinates = o.getCoordinates();
        this.creationDate = LocalDate.now();
        this.height = o.getHeight();
        this.birthday = o.getBirthday();
        this.passportID = o.getPassportID();
        this.hairColor = o.getHairColor();
        this.location = o.getLocation();
    }

    public void setCreationDate(String creationDate) {
        if (creationDate != null) {
            this.creationDate = LocalDate.parse(creationDate);
        } else {
            this.creationDate = LocalDate.now();
        }
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return -1;
        }
        if (!(o instanceof Person)) {
            throw new ClassCastException();
        }
        Person person = (Person) o;
        return (int) (this.getHeight() - person.getHeight());
    }

}
