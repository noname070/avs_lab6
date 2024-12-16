package p3111.redgry.lab7.collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

/**
 * Модель локации.
 */
@AllArgsConstructor
@Getter
@Setter
public class Location implements Serializable {
    private double x;
    private Double y; // Поле не может быть null
    private double z;
    private String name; // Поле не может быть null

    public void setY(@NonNull Double y) {
        this.y = y;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }
}
