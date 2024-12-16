package p3111.redgry.lab7.helpers;

import java.util.Date;
import java.util.Map;

/**
 * Интерфейс для коллекции.
 *
 * @param <K> ключ элемента коллекции.
 * @param <T> тип элементов коллекции.
 */
public interface Storage<K,T> {

    void clear();

    Date getInitializationTime();

    Class<?> getCollectionClass();

    int size();

    Map<K, T> toList();

    void put(K key, T person);

    void putAll(Map<K, T> map);

    Map<K, T> getPersons();

    void remove(K aLong, T person);
}
