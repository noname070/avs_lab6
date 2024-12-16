package p3111.redgry.lab7;

import p3111.redgry.lab7.collection.Person;
import p3111.redgry.lab7.helpers.StackPersonStorage;
import p3111.redgry.lab7.helpers.StackPersonStorageService;
import p3111.redgry.lab7.helpers.Storage;
import p3111.redgry.lab7.helpers.StorageService;
import p3111.redgry.lab7.commands.CommandsManager;
import p3111.redgry.lab7.utils.DataBaseManagerv2;
import p3111.redgry.lab7.utils.MessagesHandler;
import p3111.redgry.lab7.utils.UserInterface;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor
public class Server implements Runnable {
    private DatagramChannel datagramChannel;
    private SocketAddress socketAddress;

    UserInterface userInterface = new UserInterface(
            new InputStreamReader(System.in, StandardCharsets.UTF_8),
            new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true);
    Storage<Long, Person> storage = new StackPersonStorage();
    StorageService storageService = new StackPersonStorageService(storage);


    public static void main(String[] args) {
        Server server = new Server();
        new Thread(server).start();
    }

    private void receive() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2000);
        byteBuffer.clear();
        socketAddress = datagramChannel.receive(byteBuffer);
        byteBuffer.flip();
        SocketAddress socket = socketAddress;
        DatagramChannel d = datagramChannel;
        if (socketAddress != null && !new String(byteBuffer.array()).trim().isEmpty()) {
            MessagesHandler messagesHandler = new MessagesHandler(
                    d,
                    socket,
                    storageService,
                    byteBuffer);
            messagesHandler.start();
        }
    }

    @Override
    public void run() {
        DataBaseManagerv2.updateCollectionFromDataBase(storageService);
        socketAddress = new InetSocketAddress(Config.APP_HOST, Config.APP_PORT);

        try {
            datagramChannel = DatagramChannel.open();
            datagramChannel.bind(socketAddress);
            datagramChannel.configureBlocking(false);
            while (true) {
                receive();
            }

        } catch (IOException e) {
            log.error("Какая то ошибка:", e);
        } finally {

            try {
                CommandsManager.getInstance()
                        .executeCommand(userInterface,
                                storageService,
                                "save",
                                datagramChannel,
                                socketAddress,
                                new DataBaseManagerv2(Config.DB_ROOT_USER, Config.DB_ROOT_PASSWORD));

            } catch (IOException e) {
                log.error("Ошибка при экстренном сохранении", e);
            }

        }
    }
}
