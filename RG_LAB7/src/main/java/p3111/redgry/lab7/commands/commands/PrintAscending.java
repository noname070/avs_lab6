package p3111.redgry.lab7.commands.commands;

import p3111.redgry.lab7.helpers.StorageService;
import p3111.redgry.lab7.commands.CommandsManager;
import p3111.redgry.lab7.utils.DataBaseManagerv2;
import p3111.redgry.lab7.utils.UserInterface;

import java.io.IOException;
import java.util.ArrayList;

public class PrintAscending extends AbstractCommand {

    public PrintAscending() {
        command = "print_ascending";
        helpText = "Вывести элементы коллекции в порядке возрастания.";
    }

    @Override
    public ArrayList<String> execute(
            UserInterface userInterface,
            StorageService ss,
            String[] args,
            DataBaseManagerv2 dataBaseManager) throws IOException {

        if (argumentsCount == args.length) {
            CommandsManager.getInstance().printToClient(ss.display());
            return null;
        }
        // logger.warn("Команда не принимает аргументы");
        CommandsManager.getInstance().printToClient("Команда не принимает агрументы!");
        return null;
    }
}
