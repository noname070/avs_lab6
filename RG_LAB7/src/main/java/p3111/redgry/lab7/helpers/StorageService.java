package p3111.redgry.lab7.helpers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import p3111.redgry.lab7.collection.Person;

/**
 * Интерфейс для сервиса, который будет отвечать за всю бизнес-логику необходимую для приложения.
 */
public interface StorageService {

    String info();

    String show();

    int size();

    void clear();

    boolean removeById(long id);

    boolean removeByKey(long key);

    List<String> removeGreaterKey(long key);

    List<Long> removeAnyByBirthday(String date);

    boolean update(long id, Person person);

    boolean checkKey(long key);

    String CountLessThanLocation(double v);

    void add(Person person, Long key);

    void save(String pathToFile) throws IOException;

    String display();

    Person returnPerson();

    void addAll(Map<Long, Person> map);

    Map<Long, Person> list();
}
