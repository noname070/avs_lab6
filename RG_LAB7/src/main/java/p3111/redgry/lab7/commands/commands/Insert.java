package p3111.redgry.lab7.commands.commands;

import p3111.redgry.lab7.helpers.StorageService;
import p3111.redgry.lab7.commands.CommandsManager;
import p3111.redgry.lab7.utils.DataBaseManagerv2;
import p3111.redgry.lab7.utils.UserInterface;

import java.io.IOException;
import java.util.ArrayList;

public class Insert extends AbstractCommand {

    public Insert() {
        command = "insert";
        helpText = "Добавить новый элемент с заданным ключом.";
        needObjectToExecute = true;
        argumentsCount = 1;
    }

    @Override
    public ArrayList<String> execute(
            UserInterface userInterface,
            StorageService ss,
            String[] args,
            DataBaseManagerv2 dataBaseManager) throws IOException {

        if (args.length == argumentsCount) {
            try {
                CommandsManager.getInstance().getLock().lock();
                long key;
                
                try {
                    key = Long.parseLong(args[0]);
                } catch (ClassCastException e) {
                    CommandsManager.getInstance().printToClient("Команде был указан аргумент неправильного формата!");
                    return null;
                }

                if (ss.checkKey(key)) {
                    if (dataBaseManager.addElementToDb(person)) {
                        DataBaseManagerv2.updateCollectionFromDataBase(ss);
                    }
                    // if (dataBaseManager.addToDataBase(getPerson())) {
                    //     dataBaseManager.updateCollectionFromDataBase(ss);
                    // }
                    // ss.add(getPerson(), key);
                    CommandsManager.getInstance().printToClient("Персонаж успешно добавлен!");
                } else {
                    CommandsManager.getInstance().printToClient("Персонаж уже существует с данным ключом.");
                }
                return null;

            } catch (Exception e) {
                CommandsManager.getInstance().printToClient("Произошла непредвиденная ошибка: " + e.getLocalizedMessage() + "\n" + e.getMessage());
            } finally {
                CommandsManager.getInstance().getLock().unlock();
            }
        }
        // logger.warn("Команда не были переданы аргументы");
        CommandsManager.getInstance()
                .printToClient("Команде не был указан агрумент или были указаны лишние аргументы!");
        return null;
    }
}
