package seng302.group2.workspace.tag;

import javafx.scene.paint.Color;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.conversion.ColorUtils;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A class representation of tags, used to highlight and identify different states of other Sahara model objects.
 */
public class Tag extends SaharaItem implements Serializable, Comparable<Tag> {
    private String name;
    private String colorCode = ColorUtils.toRGBCode(Color.ROYALBLUE);


    /**
     * Empty constructor.
     */
    public Tag() {}

    /**
     * Basic constructor for a Tag
     *
     * @param tagName The name of the tag
     */
    public Tag(String tagName) {
        this.name = tagName;
        this.getTags().clear();
        this.colorCode = ColorUtils.toRGBCode(Color.ROYALBLUE);
    }

    /**
     * Gets the colour of the tag for use on the visual tag nodes
     *
     * @return The colour of the tag
     */
    public Color getColor() {
        if (this.colorCode == null) {
            this.colorCode = ColorUtils.toRGBCode(Color.ROYALBLUE);
        }
        return ColorUtils.toColor(this.colorCode);
    }

    /**
     * This function takes in a tag short name, and searches the global list of tags to see if it already
     * exists (matching short name). If so, returns the already existing tag, else will return a new tag
     * with the specified short name.
     * @param tagShortName The short name of a potential new tag
     * @return A tag from the global list or a new tag.
     */
    public static Tag getNewTag(String tagShortName) {
        Tag returnTag = null;
        for (Tag tag : Global.currentWorkspace.getAllTags()) {
            if (tag.getName().equals(tagShortName)) {
                returnTag = tag;
                break;
            }
        }

        if (returnTag == null) {
            returnTag = new Tag(tagShortName);
        }

        return returnTag;
    }

    /**
     * Sets the colour of the tag for use on the visual tag nodes
     *
     * @param color The colour to set the tag
     */
    public void setColor(Color color) {
        this.colorCode = ColorUtils.toRGBCode(color);
    }

    /**
     * Sets the name of the tag
     * @param name The name of the tag
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Deletes the tag from the workspace and removes it from every item that has the tag.
     */
    public void deleteGlobalTag() {
        Command deleteGlobalTag = new DeleteGlobalTagCommand(this);
        Global.commandManager.executeCommand(deleteGlobalTag);
    }

    /**
     * SaharaItems 'belonging' to the Tag
     *
     * @return Tag empty set as tags do not have any items belonging to
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        return new HashSet<>();
    }


    /**
     * Returns all of the Sahara Items that have been tagged with this tag
     *
     * @return A set of the Sahara Items that have been tagged with this tag
     */
    // Was ObservableList based on this.items
    public Set<SaharaItem> getTaggedItems() {
        Set<SaharaItem> items = new HashSet<>();
        for (SaharaItem item : SaharaItem.getAllItems()) {
            if (item.getTags().contains(this)) {
                items.add(item);
            }
        }
        return items;
    }

    /**
     * Gets the short name of the tag.
     *
     * @return tags short name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Method for creating an XML element for the Tag within report generation
     *
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        return ReportGenerator.doc.createElement(name);
    }


    /**
     * Overridden method for displaying the tag as a string
     *
     * @return The name of the tag
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Creates a tag edit command and executes it with the Global Command Manager, updating
     * the tag with the new parameter values.
     *
     * @param newName  The new tag name
     * @param newColor The new color
     */
    public void edit(String newName, Color newColor) {
        Command edit = new TagEditCommand(this, newName, newColor);
        Global.commandManager.executeCommand(edit);
    }

    /**
     * Compares the tag to another tag based on their names
     *
     * @param tag The tag to compare to
     * @return The string comparison result of the tag names
     */
    @Override
    public int compareTo(Tag tag) {
        String tag1Name = this.getName();
        String tag2Name = tag.getName();
        return tag1Name.compareTo(tag2Name);
    }

    /**
     * A command class that allows the executing and undoing of tag edits
     */
    private class TagEditCommand implements Command {
        private Tag tag;

        private String name;
        private String color;

        private String oldName;
        private String oldColor;

        private TagEditCommand(Tag tag, String newName, Color newColor) {
            this.tag = tag;

            this.name = newName;
            this.color = ColorUtils.toRGBCode(newColor);

            this.oldName = tag.name;
            this.oldColor = tag.colorCode;
        }

        /**
         * Executes/Redoes the changes of the tag edit
         */
        public void execute() {
            tag.name = name;
            tag.colorCode = color;
        }

        /**
         * Undoes the changes of the tag edit
         */
        public void undo() {
            tag.name = oldName;
            tag.colorCode = oldColor;
        }

        /**
         * Gets the String value of the Command for Tags.
         */
        public String getString() {
            return "the edit of Tag\"" + tag.getName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         *
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(tag)) {
                    this.tag = (Tag) item;
                    mapped = true;
                }
            }


            return mapped;
        }
    }

    /**
     * A command class for allowing the deletion of Tags from a Workspace.
     */
    private class DeleteGlobalTagCommand implements Command {
        private Tag tag;
        private boolean globalTag = false;
        private Set<SaharaItem> taggedItems = new HashSet<>();

        /**
         * Constructor for the global tag deletion command
         *
         * @param tag The tag to be removed from every item, and then the workspace.
         */
        DeleteGlobalTagCommand(Tag tag) {
            this.tag = tag;
        }

        /**
         * Executes the tag deletion command
         */
        public void execute() {
            taggedItems.addAll(tag.getTaggedItems());
            if (Global.currentWorkspace.getAllTags().contains(tag)) {
                Global.currentWorkspace.getAllTags().remove(tag);
            }
            for (SaharaItem item : taggedItems) {
                item.getTags().remove(tag);
            }
        }

        /**
         * Gets the String value of the Command for Tags delete command.
         */
        public String getString() {
            return "the deletion of Tag \"" + tag.getName() + "\"";
        }

        /**
         * Undoes the global tag deletion command
         */
        public void undo() {
            for (SaharaItem item : taggedItems) {
                item.getTags().add(tag);
            }
            Global.currentWorkspace.getAllTags().add(tag);
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         *
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(tag)) {
                    this.tag = (Tag) item;
                    mapped = true;
                }
            }
            return mapped;
        }
    }
}