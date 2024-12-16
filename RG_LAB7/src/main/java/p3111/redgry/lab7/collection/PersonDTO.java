package p3111.redgry.lab7.collection;

import com.opencsv.CSVParserBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import p3111.redgry.lab7.exceptions.InvalidInputException;
import p3111.redgry.lab7.utils.UserInterface;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@NoArgsConstructor
@Getter
@Setter
public class PersonDTO implements Comparable<PersonDTO> {
    private Vector<PersonDTO> labWorks = new Vector<>();

    private long id; // Значение должно быть > 0 и уникальным.
    private String name; // Поле не может быть null или пустым.
    private Coordinates coordinates; // Поле не может быть null.
    private LocalDate creationDate; // Автоматически генерируемое поле, не может быть null.
    private Long height; // Поле может быть null, значение > 0.
    private LocalDate birthday; // Поле может быть null.
    private String passportID; // Поле может быть null.
    private Color hairColor; // Поле не может быть null.
    private Location location; // Поле не может быть null.

    public PersonDTO(Person person) {
        this.id = person.getId();
        this.name = person.getName();
        this.coordinates = person.getCoordinates();
        this.creationDate = person.getCreationDate();
        this.height = person.getHeight();
        this.birthday = person.getBirthday();
        this.passportID = person.getPassportID();
        this.hairColor = person.getHairColor();
        this.location = person.getLocation();
    }

    public PersonDTO(Map.Entry<Long, Person> longPersonEntry) {
        this(longPersonEntry.getValue());
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate != null ? LocalDate.parse(creationDate) : LocalDate.now();
    }

    public void checkFields() {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Поле 'name' не может быть пустым.");
        }
        if (!UserInterface.checkNumber(height, 0, -1) || id <= 0) {
            throw new InvalidInputException(
                    "Значения 'Height' или 'ID' не соответствуют ограничениям. (Person)");
        }
    }

    public String[] toCSVArray() {
        return labWorks.stream()
                .map(PersonDTO::toCSVLine)
                .toArray(String[]::new);
    }

    @Override
    public int compareTo(PersonDTO other) {
        return Long.compare(this.id, other.id);
    }

    public String toCSVLine() {
        return new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(true)
                .withQuoteChar('"')
                .build()
                .parseToLine(toArray(), false);
    }

    private String[] toArray() {
        return List.of(
                safeToString(name),
                safeToString(coordinates.getX()),
                safeToString(coordinates.getY()),
                safeToString(height),
                safeToString(passportID),
                safeToString(hairColor),
                safeToString(location.getX()),
                safeToString(location.getY()),
                safeToString(location.getZ()),
                safeToString(location.getName()),
                safeToString(id),
                safeToString(creationDate)).toArray(new String[0]);
    }

    private String safeToString(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
