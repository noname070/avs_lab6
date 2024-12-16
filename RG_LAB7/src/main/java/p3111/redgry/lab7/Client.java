package p3111.redgry.lab7;

import p3111.redgry.lab7.commands.CommandsManager;
import p3111.redgry.lab7.commands.commands.AbstractCommand;
import p3111.redgry.lab7.commands.commands.Login;
import p3111.redgry.lab7.commands.commands.Register;
import p3111.redgry.lab7.exceptions.NoSuchCommandException;
import p3111.redgry.lab7.utils.Serialization;
import p3111.redgry.lab7.utils.UserInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

import lombok.extern.log4j.Log4j2;

@Log4j2
class Client implements Runnable {
    private DatagramChannel datagramChannel;
    private SocketAddress socketAddress;

    private String login = "";
    private String password = "";
    private boolean registered = false;

    private final UserInterface userInterface;

    public Client() throws IOException {
        datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        userInterface = new UserInterface(
                new InputStreamReader(System.in, StandardCharsets.UTF_8),
                new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true);
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.connect(Config.APP_HOST, Config.APP_PORT);
            while (true) {
                client.run();
            }
        } catch (IOException e) {
            log.error("Произошла ошибка ввода/вывода", e);
        }
    }

    private void connect(String hostname, int port) throws IOException {
        socketAddress = new InetSocketAddress(hostname, port);
        datagramChannel.connect(socketAddress);
        log.info("Устанавливаем соединение с %s по порту %s", hostname, port);
        System.out.printf("Устанавливаем соединение с %s по порту %s\n", hostname, port);
    }

    private String receiveAnswer() throws IOException {
        byte[] bytes = new byte[1000000];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        socketAddress = datagramChannel.receive(buffer);
        return new String(buffer.array()).trim();
    }

    private void authorization(BufferedReader reader) {
        try {
            String input;
            String answer = "";
            do {
                log.info("Введите 'login' для входа или 'register' для регистрации");
                System.out.print("Введите 'login' для входа или 'register' для регистрации \n>");
                input = reader.readLine().trim().split("\\s+")[0];
            } while (!input.equalsIgnoreCase("register") && !input.equalsIgnoreCase("login"));

            if (input.equalsIgnoreCase("register")) {
                register(reader);
            } else {
                login(reader);
            }

            while (answer.isEmpty()) {
                answer = receiveAnswer();
                if (answer.equals("")) {
                    continue;
                }
                if (answer.equals("Пользователь успешно вошёл в систему.")) {
                    registered = true;
                    System.out.println("Введите `help` для вывода списка команд");
                }
                log.info(answer);
                System.out.println(answer);
            }
        } catch (Exception e) {
            log.error(e);
            System.err.println("Непредвиденная ошибка: " + e.getLocalizedMessage());
            e.printStackTrace(System.err);
        }
    }

    private void register(BufferedReader reader) throws IOException {
        log.info("Регистрация нового пользователя");
        System.out.println("Регистрация нового пользователя");

        login = readInputWithValidation(reader,
                "Придумайте логин (не менее 4 символов, только английские буквы и цифры): ",
                s -> s.length() >= 4 && s.matches("[a-zA-Z0-9]+"));

        password = readInputWithValidation(reader,
                "Придумайте пароль (не менее 4 символов, только английские буквы и цифры): ",
                s -> s.length() >= 4 && s.matches("[a-zA-Z0-9]+"));

        log.info("Ваш логин: %s, Ваш пароль: %s", login, password);
        System.out.printf("Ваш логин: %s, Ваш пароль: %s\n", login, password);
        sendCommand(new Register());
    }

    private void login(BufferedReader reader) throws IOException {
        log.info("Авторизация пользователя");
        System.out.println("Авторизация пользователя");

        System.out.print("Введите логин > ");
        login = reader.readLine();
        System.out.print("Введите пароль > ");
        password = reader.readLine();

        sendCommand(new Login());
    }

    private String readInputWithValidation(
            BufferedReader reader,
            String message,
            InputValidator validator) throws IOException {

        String input;
        do {
            System.out.print(message);
            input = reader.readLine().trim();
            if (!validator.isValid(input)) {
                log.warn("Неправильный ввод. Повторите попытку.");
                System.err.println("Неправильный ввод. Повторите попытку.");
            }
        } while (!validator.isValid(input));
        return input;
    }

    private void sendCommand(AbstractCommand command) throws IOException {
        if (command == null) {
            return;
        }
        if (command != null) {
            if (command.getClass().getName().contains("Exit")) {
                log.info("glhf!");
                System.out.println("glfh!");
                System.exit(0);
            }
        }
        ByteBuffer buffer = ByteBuffer.wrap(
                Serialization.SerializeObject(command, login, password));
        datagramChannel.send(buffer, socketAddress);
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            if (!registered) {
                log.info("Зарегистрируйтесь или войдите в систему для работы с коллекцией.");
                System.out.println("Зарегистрируйтесь или войдите в систему для работы с коллекцией.");
            }
            while (!registered) {
                authorization(reader);
            }
            while (true) {
                try {
                    System.out.print("> ");
                    String[] inputs = reader.readLine().trim().split("\\s+");
                    AbstractCommand command = CommandsManager.getInstance().commandDeterminator(inputs);

                    if (command != null && command.isNeedObjectToExecute()) {
                        command.setArgs(new String[] { userInterface.readWithMessage("Введите ключ: ", false) });
                        command.setPerson(CommandsManager.getInstance()
                                .getPerson(userInterface,
                                        Long.parseLong(command.getArgs()[0])));
                    }

                    if (command == null)
                        continue;

                    sendCommand(command);
                    String answer = "";
                    while (answer.isEmpty()) {
                        answer = receiveAnswer();
                    }

                    log.info(answer);
                    System.out.println(answer);

                } catch (NoSuchCommandException e) {
                    log.error("Команда не найдена.");
                    System.err.println("Команда не найдена.");
                } catch (NumberFormatException e) {
                    log.error("Неверный формат аргумента.");
                    System.err.println("Неверный формат аргумента.");

                }
            }

        } catch (PortUnreachableException e) {
            log.error("Сервер недоступен.", e);
            System.err.printf("Сервер недоступен:\n", e);
        } catch (IOException e) {
            log.error("Ошибка ввода/вывода", e);
            System.err.printf("Ошибка ввода/вывода\n", e);

        } catch (Exception e) {
            log.error("Произошла непредусмотренная ошибка.", e);
            System.err.printf("Произошла непредусмотренная ошибка\n", e);

        } finally {
            log.error("Какая-то ошибка, выход.");
            System.exit(1);
        }
    }

    @FunctionalInterface
    private interface InputValidator {
        boolean isValid(String input);
    }
}