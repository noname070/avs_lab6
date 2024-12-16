package p3111.redgry.lab7.exceptions;

/**
 * Исключение, обозначающее неправильный ввод данных.
 */
public class InvalidInputException extends RuntimeException{

    public InvalidInputException(String message){
        super(message);
    }
}
