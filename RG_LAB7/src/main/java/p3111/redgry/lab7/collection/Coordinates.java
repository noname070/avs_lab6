package p3111.redgry.lab7.collection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import p3111.redgry.lab7.exceptions.IncorrectValueException;

import java.io.Serializable;

/**
 * Модель координат
 */

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Coordinates implements Serializable {
    Long x; // Поле не может быть null
    Double y; // Значение поля должно быть больше -537, Поле не может быть null

    public Coordinates(Long x, Double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "x = " + x + ", y = " + y;
    }

    public Long getX() {
        return x;
    }

    public void setX(@NonNull Long x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(@NonNull Double y) throws IncorrectValueException {
        if (y >= -537) {
            this.y = y;
        } else
            throw new IncorrectValueException("Число не должно быть меньше меньше или равно -537");
    }
}
