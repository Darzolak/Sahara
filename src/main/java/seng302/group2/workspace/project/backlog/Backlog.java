package seng302.group2.workspace.project.backlog;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.sceneswitch.switchStrategies.workspace.project.BacklogInformationSwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.estimation.EstimationScalesDictionary;
import seng302.group2.workspace.tag.Tag;

import java.io.Serializable;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * A class to represent a backlog of stories within a project.
 * Created by cvs20 on 19/05/15.
 */
public class Backlog extends SaharaItem implements Serializable, Comparable<Backlog> {
    private String shortName;
    private String longName;
    private String description;
    private Person productOwner;
    private transient ObservableList<Story> stories = observableArrayList();
    private List<Story> serializableStories = new ArrayList<>();

    private Project project;
    private String scale = "Fibonacci";





    /**
     * Basic Backlog constructor
     */
    public Backlog() {
        super("Untitled Backlog");
        this.shortName = "Untitled Backlog";
        this.longName = "Untitled Backlog";
        this.description = "";
        this.productOwner = null;
        this.project = null;

        setInformationSwitchStrategy(new BacklogInformationSwitchStrategy());
    }

    /** Backlog Constructor with all fields
     *
     * @param shortName    short name to identify the story
     * @param longName     long name
     * @param description  description
     * @param productOwner product owner of the backlog
     * @param project      the project the backlog belongs to
     * @param scale        the estimation scale of the backlog
     */
    public Backlog(String shortName, String longName, String description,
                   Person productOwner, Project project, String scale) {
        super(shortName);
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.productOwner = productOwner;
        this.project = project;
        this.scale = scale;

        setInformationSwitchStrategy(new BacklogInformationSwitchStrategy());
    }


    /**

/**
     * Gets the set of SaharaItems 'belonging' to the Backlog (It's Stories).
     * @return A set of SaharaItems belonging to the backlog
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        Set<SaharaItem> items = new HashSet<>();

        for (Story story : stories) {
            items.addAll(story.getItemsSet());
        }
        items.addAll(stories);


        return items;
    }


    /**

  * Adds listeners to the backlog stories for sorting
     */
    public void addListeners() {
        stories.addListener((ListChangeListener<Story>) change -> {
                if (change.next() && !change.wasPermutated()) {
                    if (stories.size() != 0) {
                        Collections.sort(stories, Story.StoryPriorityComparator);
                    }
                }
            });

    }


    // <editor-fold defaultstate="collapsed" desc="Getters">

    /**
     * Gets the short name of the Backlog
     *
     * @return the short name
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * Sets the short name of the Backlog
     *
     * @param shortName short name to be set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the long name of the Backlog
     *
     * @return the long name
     */
    public String getLongName() {
        return this.longName;
    }

    /**
     * Sets the long name of the Backlog
     *
     * @param longName long name to be set
     */
    public void setLongName(String longName) {
        this.longName = longName;
    }

    /**
     * Gets the description of the Backlog
     *
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the serializable stories of a backlog
     *
     * @return the serializable stories
     */

    /**
     * Sets the description of the Backlog
     *
     * @param description description to be set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the product owner of the Backlog
     *
     * @return productOwner
     */
    public Person getProductOwner() {
        return this.productOwner;
    }

    /**

  * Sets the product owner of the Backlog
     *
     * @param productOwner description to be set
     */
    public void setProductOwner(Person productOwner) {
        this.productOwner = productOwner;
    }

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">

    /**

  * Gets the estimation scale used in the Backlog
     *
     * @return Estimation scale name
     */
    public String getScale() {
        return this.scale;
    }

    /**

  * Gets the project the Backlog is assigned to
     *
     * @return The description of the team
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * Sets the Backlog's project
     *
     * @param project the project the backlog has been added to
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Gets the backlogs list of stories
     *
     * @return The ObservableList of stories
     */
    public ObservableList<Story> getStories() {
        this.serializableStories.clear();
        for (Object item : this.stories) {
            this.serializableStories.add((Story) item);
        }
        return this.stories;
    }



    /**
     * Gets the serializable stories belonging to the backlog
     * @return A List of Stories
     */
    public List<Story> getSerializableStories() {
        return serializableStories;
    }

    /**
     * Adds a story to the backlogs list of stories
     *
     * @param story Story to add
     */
    public void add(Story story) {
        Command addStory = new AddStoryCommand(this, story);
        Global.commandManager.executeCommand(addStory);
    }

    //</editor-fold>


    /**
     * Prepares the backlog to be serialized.
     */
    public void prepSerialization() {
        serializableStories.clear();
        for (Story story : stories) {
            story.prepSerialization();
            this.serializableStories.add(story);
        }
        prepTagSerialization();
    }


    /**
     * Deserialization post-processing.
     */
    public void postDeserialization() {
        stories.clear();
        for (Story story : serializableStories) {
            story.postDeserialization();
            this.stories.add(story);
        }
        postTagDeserialization();

        Collections.sort(this.stories, Story.StoryPriorityComparator);
    }



    /**
     * Method for creating an XML element for the Backlog within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element backlogElement = ReportGenerator.doc.createElement("backlog");

        //WorkSpace Elements
        Element backlogID = ReportGenerator.doc.createElement("ID");
        backlogID.appendChild(ReportGenerator.doc.createTextNode(String.valueOf(id)));
        backlogElement.appendChild(backlogID);

        Element backlogShortName = ReportGenerator.doc.createElement("identifier");
        backlogShortName.appendChild(ReportGenerator.doc.createTextNode(shortName));
        backlogElement.appendChild(backlogShortName);

        Element backlogLongName = ReportGenerator.doc.createElement("long-name");
        backlogLongName.appendChild(ReportGenerator.doc.createTextNode(longName));
        backlogElement.appendChild(backlogLongName);

        Element backlogDescription = ReportGenerator.doc.createElement("description");
        backlogDescription.appendChild(ReportGenerator.doc.createTextNode(description));
        backlogElement.appendChild(backlogDescription);

        Element backlogProductOwner = ReportGenerator.doc.createElement("product-owner");
        backlogProductOwner.appendChild(ReportGenerator.doc.createTextNode(productOwner.toString()));
        backlogElement.appendChild(backlogProductOwner);

        Element backlogScale = ReportGenerator.doc.createElement("estimation-scale");
        backlogScale.appendChild(ReportGenerator.doc.createTextNode(scale));
        backlogElement.appendChild(backlogScale);

        Element backlogStories = ReportGenerator.doc.createElement("stories");
        for (Story story : getStories()) {
            Element storyElement = story.generateXML();
            backlogStories.appendChild(storyElement);
        }
        backlogElement.appendChild(backlogStories);

        Element backlogTagElement = ReportGenerator.doc.createElement("tags");
        for (Tag tag : this.getTags()) {
            Element tagElement = tag.generateXML();
            backlogTagElement.appendChild(tagElement);
        }
        backlogElement.appendChild(backlogTagElement);

        return backlogElement;
    }


    /**
     * Gets the children of the SaharaItem
     *
     * @return The items of the SaharaItem
     */
    @Override
    public ObservableList getChildren() {
        ObservableList<SaharaItem> children = observableArrayList();
        for (Story story : this.getStories()) {
            children.addAll(story);
        }
        return children;
    }


    /**
     * A comparator for backlogs based on short names
     *
     * @param backlog The backlog to compare to
     * @return The result of the comparison of the short name strings
     */
    @Override
    public int compareTo(Backlog backlog) {
        String story1ShortName = this.getShortName();
        String story2ShortName = backlog.getShortName();
        return story1ShortName.compareTo(story2ShortName);
    }

    /**
     * An overridden version for the String representation of a Backlog
     *
     * @return The short name of the Backlog
     */
    @Override
    public String toString() {
        return this.shortName;
    }

    /**
     * Creates a Backlog edit command and executes it with the Global Command Manager, updating
     * the Backlog with the new parameter values.
     *
     * @param newShortName    The new short name
     * @param newLongName     The new long name
     * @param newDescription  The new description
     * @param newProductOwner The new product owner
     * @param newProject      The new project
     * @param newScale        The new estimation scale
     * @param newStories      The new stories in the backlog
     * @param newTags        The new tags
     */
    public void edit(String newShortName, String newLongName, String newDescription, Person newProductOwner,
                     Project newProject, String newScale, Collection<Story> newStories, ArrayList<Tag> newTags) {
        Command relEdit = new BacklogEditCommand(this, newShortName, newLongName, newDescription,
                newProductOwner, newProject, newScale, newStories, newTags);
        Global.commandManager.executeCommand(relEdit);
    }

    /**
     * Deletes a backlog from the given project.
     */
    public void deleteBacklog() {
        Command command = new DeleteBacklogCommand(this);
        Global.commandManager.executeCommand(command);
    }

    /**
     * A command class that allows the executing and undoing of backlog edits
     */
    private class BacklogEditCommand implements Command {
        private Backlog backlog;

        private String shortName;
        private String longName;
        private String description;
        private Person productOwner;
        private Project project;
        private String scale;
        private Collection<Story> stories = new HashSet<>();
        private Set<Tag> backlogTags = new HashSet<>();
        private Set<Tag> globalTags = new HashSet<>();

        private String oldShortName;
        private String oldLongName;
        private String oldDescription;
        private Person oldProductOwner;
        private Project oldProject;
        private String oldScale;
        private Collection<Story> oldStories = new HashSet<>();
        private Map<Story, String> oldEstimateDict = new HashMap<>();
        private Map<Story, Boolean> oldReadyStateDict = new HashMap<>();
        private Set<Tag> oldBacklogTags = new HashSet<>();
        private Set<Tag> oldGlobalTags = new HashSet<>();

        private BacklogEditCommand(Backlog backlog, String newShortName, String newLongName,
                                   String newDescription, Person newProductOwner, Project newProject,
                                   String newScale, Collection<Story> newStories, ArrayList<Tag> newTags) {
            this.backlog = backlog;

            if (newTags == null) {
                newTags = new ArrayList<>();
            }

            this.shortName = newShortName;
            this.longName = newLongName;
            this.description = newDescription;
            this.productOwner = newProductOwner;
            this.project = newProject;
            this.scale = newScale;
            this.stories.addAll(newStories);
            this.backlogTags.addAll(newTags);
            this.globalTags.addAll(newTags);
            this.globalTags.addAll(Global.currentWorkspace.getAllTags());

            this.oldShortName = backlog.shortName;
            this.oldLongName = backlog.longName;
            this.oldDescription = backlog.description;
            this.oldProductOwner = backlog.productOwner;
            this.oldProject = backlog.project;
            this.oldScale = backlog.scale;
            this.oldStories.addAll(backlog.stories);
            this.oldBacklogTags.addAll(backlog.getTags());
            this.oldGlobalTags.addAll(Global.currentWorkspace.getAllTags());

            for (Story story : oldStories) {
                oldEstimateDict.put(story, story.getEstimate());
                oldReadyStateDict.put(story, story.getReady());
            }
        }

        /**
         * Executes/Redoes the changes of the backlog edit
         */
        public void execute() {
            backlog.shortName = shortName;
            backlog.longName = longName;
            backlog.description = description;
            backlog.productOwner = productOwner;
            backlog.project = project;
            backlog.scale = scale;

            backlog.stories.removeAll(oldStories);
            backlog.stories.addAll(stories);

            if (!scale.equals(oldScale)) {
                for (Story story : stories) {
                    story.setEstimate(EstimationScalesDictionary.getScaleValue(
                            EstimationScalesDictionary.DefaultValues.NONE));
                    story.setReady(false);
                }
            }

            Set<Story> allStories = new HashSet<>();
            allStories.addAll(stories);
            allStories.addAll(oldStories);
            for (Story story : allStories) {
                if (!stories.contains(story)) {
                    // Not a story in the backlog
                    if (project != null) {
                        project.getUnallocatedStories().add(story);
                    }
                    story.setProject(project);
                    story.setBacklog(null);
                }
                else {
                    // In the backlog
                    if (project != null) {
                        project.getUnallocatedStories().remove(story);
                    }
                    story.setProject(project);
                    story.setBacklog(backlog);
                }
            }

            Set<Story> removedStories = new HashSet<>(oldStories);
            removedStories.removeAll(stories);
            for (Story story : removedStories) {
                story.setReady(false);
                story.setEstimate(EstimationScalesDictionary.getScaleValue(
                        EstimationScalesDictionary.DefaultValues.NONE));
            }

            //Add any created tags to the global collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(globalTags);
            //Add the tags a backlog has to their list of tags
            backlog.getTags().clear();
            backlog.getTags().addAll(backlogTags);

            Collections.sort(backlog.stories, Story.StoryPriorityComparator);
        }

        /**
         * Undoes the changes of the backlog edit
         */
        public void undo() {
            backlog.shortName = oldShortName;
            backlog.longName = oldLongName;
            backlog.description = oldDescription;
            backlog.productOwner = oldProductOwner;
            backlog.project = oldProject;
            backlog.scale = oldScale;

            backlog.stories.removeAll(stories);
            backlog.stories.addAll(oldStories);

            Set<Story> allStories = new HashSet<>();
            allStories.addAll(stories);
            allStories.addAll(oldStories);
            for (Story story : allStories) {
                if (!oldStories.contains(story)) {
                    // Not a story in the backlog
                    if (oldProject != null) {
                        oldProject.getUnallocatedStories().add(story);
                    }
                    story.setProject(oldProject);
                    story.setBacklog(null);
                }
                else {
                    // In the backlog
                    if (oldProject != null) {
                        oldProject.getUnallocatedStories().remove(story);
                    }
                    story.setProject(oldProject);
                    story.setBacklog(backlog);
                }
            }

            for (Story story : oldStories) {
                story.setEstimate(oldEstimateDict.get(story));
                story.setReady(oldReadyStateDict.get(story));
            }

            //Adds the old global tags to the overall collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(oldGlobalTags);

            //Changes the backlogs list of tags to what they used to be
            backlog.getTags().clear();
            backlog.getTags().addAll(oldBacklogTags);

            Collections.sort(backlog.stories, Story.StoryPriorityComparator);
        }

        /**
         * Gets the String value of the Command for editting backlogs.
         */
        public String getString() {
            return "the edit of Backlog \"" + backlog.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override // BL, PO, Proj, Stories?
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_bl = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(backlog)) {
                    this.backlog = (Backlog) item;
                    mapped_bl = true;
                }
            }
            boolean mapped_po = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(productOwner)) {
                    this.productOwner = (Person) item;
                    mapped_po = true;
                }
            }
            boolean mapped_old_po = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(oldProductOwner)) {
                    this.oldProductOwner = (Person) item;
                    mapped_old_po = true;
                }
            }
            boolean mapped_proj = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(project)) {
                    this.project = (Project) item;
                    mapped_proj = true;
                }
            }
            boolean mapped_old_proj = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(oldProject)) {
                    this.oldProject = (Project) item;
                    mapped_old_proj = true;
                }
            }


            // Story collections
            for (Story story : stories) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(story)) {
                        stories.remove(story);
                        stories.add((Story)item);
                        break;
                    }
                }
            }

            for (Story story : oldStories) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(story)) {
                        oldStories.remove(story);
                        oldStories.add((Story)item);
                        break;
                    }
                }
            }

            //Tag collections
            for (Tag tag : backlogTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        backlogTags.remove(tag);
                        backlogTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : oldBacklogTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        oldBacklogTags.remove(tag);
                        oldBacklogTags.add((Tag) item);
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

            return mapped_po && mapped_bl && mapped_proj && mapped_old_proj && mapped_old_po;
        }
    }

    /**
     * A command class for allowing the deletion of Backlogs.
     */
    private class DeleteBacklogCommand implements Command {
        private Backlog backlog;
        private Project proj;

        /**
         * Constructor for the backlog deletion command.
         * @param backlog The backlog to be deleted.
         */
        DeleteBacklogCommand(Backlog backlog) {
            this.backlog = backlog;
            this.proj = backlog.getProject();
        }

        /**
         * Executes the backlog deletion command.
         */
        public void execute() {
            //System.out.println("Exec Backlog Delete");
            proj.getBacklogs().remove(backlog);
            backlog.setProject(null);
            //release.setProject(null);
        }

        /**
         * Undoes the backlog deletion command.
         */
        public void undo() {
            //System.out.println("Undone Backlog Delete");
            proj.getBacklogs().add(backlog);
            backlog.setProject(proj);
            //release.setProject(proj);
        }

        /**
         * Gets the String value of the Command for deleting backlogs.
         */
        public String getString() {
            return "the deletion of Backlog \"" + backlog.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override // BL, PO, Proj, Stories?
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_bl = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(backlog)) {
                    this.backlog = (Backlog) item;
                    mapped_bl = true;
                }
            }
            boolean mapped_proj = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(project)) {
                    this.proj = (Project) item;
                    mapped_proj = true;
                }
            }
            return mapped_bl && mapped_proj;
        }
    }

    /**
     * A command class for allowing the addition of Stories to Backlogs.
     */
    private class AddStoryCommand implements Command {
        private Project proj;
        private Backlog backlog;
        private Story story;

        /**
         * Constructor for the story addition command.
         * @param backlog The backlog to which the story is to be added.
         * @param story The story to be added.
         */
        AddStoryCommand(Backlog backlog, Story story) {
            //this.proj = proj;
            this.backlog = backlog;
            this.story = story;
        }

        /**
         * Executes the Story addition command
         */
        public void execute() {
            /*if (proj != null)
            {
                proj.getBacklogs().add(backlog);  // Why? This is add story to bl, not bl to proj
            }*/
            backlog.stories.add(story);
            if (backlog.getProject() != null) {
                backlog.getProject().getUnallocatedStories().remove(story);
            }
        }

        /**
         * Undoes the story addition command.
         */
        public void undo() {
            /*if (proj != null)
            {
                proj.getBacklogs().remove(backlog);
            }*/
            backlog.stories.remove(story);
            if (backlog.getProject() != null) {
                backlog.getProject().getUnallocatedStories().add(story);
            }
        }

        /**
         * Gets the String value of the Command for adding a story.
         */
        public String getString() {
            return "the addition of Story \"" + story.getShortName() + "\" to Backlog \""
                    + backlog.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override // BL, Proj, Story
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_bl = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(backlog)) {
                    this.backlog = (Backlog) item;
                    mapped_bl = true;
                }
            }
            boolean mapped_proj = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(project)) {
                    this.proj = (Project) item;
                    mapped_proj = true;
                }
            }
            boolean mapped_story = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(story)) {
                    this.story = (Story) item;
                    mapped_story = true;
                }
            }
            return mapped_bl && mapped_proj && mapped_story;
        }
    }
}




