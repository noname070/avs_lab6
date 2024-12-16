package p3111.redgry.lab7.commands.commands;

import p3111.redgry.lab7.helpers.StorageService;
import p3111.redgry.lab7.commands.CommandsManager;
import p3111.redgry.lab7.utils.DataBaseManagerv2;
import p3111.redgry.lab7.utils.UserInterface;

import java.io.IOException;
import java.util.ArrayList;

public class CountLessThanLocation extends AbstractCommand {

    public CountLessThanLocation() {
        command = "count_less_than_location";
        helpText = "Вывести количество элементов, значение поля location которых меньше заданного.";
        argumentsCount = 1;
    }

    @Override
    public ArrayList<String> execute(
            UserInterface userInterface,
            StorageService ss, String[] args,
            DataBaseManagerv2 dataBaseManager) throws IOException {
        if (argumentsCount == args.length) {
            try {
                CommandsManager.getInstance().getLock().lock();
                double V;
                try {
                    V = Double.parseDouble(args[0]);
                } catch (NumberFormatException e) {
                    CommandsManager.getInstance().printToClient("Неправильный формат аргумента");
                    return null;
                }
                CommandsManager.getInstance().printToClient(ss.CountLessThanLocation(V));
                return null;
            } finally {
                CommandsManager.getInstance().getLock().unlock();
            }
        }
        // logger.warn("Команда не были переданы аргументы");
        CommandsManager.getInstance().printToClient("Команде не был указан агрумент или был неправильного формата!");
        return null;
    }
}
