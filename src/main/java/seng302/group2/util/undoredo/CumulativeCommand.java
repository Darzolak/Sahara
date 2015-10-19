package seng302.group2.util.undoredo;

import seng302.group2.workspace.SaharaItem;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

/**
 * A command that nests a collection of other commands such that they can be
 * Created by jml168 on 2/07/15.
 */
public class CumulativeCommand implements Command {

    private Collection<Command> commands = new LinkedList<>();
    private String statusBarString = null;

    /**
     * The default constructor for CumulativeCommands, has no initial sub-commands
     */
    public CumulativeCommand() {
        super();
    }

    /**
     * A constructor that takes in an initial collection of sub-commands
     *
     * @param commands The initial collection of sub-commands
     */
    public CumulativeCommand(Collection<Command> commands) {
        super();
        this.commands.addAll(commands);
    }

    /**
     * Adds a command to the collection of cumulative commands
     *
     * @param command The command to add
     * @return The result of adding the command to the cumulative commands collection
     */
    public boolean add(Command command) {
        return commands.add(command);
    }

    /**
     * Gets the number of sub-commands inside the cumulative command
     *
     * @return The size of the list of sub-commands
     */
    public int size() {
        return commands.size();
    }

    /**
     * Executes the Command
     */
    @Override
    public void execute() {
        for (Command command : commands) {
            command.execute();
        }
    }

    /**
     * Undoes the Command
     */
    @Override
    public void undo() {
        for (Command command : commands) {
            command.undo();
        }
    }

    /**
     * sets the String value of the Command for cumulative commands.
     * @param string The new status bar string
     */
    public void setString(String string) {
        statusBarString = string;
    }

    /**
     * Gets the String value of the Command for cumulative commands.
     */
    public String getString() {
        return statusBarString;
    }

    /**
     * Searches the stateObjects to find an equal model class to map to
     *
     * @param stateObjects A set of objects to search through
     * @return If the item was successfully mapped
     */
    @Override
    public boolean map(Set<SaharaItem> stateObjects) {
        boolean result = true;
        for (Command command : commands) {
            if (!command.map(stateObjects)) {
                result = false;
            }
        }
        return result;
    }
}
