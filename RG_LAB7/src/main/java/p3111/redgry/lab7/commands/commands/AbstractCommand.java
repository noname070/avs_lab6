package p3111.redgry.lab7.commands.commands;

import p3111.redgry.lab7.collection.Person;
import p3111.redgry.lab7.helpers.StackPersonStorageService;
import p3111.redgry.lab7.helpers.StorageService;
import p3111.redgry.lab7.utils.DataBaseManagerv2;
import p3111.redgry.lab7.utils.UserInterface;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Абстрактный класс для команд.
 */
public abstract class AbstractCommand implements Serializable {
    protected static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(AbstractCommand.class);

    protected static final String ANSI_RESET = "\u001B[0m";
    protected static final String ANSI_BLACK = "\u001B[30m";
    protected static final String ANSI_RED = "\u001B[31m";
    protected static final String ANSI_GREEN = "\u001B[32m";
    protected static final String ANSI_YELLOW = "\u001B[33m";
    protected static final String ANSI_BLUE = "\u001B[34m";
    protected static final String ANSI_PURPLE = "\u001B[35m";
    protected static final String ANSI_CYAN = "\u001B[36m";
    protected static final String ANSI_WHITE = "\u001B[37m";

    @Getter protected String command;
    @Getter protected String helpText;
    protected int argumentsCount = 0;
    @Getter protected boolean needObjectToExecute = false;
    @Getter @Setter protected String[] args;
    @Getter @Setter protected Person person;

    public abstract ArrayList<String> execute(
            UserInterface userInterface,
            StorageService ss,
            String[] args,
            DataBaseManagerv2 dataBaseManager) throws IOException;

    public void setPerson(Person person, StackPersonStorageService storage) {
        storage.setPersons(person);
    }

}
