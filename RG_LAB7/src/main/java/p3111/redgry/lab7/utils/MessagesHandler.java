package p3111.redgry.lab7.utils;

import p3111.redgry.lab7.commands.CommandsManager;
import p3111.redgry.lab7.commands.commands.AbstractCommand;
import p3111.redgry.lab7.exceptions.InvalidCountOfArgumentsException;
import p3111.redgry.lab7.helpers.StorageService;

import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class MessagesHandler extends Thread {
    UserInterface userInterface = new UserInterface(new InputStreamReader(System.in, StandardCharsets.UTF_8), new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true);
    
    private DatagramChannel datagramChannel;
    private SocketAddress socketAddress;
    private DataBaseManagerv2 dataBaseManager;
    private StorageService storageService;
    private ByteBuffer byteBuffer;
    private static AbstractCommand command = null;

    public MessagesHandler(DatagramChannel datagramChannel, SocketAddress socketAddress, StorageService storageService, ByteBuffer byteBuffer){
        this.datagramChannel = datagramChannel;
        this.socketAddress = socketAddress;
        this.storageService = storageService;
        this.byteBuffer = byteBuffer;
    }

    @Override
    public void run() {
        String login;
        String password;
        Object o;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            login = objectInputStream.readUTF();
            password = objectInputStream.readUTF();
            o = objectInputStream.readObject();
            if (o == null){
                datagramChannel.send(ByteBuffer.wrap("Команда не найдена или имеент неверное количество аргументов. Для просмотра доступных команд введите help".getBytes()), socketAddress);
                datagramChannel.send(ByteBuffer.wrap("I'm fucking seriously, it's fucking EOF!!!".getBytes()), socketAddress);
            } else {
                if (o.getClass().getName().contains(".Login")) authorization("login", login, password);
                else if (o.getClass().getName().contains(".Register")) authorization("register", login, password);
                else if (!o.getClass().getName().contains(".Person")){
                    if (o.getClass().getName().contains(".commands.")) {
                        command = (AbstractCommand) o;
                        this.dataBaseManager = new DataBaseManagerv2(login, password);
                        // dataBaseManager.setUSER(login);
                        // dataBaseManager.setPASSWORD(password);
                        CommandsManager.getInstance().executeCommand(userInterface, storageService, command, datagramChannel, socketAddress, this.dataBaseManager);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | InvalidCountOfArgumentsException e) {
            log.error("Err in message handler thread", e);
            System.err.println("Err in msg handler thread");
            e.printStackTrace(System.err);
        }
    }

    private void authorization(String message, String login, String password) throws IOException {
        String response;
    
        switch (message) {
            case "login" -> {
                // response = dataBaseManager.login(login, password)
                //         ? "Пользователь успешно вошёл в систему."
                //         : "Не удалось войти в систему.";
                dataBaseManager = new DataBaseManagerv2(login, password);
                response = dataBaseManager.login()
                            ? "Пользователь успешно вошёл в систему."
                            : "Не удалось войти в систему.";
            }
            case "register" -> {
                response = DataBaseManagerv2.addUser(login, password)
                        ? "Пользователь добавлен. Войдите в систему."
                        : "Не удалось добавить пользователя. Логин занят, содержит недопустимые символы или их последовательность.";
            }
            default -> {
                response = "Неизвестное действие: " + message;
            }
        }
    
        datagramChannel.send(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)), socketAddress);
    }
}
