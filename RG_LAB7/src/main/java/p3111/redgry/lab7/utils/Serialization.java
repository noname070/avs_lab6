package p3111.redgry.lab7.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class Serialization implements Serializable {
    public static <T> byte[] SerializeObject(T object, String login, String password) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);) {
            objectOutputStream.writeUTF(login);
            objectOutputStream.writeUTF(password);
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            // System.out.println("Ошибка сериализации");
            // e.printStackTrace();
            log.error("Ошибка сериализации", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T DeserializeObject(byte[] buffer) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);) {
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException  e) {
            // System.out.println("Ошибка десериализации");
            // e.printStackTrace();
            log.error("Ошибка десериализации", e);
        }
        return null;
    }
}
