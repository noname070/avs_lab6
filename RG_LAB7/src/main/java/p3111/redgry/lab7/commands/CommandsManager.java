package p3111.redgry.lab7.commands;

import p3111.redgry.lab7.collection.Person;
import p3111.redgry.lab7.commands.commands.*;
import p3111.redgry.lab7.exceptions.InvalidCountOfArgumentsException;
import p3111.redgry.lab7.exceptions.NoSuchCommandException;
import p3111.redgry.lab7.helpers.StorageService;
import p3111.redgry.lab7.utils.CollectionUtils;
import p3111.redgry.lab7.utils.DataBaseManagerv2;
import p3111.redgry.lab7.utils.UserInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
/**
 * Синглтон, который управляет командами. Хранит в себе реестр всех команд.
 * Через него происходит выполнение команды по строке пользователя.
 */

@Log4j2
public class CommandsManager {
    public static CommandsManager instance;

    private static final Map<String, AbstractCommand> commands = new HashMap<>();
    @Getter @Setter static List<String> commandsList = new ArrayList<>();

    @Getter @Setter private DatagramChannel serverDatagramChannel;
    @Getter @Setter private SocketAddress socketAddress;
    @Getter @Setter private String scriptFileName;
    @Getter @Setter private boolean isScript = false;
    @Getter @Setter private BufferedReader scriptBufferedReader;

    @Getter ReentrantLock lock = new ReentrantLock();

    public static CommandsManager getInstance() {
        if (instance == null) {
            instance = new CommandsManager();
        }
        return instance;
    }

    /**
     * Стандартный конструктор, в котором добавляются все команды.
     */
    private CommandsManager() {
        addCommand(new Clear());
        addCommand(new CountLessThanLocation());
        // addCommand(new ExecuteScript()); // ну да выпилил, иче?
        addCommand(new Exit());
        addCommand(new Help());
        addCommand(new History());
        addCommand(new Info());
        addCommand(new Insert());
        addCommand(new PrintAscending());
        addCommand(new RemoveAnyByBirthday());
        addCommand(new RemoveByKey());
        addCommand(new RemoveGreater());
        addCommand(new RemoveGreaterKey());
        addCommand(new Show());
        addCommand(new Update());

    }

    private void addCommand(AbstractCommand cmd) {
        commands.put(cmd.getCommand(), cmd);
    }

    public AbstractCommand getCommand(String s) throws NoSuchCommandException {
        if (!commands.containsKey(s)) {
            throw new NoSuchCommandException();
        }
        return commands.get(s);
    }

    public void executeCommand(UserInterface userInterface,
                            StorageService storageService,
                            AbstractCommand command,
                            DatagramChannel datagramChannel,
                            SocketAddress socketAddress,
                            DataBaseManagerv2 dataBaseManager) throws IOException, InvalidCountOfArgumentsException  {
                           
        CommandsManager.getInstance().setServerDatagramChannel(datagramChannel);
        CommandsManager.getInstance().setSocketAddress(socketAddress);

        log.info("Выполнение команды пользователя.");
        command.execute(userInterface, storageService, command.getArgs(), dataBaseManager);
        
        // ?
        // int i = 0;
        // while (i < 1000000) {
        //     i++;
        // } 

        if (!CommandsManager.getInstance().isScript) {
            log.info("Отправка пользователю сообщение о завершении чтения.");
            System.out.println("Отправка пользователю сообщение о завершении чтения.");
            // datagramChannel.send(ByteBuffer.wrap("I'm fucking seriously, it's fucking EOF!!!".getBytes()),
            //         socketAddress);
        }
    }

    public void executeCommand(UserInterface userInterface,
            StorageService storageService,
            String commandName,
            DatagramChannel datagramChannel,
            SocketAddress socketAddress,
            DataBaseManagerv2 dataBaseManager) throws NoSuchCommandException, IOException {

        CommandsManager.getInstance().setServerDatagramChannel(datagramChannel);
        CommandsManager.getInstance().setSocketAddress(socketAddress);

        log.info("Выполнение команды пользователя.");
        getCommand(commandName).execute(userInterface, storageService, new String[0], dataBaseManager);
        
        // ?
        // int i = 0;
        // while (i < 1000000) {
        //     i++;
        // }
        if (!CommandsManager.getInstance().isScript) {
            log.info("Отправка пользователю сообщение о завершении чтения.");
            datagramChannel.send(ByteBuffer.wrap("I'm fucking seriously, it's fucking EOF!!!".getBytes()),
                    socketAddress);
        }
    }

    public List<AbstractCommand> getAllCommands() {
        return commands.keySet().stream().map(x -> (commands.get(x))).collect(Collectors.toList());
    }

    // треш..
    public static void cmdList() {
        StringBuilder sb = new StringBuilder();
        if (getCommandsList().size() - 1 < 15) {
            int count = 1;
            for (int i = getCommandsList().size() - 1; i != -1; i--) {
                sb.append(count + ": " + getCommandsList().get(getCommandsList().size() - 1 - i).split(" ")[0] + "\n");
                count++;
            }
        } else {
            int count = 15;
            for (int i = 0; i < 15; i++) {
                sb.append(count + ": " + getCommandsList().get(getCommandsList().size() - 1 - i).split(" ")[0] + "\n");
                --count;
            }
        }
        CommandsManager.getInstance().printToClient(sb.toString());
    }

    public void printToClient(String line) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap((line.getBytes()));
            CommandsManager.getInstance().getServerDatagramChannel().send(buffer,
                    CommandsManager.getInstance().getSocketAddress());
            log.info("Отправляем ответ клиенту: %s ", new String(buffer.array()));
        } catch (IOException e) {
            log.info("Не удалось отправить ответ клиенту %s ", e.getMessage());
        }
    }

    public AbstractCommand commandDeterminator(String[] args) {
        try {
            String cmd = args[0].trim();
            args = Arrays.copyOfRange(args, 1, args.length);
            if (cmd.trim().equals("login") || cmd.trim().equals("register")) {
                // System.out.println(
                //         "Вы уже вошли, для повторного входа или регистрации закончите сеанс с помощью команды exit");
                log.warn("Вы уже вошли, для повторного входа или регистрации закончите сеанс с помощью команды exit");
                return null;
            }
            AbstractCommand command = commands.getOrDefault(cmd.trim(), null);
            if (command == null) {
                return null;
            }
            
            command.setArgs(args);
            return command;
            /**
             * for (AbstractCommand command : commands.values()) {
             * if (command.getCommand().equals(cmd.trim())) {
             * command.setArgs(args);
             * return command;
             * }
             * }
             */
        } catch (Exception e) {
            // System.out.println("");
            log.error("Ошибка в обработке команды: ", e);
        }
        return null;
    }

    public Person getPerson(UserInterface userInterface, Long key) throws IOException {
        return CollectionUtils.argumentsReader(userInterface, key);
    }



}
