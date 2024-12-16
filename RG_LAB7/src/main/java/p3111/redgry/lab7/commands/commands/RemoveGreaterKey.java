package p3111.redgry.lab7.commands.commands;

import p3111.redgry.lab7.helpers.StorageService;
import p3111.redgry.lab7.commands.CommandsManager;
import p3111.redgry.lab7.utils.DataBaseManagerv2;
import p3111.redgry.lab7.utils.UserInterface;

import java.io.IOException;
import java.util.ArrayList;

public class RemoveGreaterKey extends AbstractCommand {

    public RemoveGreaterKey() {
        command = "remove_greater_key";
        helpText = "Удалить из коллекции все элементы, ключ которых превышает заданный.";
        argumentsCount = 1;
    }

    @Override
    public ArrayList<String> execute(
            UserInterface userInterface,
            StorageService ss,
            String[] args,
            DataBaseManagerv2 dataBaseManager) throws IOException {

        if (args.length == argumentsCount) {
            long key;
            try {
                key = Long.parseLong(args[0]);
            } catch (ClassCastException e) {
                CommandsManager.getInstance().printToClient("Команде был указан аргумент неправильного формата!");
                return null;
            }
            if (ss.removeGreaterKey(key) != null) {
                ss.removeGreaterKey(key).forEach(keys -> {
                    if (dataBaseManager.removeFromDataBase(ss.list().get(Long.parseLong(keys)))) {
                        CommandsManager.getInstance().printToClient("Персонаж с ключем: " + keys + " успешно удален!");
                    }
                    ;
                });
                DataBaseManagerv2.updateCollectionFromDataBase(ss);
                CommandsManager.getInstance().printToClient("Персонажи пренадлежащие вам были удалены из коллекции.");
            } else
                CommandsManager.getInstance().printToClient("Коллекция пуста( Заполни её, а потом попробуй снова.");
            return null;
        }
        // logger.warn("Команда не были переданы аргументы");
        CommandsManager.getInstance().printToClient("Команда ожидала аргументы.");
        return null;
    }
}
