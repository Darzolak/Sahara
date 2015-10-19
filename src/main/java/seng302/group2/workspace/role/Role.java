/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.workspace.role;

import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.sceneswitch.switchStrategies.workspace.RoleInformationSwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.tag.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javafx.collections.FXCollections.observableArrayList;


/**
 * A class representing the Roles a person may have within a Workspace, Team or Project.
 * @author swi67
 */
public class Role extends SaharaItem implements Serializable {
    private String shortName;
    private String description;
    private RoleType type;
    private transient ObservableList<Skill> requiredSkills = observableArrayList();
    private List<Skill> serializableRequiredSkills = new ArrayList<>();
    private boolean defaultRole;

    /**
     * Basic Role constructor
     */
    public Role() {
        // Initialize as a SaharaItem
        super("Untitled Role");
        this.shortName = "Untitled Role";
        this.description = "";
        this.type = RoleType.NONE;

        setInformationSwitchStrategy(new RoleInformationSwitchStrategy());
    }

    /**
     * Gets the set of SaharaItems 'belonging' to the Role (It's required skills).
     * @return A set of SaharaItems belonging to the role
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        Set<SaharaItem> items = new HashSet<>();
        items.addAll(requiredSkills);
        return items;
    }

    /**
     * Role Constructor
     *
     * @param shortName short name to be set
     * @param type      type of role to be set
     */
    public Role(String shortName, RoleType type) {
        // Initialize as a SaharaItem
        super(shortName);
        this.shortName = shortName;
        this.type = type;

        setInformationSwitchStrategy(new RoleInformationSwitchStrategy());
    }

    /**
     * Role Constructor
     *
     * @param shortName   short name to be set
     * @param type        type of role to be set
     * @param description brief description of role
     */
    public Role(String shortName, RoleType type, String description) {
        // Initialize as a SaharaItem
        super(shortName);
        this.shortName = shortName;
        this.description = description;
        this.type = type;

        setInformationSwitchStrategy(new RoleInformationSwitchStrategy());
    }

    /**
     * Role Constructor
     *
     * @param shortName short name to be set
     * @param type      type of role to be set
     * @param skills    skills required for role
     */
    public Role(String shortName, RoleType type, ObservableList<Skill> skills) {
        // Initialize as a SaharaItem
        super(shortName);
        this.shortName = shortName;
        this.description = "";
        this.requiredSkills = skills;
        this.type = type;

        setInformationSwitchStrategy(new RoleInformationSwitchStrategy());
    }

    /**
     * Role Constructor
     *
     * @param shortName   short name to be set
     * @param type        type of role to be set
     * @param description brief description of role
     * @param skills      skills required for role
     */
    public Role(String shortName, RoleType type, String description, ObservableList<Skill> skills) {
        // Initialize as a SaharaItem
        super(shortName);
        this.shortName = shortName;
        this.description = description;
        this.requiredSkills = skills;
        this.type = type;

        setInformationSwitchStrategy(new RoleInformationSwitchStrategy());
    }

    /**
     * Searches all roles in the workspace to find the role for the given type
     *
     * @param type The type of role to return
     * @return The role in the workspace of the given type
     */
    public static Role getRoleFromType(RoleType type) {
        Role role = null;
        for (Role wsrole : Global.currentWorkspace.getRoles()) {
            if (wsrole.getType().equals(type)) {
                role = wsrole;
                break;
            }
        }
        return role;
    }


    // <editor-fold defaultstate="collapsed" desc="Getters">

    /**
     * Gets the Role's short name
     *
     * @return The short name of the role
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * Sets the role's short name
     *
     * @param shortName the short name to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the Role's description
     * Role
     *
     * @return The description name of the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the role's short name
     *
     * @param description the short name to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the type of role
     *
     * @return Type of role
     */
    public RoleType getType() {
        return this.type;
    }

    /**
     * Sets the type of role
     *
     * @param type RoleType to set
     */
    public void setType(RoleType type) {
        this.type = type;
    }

    /**
     * Gets the required skills for a role
     *
     * @return list of skills
     */
    public ObservableList<Skill> getRequiredSkills() {
        return this.requiredSkills;
    }


    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">

    /**
     * Gets the required skills for a role
     *
     * @return list of skills
     */
    public List<Skill> getSerializableRequiredSkills() {
        return this.serializableRequiredSkills;
    }

    /**
     * Gets the default state of the role
     *
     * @return whether the role is a default role or not
     */
    public boolean isDefault() {
        return this.defaultRole;
    }

    /**
     * Set the default state of the role
     *
     * @param isDefault Boolean type of default to set
     */
    public void setDefault(boolean isDefault) {
        this.defaultRole = isDefault;
    }

    /**
     * Prepares a role to be serialized.
     */
    public void prepSerialization() {
        serializableRequiredSkills.clear();
        for (Skill skill : requiredSkills) {
            this.serializableRequiredSkills.add(skill);
        }

        prepTagSerialization();
    }

    //</editor-fold>  

    /**
     * Deserialization post-processing.
     */
    public void postDeserialization() {
        requiredSkills.clear();
        for (Skill skill : serializableRequiredSkills) {
            this.requiredSkills.add(skill);
        }

        postTagDeserialization();
    }

    /**
     * Method for creating an XML element for the Role within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element roleElement = ReportGenerator.doc.createElement("role");

        //WorkSpace Elements
        Element roleID = ReportGenerator.doc.createElement("ID");
        roleID.appendChild(ReportGenerator.doc.createTextNode(String.valueOf(id)));
        roleElement.appendChild(roleID);

        Element roleShortName = ReportGenerator.doc.createElement("identifier");
        roleShortName.appendChild(ReportGenerator.doc.createTextNode(getShortName()));
        roleElement.appendChild(roleShortName);

        Element roleDescription = ReportGenerator.doc.createElement("description");
        roleDescription.appendChild(ReportGenerator.doc.createTextNode(getDescription()));
        roleElement.appendChild(roleDescription);

        Element roleRequiredSkills = ReportGenerator.doc.createElement("required-skills");
        for (Skill skill : getRequiredSkills()) {
            Element skillElement = skill.generateXML();
            roleRequiredSkills.appendChild(skillElement);
        }
        roleElement.appendChild(roleRequiredSkills);

        Element roleTagElement = ReportGenerator.doc.createElement("tags");
        for (Tag tag : this.getTags()) {
            Element tagElement = tag.generateXML();
            roleTagElement.appendChild(tagElement);
        }
        roleElement.appendChild(roleTagElement);

        return roleElement;
    }

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
     * Overridden toString method for the Role class
     * @return The string representation of the Role 
     */
    @Override
    public String toString() {
        return this.shortName;
    }

    public enum RoleType {
        SCRUM_MASTER, PRODUCT_OWNER, DEVELOPMENT_TEAM_MEMBER, NONE
    }
}
