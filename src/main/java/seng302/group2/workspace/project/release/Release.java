/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.workspace.project.release;

import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.sceneswitch.switchStrategies.workspace.project.ReleaseInformationSwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.tag.Tag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A release is a sub-member of project and contains information about a release of a project
 *
 * @author Shinobu, Jordane
 */
public class Release extends SaharaItem implements Comparable<Release> {
    private String shortName;
    private String description;
    private LocalDate estimatedDate;
    private Project project;


    /**
     * Basic constructor
     */
    public Release() {
        super("Untitled Release");
        this.shortName = "Untitled Release";
        this.description = "Release without project assigned should not exist";
        this.estimatedDate = LocalDate.now();
        this.project = new Project();

        setInformationSwitchStrategy(new ReleaseInformationSwitchStrategy());
    }

    /** Required Constructor
     *
     * @param shortName short name to be set
     * @param project   project to be set
     */
    public Release(String shortName, Project project) {
        super(shortName);
        this.shortName = shortName;
        this.project = project;

        setInformationSwitchStrategy(new ReleaseInformationSwitchStrategy());
    }


    /**
     * Complete Constructor
     *
     * @param shortName   short name to be set
     * @param description description to be set
     * @param releaseDate release date to be set
     * @param project     project to be set
     */
    public Release(String shortName, String description, LocalDate releaseDate, Project project) {
        this.shortName = shortName;
        this.description = description;
        this.estimatedDate = releaseDate;
        this.project = project;

        setInformationSwitchStrategy(new ReleaseInformationSwitchStrategy());
    }


    /**

/**
     * Returns the items held by the release, blank as the release has no child items.
     * @return a blank hash set
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        return new HashSet<>();
    }


    // <editor-fold defaultstate="collapsed" desc="Getters">


    /**

  * Gets short name of the release
     *
     * @return short name of the release
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * Sets the short Name of the release
     *
     * @param shortName short name to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the description of the release
     *
     * @return the description of the release
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the Description of the release
     *
     * @param description The description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the estimated release date for the release
     *
     * @return the estimated release date of the release
     */
    public LocalDate getEstimatedDate() {
        return this.estimatedDate;
    }


    //</editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Setters"> 

    /**
     * Sets the estimated Release Date of the release
     *
     * @param releaseDate The release date to set
     */
    public void setEstimatedDate(LocalDate releaseDate) {
        this.estimatedDate = releaseDate;
    }

    /**
     * Gets the project the release belongs to
     *
     * @return project
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * Sets the project the release belongs to
     *
     * @param project project to set
     */
    public void setProject(Project project) {
        //this.project.removeWithoutUndo(this);
        this.project = project;
        //project.add(this);
    }

    /**
     * Gets the release date as a string
     *
     * @return the release date as a string
     */
    public String getDateString() {
        if (this.estimatedDate == null) {
            return "";
        }
        else {
            try {
                return this.getEstimatedDate().format(Global.dateFormatter);
            }
            catch (Exception e) {
                return "";
            }
        }
    }

    /**
     * Sets the project the release belongs to without an undoable command
     *
     * @param project project to set
     */
    public void setProjectWithoutUndo(Project project) {
        this.project = project;
        project.addWithoutUndo(this);
    }

    //</editor-fold>

    /**
     * Compares the release to another release based on their short names
     *
     * @param compareRelease The release to compare to
     * @return The string comparison or the releases short names
     */
    @Override
    public int compareTo(Release compareRelease) {
        String release1ShortName = this.getShortName();
        String release2ShortName = compareRelease.getShortName();
        return release1ShortName.compareTo(release2ShortName);
    }


    /**
     * Deletes a release from the given project.
     */
    public void deleteRelease() {
        Command command = new DeleteReleaseCommand(this);
        Global.commandManager.executeCommand(command);
    }

    /**
     * Method for creating an XML element for the Release within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element releaseElement = ReportGenerator.doc.createElement("release");

        //WorkSpace Elements
        Element releaseID = ReportGenerator.doc.createElement("ID");
        releaseID.appendChild(ReportGenerator.doc.createTextNode(String.valueOf(id)));
        releaseElement.appendChild(releaseID);

        Element releaseShortName = ReportGenerator.doc.createElement("identifier");
        releaseShortName.appendChild(ReportGenerator.doc.createTextNode(getShortName()));
        releaseElement.appendChild(releaseShortName);

        Element releaseDescription = ReportGenerator.doc.createElement("description");
        releaseDescription.appendChild(ReportGenerator.doc.createTextNode(getDescription()));
        releaseElement.appendChild(releaseDescription);

        Element releaseDate = ReportGenerator.doc.createElement("release-date");
        releaseDate.appendChild(ReportGenerator.doc.createTextNode(getDateString()));
        releaseElement.appendChild(releaseDate);

        Element releaseTagElement = ReportGenerator.doc.createElement("tags");
        for (Tag tag : this.getTags()) {
            Element tagElement = tag.generateXML();
            releaseTagElement.appendChild(tagElement);
        }
        releaseElement.appendChild(releaseTagElement);

        return releaseElement;
    }


    /**
     * Returns a list of the children of the release class
     * @return null (for now)
     */
    @Override
    public ObservableList<SaharaItem> getChildren() {
        return null;
    }

    /**
     * The overidden toString method of the Release class.
     * @return A string representation of the Release.
     */
    @Override
    public String toString() {
        return this.shortName;
    }

    /**
     * Creates a Release edit command and executes it with the Global Command Manager, updating
     * the release with the new parameter values.
     *
     * @param newShortName     The new short name
     * @param newDescription   The new description
     * @param newEstimatedDate The new estimated date
     * @param newTags          The new tags

     */
    public void edit(String newShortName, String newDescription, LocalDate newEstimatedDate, ArrayList<Tag> newTags) {
        Command relEdit = new ReleaseEditCommand(this, newShortName, newDescription,
                newEstimatedDate, newTags);
        Global.commandManager.executeCommand(relEdit);
    }


    /**
     * A command class that allows the executing and undoing of release edits
     */
    private class ReleaseEditCommand implements Command {
        private Release release;

        private String shortName;
        private String description;
        private LocalDate estimatedDate;
        private Set<Tag> releaseTags = new HashSet<>();
        private Set<Tag> globalTags = new HashSet<>();

        private Project project;

        private String oldShortName;
        private String oldDescription;
        private LocalDate oldEstimatedDate;
        private Set<Tag> oldReleaseTags = new HashSet<>();
        private Set<Tag> oldGlobalTags = new HashSet<>();
        
        /**
         * Constructor for the Release Edit command
         * @param release The release to be edited
         * @param newShortName The new short name for the release
         * @param newDescription The new description for the release
         * @param newEstimatedDate The new estimated date for the release
         * @param newTags        The new tags

         */
        private ReleaseEditCommand(Release release, String newShortName, String newDescription,
                                   LocalDate newEstimatedDate, ArrayList<Tag> newTags) {
            this.release = release;

            if (newTags == null) {
                newTags = new ArrayList<>();
            }

            this.shortName = newShortName;
            this.description = newDescription;
            this.estimatedDate = newEstimatedDate;
            this.releaseTags.addAll(newTags);
            this.globalTags.addAll(newTags);
            this.globalTags.addAll(Global.currentWorkspace.getAllTags());

            this.project = release.project;

            this.oldShortName = release.shortName;
            this.oldDescription = release.description;
            this.oldEstimatedDate = release.estimatedDate;
            this.oldReleaseTags.addAll(release.getTags());
            this.oldGlobalTags.addAll(Global.currentWorkspace.getAllTags());
        }

        /**
         * Executes/Redoes the changes of the release edit
         */
        public void execute() {
            release.shortName = shortName;
            release.description = description;
            release.estimatedDate = estimatedDate;

            //Add any created tags to the global collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(globalTags);
            //Add the tags a release has to their list of tags
            release.getTags().clear();
            release.getTags().addAll(releaseTags);

            Collections.sort(project.getReleases());

        }

        /**
         * Undoes the changes of the release edit
         */
        public void undo() {
            release.shortName = oldShortName;
            release.description = oldDescription;
            release.estimatedDate = oldEstimatedDate;
            //Adds the old global tags to the overall collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(oldGlobalTags);

            //Changes the releases list of tags to what they used to be
            release.getTags().clear();
            release.getTags().addAll(oldReleaseTags);

            Collections.sort(project.getReleases());
        }

        /**
         * Gets the String value of the Command for editting releases.
         */
        public String getString() {
            return "the edit of Release \"" + release.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_rl = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(release)) {
                    this.release = (Release) item;
                    mapped_rl = true;
                }
            }
            boolean mapped_project = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(project)) {
                    this.project = (Project) item;
                    mapped_project = true;
                }
            }

            //Tag collections
            for (Tag tag : releaseTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        releaseTags.remove(tag);
                        releaseTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : oldReleaseTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        oldReleaseTags.remove(tag);
                        oldReleaseTags.add((Tag) item);
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

            return mapped_rl && mapped_project;
        }
    }

    /**
     * A command class for allowing the deletion of Releases.
     */
    private class DeleteReleaseCommand implements Command {
        private Release release;
        private Project proj;

        /**
         * Constructor for the release deletion command
         * @param release The release to be deleted.
         */
        DeleteReleaseCommand(Release release) {
            this.release = release;
            this.proj = release.getProject();
        }

        /**
         * Executes the release deletion command.
         */
        public void execute() {
            proj.getReleases().remove(release);
            //release.setProject(null);
        }

        /**
         * Undoes the release deletion command.
         */
        public void undo() {
            proj.getReleases().add(release);
            //release.setProject(proj);
        }

        /**
         * Gets the String value of the Command for deleting releases.
         */
        @Override
        public String getString() {
            return "the deletion of Release \"" + release.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_rl = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(release)) {
                    this.release = (Release) item;
                    mapped_rl = true;
                }
            }
            boolean mapped_project = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(project)) {
                    this.proj = (Project) item;
                    mapped_project = true;
                }
            }
            return mapped_rl && mapped_project;
        }
    }
}
