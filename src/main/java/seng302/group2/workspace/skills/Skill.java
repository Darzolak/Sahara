package seng302.group2.workspace.skills;

import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.sceneswitch.switchStrategies.workspace.SkillInformationSwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.tag.Tag;

import java.io.Serializable;
import java.util.*;

/**
 * A basic class to represent skills a person may have
 *
 * @author crw73
 */
public class Skill extends SaharaItem implements Serializable, Comparable<Skill> {
    private String shortName;
    private String description;

    /**
     * Basic Skill constructor
     */
    public Skill() {
        super("Untitled Skill");
        this.shortName = "Untitled Skill";
        this.description = "no description";

        setInformationSwitchStrategy(new SkillInformationSwitchStrategy());
    }

    /** Basic Skill constructor with all fields
     *
     * @param shortName   A unique short name to identify a Skill
     * @param description The Description of a skill
     */
    public Skill(String shortName, String description) {
        // Initialize as a SaharaItem
        super(shortName);

        this.shortName = shortName;
        this.description = description;
        setInformationSwitchStrategy(new SkillInformationSwitchStrategy());
    }

    /**

/**
     * Returns the items held by the Skill, blank as the skill has no child items.
     * @return a blank hash set
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        return new HashSet<>();
    }

    // <editor-fold defaultstate="collapsed" desc="Getters">

    /**

  * Gets a skills short name
     *
     * @return The short name of the skill
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * Sets a skills short name
     *
     * @param shortName the short name to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    //</editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Setters">

    /**
     * Gets a skills description
     *
     * @return The description of the skill
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets a skills description
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    //</editor-fold>

    /**
     * Gets the children of the SaharaItem
     *
     * @return The items of the SaharaItem
     */
    @Override
    public ObservableList<SaharaItem> getChildren() {
        return null;
    }


    /**
     * Compares the skill to another skill by their short names
     *
     * @param compareSkill The skill to compare to
     * @return The result of the string comparison between the teams' short names
     */
    @Override
    public int compareTo(Skill compareSkill) {
        String skill1ShortName = this.getShortName();
        String skill2ShortName = compareSkill.getShortName();
        return skill1ShortName.compareTo(skill2ShortName);
    }


    /**
     * Deletes the skill and removes it from any people who have the skill.
     * Cannot delete Product Owner or Scrum Master skills.
     */
    public void deleteSkill() {
        if (this.shortName.equals("Scrum Master") || this.shortName.equals("Product Owner")) {
            return;
        }
        Command deleteSkill = new DeleteSkillCommand(this);
        Global.commandManager.executeCommand(deleteSkill);
    }


    /**
     * Gets a list of people in the current workspace that have this skill
     *
     * @return People with the skill
     */
    public List<Person> getPeopleWithSkill() {
        List<Person> people = new ArrayList<>();
        for (Person person : Global.currentWorkspace.getPeople()) {
            if (person.getSkills().contains(this)) {
                people.add(person);
            }
        }
        return people;
    }


    /**
     * Creates a skill edit command and executes it with the Global Command Manager, updating
     * the skill with the new parameter values.
     *
     * @param newShortName   The new short name
     * @param newDescription The new description
     * @param newTags        The new tags of the Skill

     */
    public void edit(String newShortName, String newDescription, ArrayList<Tag> newTags) {
        Command edit = new SkillEditCommand(this, newShortName, newDescription, newTags);
        Global.commandManager.executeCommand(edit);
    }

    /**
     * Method for creating an XML element for the Skill within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element skillElement = ReportGenerator.doc.createElement("skill");

        //WorkSpace Elements
        Element skillID = ReportGenerator.doc.createElement("ID");
        skillID.appendChild(ReportGenerator.doc.createTextNode(String.valueOf(id)));
        skillElement.appendChild(skillID);

        Element skillShortName = ReportGenerator.doc.createElement("shortname");
        skillShortName.appendChild(ReportGenerator.doc.createTextNode(shortName));
        skillElement.appendChild(skillShortName);

        Element skillDescription = ReportGenerator.doc.createElement("description");
        skillDescription.appendChild(ReportGenerator.doc.createTextNode(description));
        skillElement.appendChild(skillDescription);

        Element skillTagElement = ReportGenerator.doc.createElement("tags");
        for (Tag tag : this.getTags()) {
            Element tagElement = tag.generateXML();
            skillTagElement.appendChild(tagElement);
        }
        skillElement.appendChild(skillTagElement);

        return skillElement;
    }

    /**
     * An overridden version for the String representation of a Skill
     *
     * @return The short name of the Skill
     */
    @Override
    public String toString() {
        return this.shortName;
    }



    /**
     * A command class that allows the executing and undoing of skill edits
     */
    private class SkillEditCommand implements Command {
        private Skill skill;

        private String shortName;
        private String description;
        private Set<Tag> skillTags = new HashSet<>();
        private Set<Tag> globalTags = new HashSet<>();

        private String oldShortName;
        private String oldDescription;
        private Set<Tag> oldSkillTags = new HashSet<>();
        private Set<Tag> oldGlobalTags = new HashSet<>();

        private SkillEditCommand(Skill skill, String newShortName, String newDescription, ArrayList<Tag> newTags) {
            this.skill = skill;

            if (newTags == null) {
                newTags = new ArrayList<>();
            }

            this.shortName = newShortName;
            this.description = newDescription;
            this.skillTags.addAll(newTags);
            this.globalTags.addAll(newTags);
            this.globalTags.addAll(Global.currentWorkspace.getAllTags());

            this.oldShortName = skill.shortName;
            this.oldDescription = skill.description;
            this.oldSkillTags.addAll(skill.getTags());
            this.oldGlobalTags.addAll(Global.currentWorkspace.getAllTags());
        }

        /**
         * Executes/Redoes the changes of the skill edit
         */
        public void execute() {
            skill.shortName = shortName;
            skill.description = description;

            //Add any created tags to the global collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(globalTags);
            //Add the tags a skill has to their list of tags
            skill.getTags().clear();
            skill.getTags().addAll(skillTags);

            Collections.sort(Global.currentWorkspace.getSkills());
        }

        /**
         * Undoes the changes of the skill edit
         */
        public void undo() {
            skill.shortName = oldShortName;
            skill.description = oldDescription;

            //Adds the old global tags to the overall collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(oldGlobalTags);

            //Changes the skills list of tags to what they used to be
            skill.getTags().clear();
            skill.getTags().addAll(oldSkillTags);

            Collections.sort(Global.currentWorkspace.getSkills());
        }

        /**
         * Gets the String value of the Command for editting skills.
         */
        public String getString() {
            return "the edit of Skill \"" + skill.getShortName() + "\"";
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
                if (item.equivalentTo(skill)) {
                    this.skill = (Skill) item;
                    mapped = true;
                }
            }

            //Tag collections
            for (Tag tag : skillTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        skillTags.remove(tag);
                        skillTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : oldSkillTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        oldSkillTags.remove(tag);
                        oldSkillTags.add((Tag)item);
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
     * A command class for allowing the deletion of Skills from a Workspace.
     */
    private class DeleteSkillCommand implements Command {
        private Skill skill;
        private List<Person> people;

        /**
         * Constructor for the skill deletion command
         * @param skill The skill to be deleted
         */
        DeleteSkillCommand(Skill skill) {
            this.skill = skill;
            this.people = skill.getPeopleWithSkill();
        }

        /**
         * Executes the skill deletion command
         */
        public void execute() {
            for (Person person : this.people) {
                person.getSkills().remove(skill);
            }
            Global.currentWorkspace.getSkills().remove(skill);
        }

        /**
         * Undoes the skill deletion command
         */
        public void undo() {
            for (Person person : this.people) {
                person.getSkills().add(skill);
            }
            Global.currentWorkspace.getSkills().add(skill);
        }

        /**
         * Gets the String value of the Command for deleting skills.
         */
        public String getString() {
            return "the deletion of Skill \"" + skill.getShortName() + "\"";
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
                if (item.equivalentTo(skill)) {
                    this.skill = (Skill) item;
                    mapped = true;
                }
            }
            return mapped;
        }
    }
}
