package seng302.group2.util.undoredo;

import seng302.group2.workspace.SaharaItem;

import java.util.Set;

/**
 * The interface for undoable items that follows as an implementation of the command pattern.
 * Created by Jordane on 7/05/2015.
 */
public interface Command {
    /**
     * Executes the command
     */
    public void execute();

    /**
     * Undoes the command
     */
    public void undo();

    /**
     * Gets the string value of the Command.
     * return The commands string
     */
    public String getString();

    /**
     * Searches the stateObjects to find an equal model class to map to
     *
     * @param stateObjects A set of objects to search through
     * @return If the item was successfully mapped
     */
    public boolean map(Set<SaharaItem> stateObjects);
}