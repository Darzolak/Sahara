package seng302.group2.workspace.project.story.acceptanceCriteria;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.estimation.EstimationScalesDictionary;
import seng302.group2.workspace.tag.Tag;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A model class for acceptance criteria items inside of stories
 * Created by Shinobu on 30/05/2015.
 */
public class AcceptanceCriteria extends SaharaItem implements Serializable {
    private String description;
    private AcState state;
    private Story story;

    /**
     * An enumeration to represent the states of acceptance criteria
     */
    public enum AcState {
        ACCEPTED,
        UNACCEPTED
    }

    /**
     * Basic constructor
     *
     * @param description the description of the AC
     * @param story       the AC belongs to
     */
    public AcceptanceCriteria(String description, Story story) {
        super();
        this.description = description;
        this.state = AcState.UNACCEPTED;
        this.story = story;
    }

    /**
     * Sets the text of the AC
     *
     * @param text text to set
     */
    public void setText(String text) {
        this.description = text;
    }

    /**
     * Sets the state of the AC to done, accepted or unaccepted
     *
     * @param state the state to set
     */
    public void setAcState(AcState state) {
        this.state = state;
    }

    /**
     * Get the text
     *
     * @return the description of the acceptance criteria
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the state
     *
     * @return the state of the acceptance criteria
     */
    public AcState getState() {
        return this.state;
    }

    /**
     * Gets the story the AC belongs to
     *
     * @return the story of the acceptance criteria
     */
    public Story getStory() {
        return this.story;
    }

    /**
     * Sets the story the AC belongs to
     *
     * @param story to set
     */
    public void setStory(Story story) {
        this.story = story;
    }

    /**
     * Delete the acceptance maintenance and removes it from story
     */
    public void delete() {
        Command deleteAc = new DeleteAcCommand(this);
        Global.commandManager.executeCommand(deleteAc);
    }

    /**
     * Edits the description to the one given
     *
     * @param desc the description to be set to
     */
    public void edit(String desc) {
        Command editAc = new EditAcCommand(this, desc);
        Global.commandManager.executeCommand(editAc);
    }

    /**
     * Edits the state to the one given
     *
     * @param state the state to be set to
     */
    public void edit(AcState state) {
        Command editAc = new EditAcStateCommand(this, state);
        Global.commandManager.executeCommand(editAc);
    }

    /**
     * Edits the tags of the Acceptance critera
     * @param newTags the new tags of the acceptance criteria
     */
    public void editAcTags(ObservableList<Tag> newTags) {
        Command editAc = new EditAcTagsCommand(this, newTags);
        Global.commandManager.executeCommand(editAc);
    }

    /**
     * Method for creating an XML element for the Team within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element acceptanceElement = ReportGenerator.doc.createElement("acceptance-criteria");

        //WorkSpace Elements
        Element acceptanceID = ReportGenerator.doc.createElement("ID");
        acceptanceID.appendChild(ReportGenerator.doc.createTextNode(String.valueOf(id)));
        acceptanceElement.appendChild(acceptanceID);

        Element acceptanceCriteria = ReportGenerator.doc.createElement("description");
        acceptanceCriteria.appendChild(ReportGenerator.doc.createTextNode(description));
        acceptanceElement.appendChild(acceptanceCriteria);

        Element state = ReportGenerator.doc.createElement("state");
        StringConverter<AcState> converter = new AcEnumStringConverter();

        state.appendChild(ReportGenerator.doc.createTextNode(converter.toString(this.state)));
        acceptanceElement.appendChild(state);

        return acceptanceElement;
    }

    /**
     * Returns the items held by the Acceptance Criteria, blank as the AC has no child items.
     * @return a blank hash set
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        return new HashSet<>();
    }

    /**
     * A command class for allowing the deletion of Acceptance Criteria from Stories.
     */
    private class DeleteAcCommand implements Command {
        private AcceptanceCriteria acceptanceCriteria;
        private Story story;
        private boolean oldStoryReady = false;
        private String oldEstimate;

        /**
         * Constructor for the Acceptance Criteria deletion command.
         * @param ac The AC to be deleted.
         */
        DeleteAcCommand(AcceptanceCriteria ac) {
            this.acceptanceCriteria = ac;
            this.story = ac.story;
            if (story != null) {
                oldStoryReady = story.getReady();
                oldEstimate = story.getEstimate();
            }
        }

        /**
         * Executes the acceptance criteria deletion command.
         */
        public void execute() {
            story.getAcceptanceCriteria().remove(acceptanceCriteria);
            if (story.getAcceptanceCriteria().isEmpty()) {
                story.setReady(false);
                story.setEstimate(EstimationScalesDictionary.getScaleValue(
                        EstimationScalesDictionary.DefaultValues.NONE));
            }
        }

        /**
         * Undoes the acceptance criteria deletion command.
         */
        public void undo() {
            story.getAcceptanceCriteria().add(acceptanceCriteria);
            story.setReady(oldStoryReady);
            story.setEstimate(oldEstimate);
        }

        /**
         * Gets the String value of the Command for deleting acceptance criteria.
         */
        public String getString() {
            return "the deletion of Acceptance Criteria \"" + acceptanceCriteria.toString() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_ac = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(acceptanceCriteria)) {
                    this.acceptanceCriteria = (AcceptanceCriteria) item;
                    mapped_ac = true;
                }
            }
            boolean mapped_story = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(story)) {
                    this.story = (Story) item;
                    mapped_story = true;
                }
            }
            return mapped_ac && mapped_story;
        }
    }

    /**
     * AC Edit tags command.
     */
    private class EditAcTagsCommand implements Command {
        private AcceptanceCriteria ac;

        private Set<Tag> acTags = new HashSet<>();
        private Set<Tag> globalTags = new HashSet<>();

        private Set<Tag> oldAcTags = new HashSet<>();
        private Set<Tag> oldGlobalTags = new HashSet<>();

        /**
         * Constructor for the ac editing tags command.
         * @param ac The ac to be edited
         * @param newTags The ac's new tags.
         */
        private EditAcTagsCommand(AcceptanceCriteria ac, ObservableList<Tag> newTags) {
            this.ac = ac;

            if (newTags == null) {
                newTags = FXCollections.observableArrayList();
            }

            this.acTags.addAll(newTags);
            this.globalTags.addAll(newTags);
            this.globalTags.addAll(Global.currentWorkspace.getAllTags());

            this.oldAcTags.addAll(ac.getTags());
            this.oldGlobalTags.addAll(Global.currentWorkspace.getAllTags());
        }

        /**
         * Executes/Redoes the changes of the ac tags edit
         */
        public void execute() {
            //Add any created tags to the global collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(globalTags);
            //Add the tags a AC has to their list of tags
            ac.getTags().clear();
            ac.getTags().addAll(acTags);
        }

        /**
         * Undoes the changes of the ac tags edit
         */
        public void undo() {
            //Adds the old global tags to the overall collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(oldGlobalTags);

            //Changes the AC list of tags to what they used to be
            ac.getTags().clear();
            ac.getTags().addAll(oldAcTags);
        }

        /**
         * Gets the String value of the Command for editting acceptance criteria tags.
         */
        public String getString() {
            return "the edit of Tags on Acceptance Criteria \"" + ac.toString() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(ac)) {
                    this.ac = (AcceptanceCriteria) item;
                    mapped = true;
                }
            }

            //Tag collections
            for (Tag tag : acTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        acTags.remove(tag);
                        acTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : oldAcTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        oldAcTags.remove(tag);
                        oldAcTags.add((Tag) item);
                        break;
                    }
                }
            }

            for (Tag tag : globalTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        globalTags.remove(tag);
                        globalTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : oldGlobalTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        oldGlobalTags.remove(tag);
                        oldGlobalTags.add((Tag)item);
                        break;
                    }
                }
            }

            return mapped;
        }
    }


    /**
     * A command class that allows the executing and undoing of story edits
     */
    private class EditAcStateCommand implements Command {
        private AcceptanceCriteria ac;

        private AcState newAcState;
        private AcState oldAcState;

        /**
         * Constructor for the Acceptance Criteria state editing command.
         * @param ac The AC to be edited.
         * @param newState The new state the AC is in.
         */
        private EditAcStateCommand(AcceptanceCriteria ac, AcState newState) {
            this.ac = ac;

            this.oldAcState = ac.state;
            this.newAcState = newState;
        }

        /**
         * Executes the changes of the story edit
         */
        public void execute() {
            ac.state = newAcState;
        }

        /**
         * Undoes the changes of the story edit
         */
        public void undo() {
            ac.state = oldAcState;
        }

        /**
         * Gets the String value of the Command for editting the AC state.
         */
        public String getString() {
            return "the edit of State on Acceptance Criteria \"" + ac.toString() + "\"";
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
                if (item.equivalentTo(ac)) {
                    this.ac = (AcceptanceCriteria) item;
                    mapped = true;
                }
            }
            return mapped;
        }
    }




    /**
     * A command class that allows the executing and undoing of story edits
     */
    private class EditAcCommand implements Command {
        private AcceptanceCriteria ac;

        private String newDescription;
        private String oldDescription;

        /**
         * Constructor for the Acceptance Criteria editing command.
         * @param ac The AC to be edited.
         * @param newDesc The new description for the AC.
         */
        private EditAcCommand(AcceptanceCriteria ac, String newDesc) {
            this.ac = ac;

            this.oldDescription = ac.description;
            this.newDescription = newDesc;
        }

        /**
         * Executes the changes of the story edit
         */
        public void execute() {
            ac.description = newDescription;
        }

        /**
         * Undoes the changes of the story edit
         */
        public void undo() {
            ac.description = oldDescription;
        }

        /**
         * Gets the String value of the Command for editting AC descriptions.
         */
        public String getString() {
            return "the edit of Acceptance Criteria \"" + ac.toString() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(ac)) {
                    this.ac = (AcceptanceCriteria) item;
                    mapped = true;
                }
            }
            return mapped;
        }
    }

    @Override
    public String toString() {
        return this.description;
    }
}