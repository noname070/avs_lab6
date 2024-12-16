package p3111.redgry.lab7.commands.commands;

import p3111.redgry.lab7.helpers.StorageService;
import p3111.redgry.lab7.utils.DataBaseManagerv2;
import p3111.redgry.lab7.utils.UserInterface;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Save extends AbstractCommand {
    private static final String PATH = Paths.get("out.csv").toAbsolutePath().toString();

    public Save() {
        command = "save";
        helpText = "Сохранить коллекцию в файл.";
    }

    @Override
    public ArrayList<String> execute(
            UserInterface userInterface,
            StorageService ss,
            String[] args,
            DataBaseManagerv2 dataBaseManager) throws IOException {

        if (args.length == argumentsCount) {
            ss.save(PATH);
            // System.out.println("Коллекция сохранена успешно.");
            log.info("Коллекция сохранена успешно.");
            return null;
        }
        log.warn("Команда не принимает аргументы");
        return null;
    }
}
