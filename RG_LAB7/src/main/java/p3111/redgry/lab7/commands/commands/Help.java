package p3111.redgry.lab7.commands.commands;

import p3111.redgry.lab7.helpers.StorageService;
import p3111.redgry.lab7.commands.CommandsManager;
import p3111.redgry.lab7.utils.DataBaseManagerv2;
import p3111.redgry.lab7.utils.UserInterface;

import java.io.IOException;
import java.util.ArrayList;

public class Help extends AbstractCommand {

    public Help() {
        command = "help";
        helpText = "Выводит справку по доступным командам.";
    }

    @Override
    public ArrayList<String> execute(
            UserInterface userInterface,
            StorageService ss,
            String[] args,
            DataBaseManagerv2 dataBaseManager) throws IOException {

        StringBuilder sb = new StringBuilder();
        if (argumentsCount == args.length) {
            sb.append("Команды:").append("\n");
            for (AbstractCommand command : CommandsManager.getInstance().getAllCommands()) {
                sb.append(ANSI_YELLOW).append(command.getCommand()).append(ANSI_RESET).append(": ")
                        .append(command.getHelpText()).append("\n");
            }
            CommandsManager.getInstance().printToClient(sb.toString());
            return null;
        }
        log.warn("Команда не принимает аргументы");
        CommandsManager.getInstance().printToClient("Команда не принимает агрументы!");
        return null;
    }
}
