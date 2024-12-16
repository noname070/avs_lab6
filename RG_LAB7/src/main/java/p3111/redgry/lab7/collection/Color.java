package p3111.redgry.lab7.collection;

import java.util.Arrays;

import lombok.Getter;
import p3111.redgry.lab7.exceptions.InvalidInputException;

/**
 * The enum Color.
 */
@Getter
public enum Color {
    GREEN("Зеленый", 0),
    BLACK("Черный", 1),
    YELLOW("Желтый", 2),
    ORANGE("Оранжевый", 3),
    WHITE("Белый", 4),
    RED("Красный", 5);

    private String rus;
    private int id;

    Color(String rus, int id) {
        this.rus = rus;
        this.id = id;
    }

    public static Color byOrdinal(String input) {
        try {
            int id = Integer.parseInt(input);
            return Arrays.stream(Color.values())
                    .filter(color -> color.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new InvalidInputException("Не найден вид, соответствующей строке: " + input));
        } catch (NumberFormatException e) {
            
            try {
                return Color.valueOf(input);
            } catch (Exception e2) {
                // e.addSuppressed(e2);

                return Arrays.stream(Color.values())
                    .filter(color -> color.getRus().equalsIgnoreCase(input))
                    .findFirst()
                    .orElseThrow(() -> new InvalidInputException("Не найден вид, соответствующей строке: " + input));
            }

        }
    }

    public static boolean checkColor(String color) {
        return Arrays.stream(Color.values())
                .anyMatch(i -> i.getRus().equalsIgnoreCase(color));
    }

    public static boolean checkId(String idColor) {
        try {
            int id = Integer.parseInt(idColor);
            return Arrays.stream(Color.values())
                    .anyMatch(i -> i.getId() == id);
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
