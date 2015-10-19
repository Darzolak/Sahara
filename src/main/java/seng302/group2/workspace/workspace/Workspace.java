/*
 * SENG302 Group 2
 */
package seng302.group2.workspace.workspace;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.w3c.dom.Element;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.sceneswitch.switchStrategies.workspace.WorkspaceInformationSwitchStrategy;
import seng302.group2.scenes.treeView.HierarchyTracker;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.util.revert.RevertManager;
import seng302.group2.util.serialization.SerialBuilder;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.categories.*;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.estimation.EstimationScalesDictionary;
import seng302.group2.workspace.project.story.tasks.Log;
import seng302.group2.workspace.project.story.tasks.Task;
import seng302.group2.workspace.roadMap.RoadMap;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * Basic workspace class that acts as the root object for Sahara and represents a real-world
 * company or work group space.
 *
 * @author Jordane Lew (jml168)
 */
@SuppressWarnings("deprecation")
public class Workspace extends SaharaItem implements Serializable {
    private static Gson gson = SerialBuilder.getBuilder();
    private String shortName;
    private String longName;
    private String description;
    private String lastSaveLocation = null;
    private EstimationScalesDictionary estimationScales;
    private transient boolean hasUnsavedChanges = false;

    // Workspace elements
    private transient ObservableList<Team> teams = observableArrayList();
    private List<Team> serializableTeams = new ArrayList<>();
    private transient ObservableList<Person> people = observableArrayList();
    private List<Person> serializablePeople = new ArrayList<>();
    private transient ObservableList<Skill> skills = observableArrayList();
    private List<Skill> serializableSkills = new ArrayList<>();
    private transient ObservableList<Project> projects = observableArrayList();
    private List<Project> serializableProjects = new ArrayList<>();
    private transient ObservableList<Role> roles = observableArrayList();
    private List<Role> serializableRoles = new ArrayList();
    private transient ObservableList<RoadMap> roadMaps = observableArrayList();
    private List<RoadMap> serializableRoadMaps = new ArrayList();
    private transient ObservableList<Tag> globalTags = observableArrayList();
    private List<Tag> serializableGlobalTags = new ArrayList<>();

    // Make the categories
    private transient Category projectCategory = new ProjectCategory();
    private transient Category teamsCategory = new TeamsCategory();
    private transient Category peopleCategory = new PeopleCategory();
    private transient Category roadMapCategory = new RoadMapCategory();
    private transient Category rolesCategory = new RolesCategory();
    private transient Category skillCategory = new SkillsCategory();

    private Map<SaharaItem, Boolean> hierarchyMap = new HashMap<>();
    
    private double version = App.version;


    /**
     * Basic Workspace constructor.
     */
    public Workspace() {
        super("Untitled Workspace");
        this.shortName = "Untitled Workspace";
        this.longName = "Untitled Workspace";
        this.description = "A blank workspace.";
        this.estimationScales = EstimationScalesDictionary.getEstimationScale();

        SaharaItem.setStartId(0, true);
        RevertManager.clear();

        this.createDefaultElements();

        addListeners();

        setInformationSwitchStrategy(new WorkspaceInformationSwitchStrategy());

        //updateDefaultRevert();
    }


    /**
     * Basic workspace constructor with input.
     *
     * @param shortName   A unique short name to identify the Workspace
     * @param fullName    The full Workspace name
     * @param description A description of the Workspace
     */
    public Workspace(String shortName, String fullName, String description) {
        super(shortName);
        this.shortName = shortName;
        this.longName = fullName;
        this.description = description;
        this.estimationScales = EstimationScalesDictionary.getEstimationScale();

        SaharaItem.setStartId(0, true);
        RevertManager.clear();

        this.createDefaultElements();

        addListeners();

        setInformationSwitchStrategy(new WorkspaceInformationSwitchStrategy());

        //updateDefaultRevert();
    }


    /**
     * Returns a set of all children items inside a workspace
     * @return A set of all children items inside this workspace
     */
    public Set<SaharaItem> getItemsSet() {
        Set<SaharaItem> items = new HashSet<>();
        items.add(this);

        for (Project proj : this.getProjects()) {
            items.add(proj);
            items.addAll(proj.getItemsSet());
        }
        for (Person person : this.getPeople()) {
            items.add(person);
            items.addAll(person.getItemsSet());
        }
        for (Skill skill : skills) {
            items.add(skill);
            items.addAll(skill.getItemsSet());
        }
        for (Team team : teams) {
            items.add(team);
            items.addAll(team.getItemsSet());
        }
        for (Role role : roles) {
            items.add(role);
            items.addAll(role.getItemsSet());
        }

        return items;
    }


    /**
     * Saves the current workspace as a file specified by the user.
     *
     * @param workspace The workspace to save
     * @param saveAs    If acting as save as
     * @return The corresponding SaveLoadResult status of the process
     */
    public static SaveLoadResult saveWorkspace(Workspace workspace, boolean saveAs) {
        // If there is no current workspace open, display a dialog and skip saving
        if (workspace == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No open workspace");
            alert.setContentText("There is currently no workspace open to save");
            alert.showAndWait();

            return SaveLoadResult.NULLWORKSPACE;
        }

        workspace = Workspace.prepSerialization(workspace);

        if (workspace.lastSaveLocation != null) {
            File f = new File(workspace.lastSaveLocation);
            if (!f.exists()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Load Error");
                alert.setContentText("The file no longer exists. Please choose another save location");
                alert.showAndWait();
                saveAs = true;
            }
        }

        if (saveAs || workspace.lastSaveLocation == null || workspace.lastSaveLocation.equals("")) {
            // Prime a FileChooser
            FileChooser fileChooser = new FileChooser();
            if (Global.lastSaveLocation != null && !Global.lastSaveLocation.equals("")) {
                //System.out.println("last save dir: " + Global.lastSaveLocation);
                fileChooser.setInitialDirectory(new File(Global.lastSaveLocation));
            }
            fileChooser.setInitialFileName(workspace.shortName);
            fileChooser.setTitle("Save Workspace");
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Workspace Files", "*.proj")
            );

            // Open the FileChooser to choose the save location of the workspace
            File selectedFile;
            try {
                selectedFile = fileChooser.showSaveDialog(new Stage());
            }
            catch (IllegalArgumentException e) {
                // The file directory is invalid, try again with 'root'
                //System.out.println("Bad directory");
                fileChooser.setInitialDirectory(new File("/"));
                selectedFile = fileChooser.showSaveDialog(new Stage());
            }

            if (selectedFile == null) {
                return SaveLoadResult.FILENOTFOUND;  // Most likely user cancellation
            }
            workspace.lastSaveLocation = selectedFile.toString();

            // Ensure the extension is .proj (Linux issue)
            if (!workspace.lastSaveLocation.endsWith(".proj")) {
                workspace.lastSaveLocation = workspace.lastSaveLocation + ".proj";
            }
        }

        /* GSON SERIALIZATION */
        try (Writer writer = new FileWriter(workspace.lastSaveLocation)) {
            gson.toJson(workspace, writer);
            writer.close();

            String json = gson.toJson(workspace);
            //System.out.println(json);
            RevertManager.updateRevertState(json);

            Global.setCurrentWorkspaceUnchanged();
            Global.lastSaveLocation = Paths.get(workspace.lastSaveLocation).getParent().toString();

            //TESTING
            //System.out.println("WORKSPACE LOCATION: " + workspace.lastSaveLocation);
            //System.out.println("GLOBAL LOCATION: " + Global.lastSaveLocation);


            App.refreshMainScene();
            Global.commandManager.trackSave();
            App.mainPane.refreshStatusBar("Saved the Workspace "  + Global.currentWorkspace.getShortName());

            return SaveLoadResult.SUCCESS;
        }
        catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Saving");
            alert.setContentText("An error occurred while trying to save the file");

            // Create expandable Exception.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);

            alert.showAndWait();
            return SaveLoadResult.IOEXCEPTION;
        }
    }

    /**
     * Loads a workspace specified by the user into Global.currentWorkspace.
     *
     * @return The corresponding SaveLoadResult status of the process
     */
    public static SaveLoadResult loadWorkspace() {
        // Prime a FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Workspace");
        if (Global.lastSaveLocation != null && !Global.lastSaveLocation.equals("")) {
            //System.out.println("last save dir: " + Global.lastSaveLocation);
            fileChooser.setInitialDirectory(new File(Global.lastSaveLocation));
        }
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Workspace Files", "*.proj")
        );

        // Open the FileChooser and try saving the Workspace as the user specifies
        File selectedFile;
        try {
            selectedFile = fileChooser.showOpenDialog(new Stage());
        }
        catch (IllegalArgumentException e) {
            // The file directory is invalid, try again with 'root'
            System.out.println("Bad directory");
            fileChooser.setInitialDirectory(new File("/"));
            selectedFile = fileChooser.showOpenDialog(new Stage());
        }

        if (selectedFile != null) {
            /* GSON DESERIALIZATION */
            try (Reader reader = new FileReader(selectedFile)) {
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    json.append(line);
                }
                //System.out.println(json);

                Global.currentWorkspace = gson.fromJson(json.toString(), Workspace.class);

                RevertManager.updateRevertState(json.toString());

                reader.close();
            }
            catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("File not found");
                alert.setContentText("The specified file could not be found.");
                alert.showAndWait();

                return SaveLoadResult.FILENOTFOUND;
            }
            catch (JsonSyntaxException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("File outdated");
                alert.setContentText("The specified file was created with a deprecated version of Sahara.\n"
                        + "The file cannot be loaded.");
                alert.showAndWait();
                return SaveLoadResult.FILENOTFOUND;
            }
            catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error Loading");
                alert.setContentText("An error occurred while trying to load the file.");

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String exceptionText = sw.toString();

                Label label = new Label("The exception stacktrace was:");

                TextArea textArea = new TextArea(exceptionText);
                textArea.setEditable(false);
                textArea.setWrapText(true);

                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);

                GridPane expContent = new GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                expContent.add(label, 0, 0);
                expContent.add(textArea, 0, 1);

                alert.getDialogPane().setExpandableContent(expContent);
                alert.showAndWait();

                return SaveLoadResult.IOEXCEPTION;
            }

            Global.currentWorkspace.lastSaveLocation = selectedFile.toString();
            Global.lastSaveLocation = Paths.get(Global.currentWorkspace.lastSaveLocation)
                    .getParent().toString();

            Workspace.postDeserialization(Global.currentWorkspace);

            App.refreshMainScene();

            Global.commandManager.clear();
            Global.commandManager.trackSave();
            App.mainPane.refreshStatusBar("Loaded the Workspace " + Global.currentWorkspace.getShortName());

            App.mainPane.selectItem(Global.currentWorkspace);

            TrackedTabPane.clearHistory();

            return SaveLoadResult.SUCCESS;
        }
        else {
            return SaveLoadResult.FILENOTFOUND;  // Was null, probably cancelled action?
        }
    }


    /**
     * Collects all of the stories from each of the projects inside of the workspace
     * @return A collection of all stories inside the workspace
     */
    public Collection<Story> getAllStories() {
        Set<Story> stories = new HashSet<>();
        for (Project project : getProjects()) {
            stories.addAll(project.getAllStories());
        }
        return stories;
    }


    /**
     * Perform pre-serialization steps
     * 1) Transform ObservableLists into ArrayLists for serialization.
     *
     * @param workspace The workspace for intended serialization
     * @return A serializable version of the given workspace
     */
    public static Workspace prepSerialization(Workspace workspace) {

        workspace.serializablePeople.clear();
        for (Person item : workspace.people) {
            item.prepSerialization();
            workspace.serializablePeople.add(item);
        }

        workspace.serializableProjects.clear();
        for (Project item : workspace.projects) {
            item.prepSerialization();
            workspace.serializableProjects.add(item);
        }

        workspace.serializableRoadMaps.clear();
        for (RoadMap item : workspace.roadMaps) {
            item.prepSerialization();
            workspace.serializableRoadMaps.add(item);
        }

        workspace.serializableSkills.clear();
        for (Skill item : workspace.skills) {
            workspace.serializableSkills.add(item);
        }

        workspace.serializableTeams.clear();
        for (Team item : workspace.teams) {
            item.prepSerialization();
            workspace.serializableTeams.add(item);
        }

        workspace.serializableRoles.clear();
        for (Role item : workspace.roles) {
            item.prepSerialization();
            workspace.serializableRoles.add(item);
        }

        workspace.serializableGlobalTags.clear();
        for (Tag item : workspace.globalTags) {
            workspace.serializableGlobalTags.add(item);
        }

        workspace.prepTagSerialization();

        return workspace;
    }

    /**
     * Perform post-deserialization steps (performs on Global.currentWorkspace for now).
     * 1) Transform ArrayLists back into ObservableLists
     * 2) Add listeners back to the workspace (and recursively project..etc.)
     *
     * @param workspace The workspace to prepare
     */
    public static void postDeserialization(Workspace workspace) {
        workspace.people = observableArrayList();
        for (Person item : workspace.serializablePeople) {
            item.postDeserialization();
            workspace.people.add(item);
        }

        for (RoadMap item : workspace.serializableRoadMaps) {
            item.postDeserialization();
            workspace.roadMaps.add(item);
        }


        for (Project item : workspace.serializableProjects) {
            item.postDeserialization();
            workspace.projects.add(item);
        }

        workspace.skills = observableArrayList();
        for (Skill item : workspace.serializableSkills) {
            workspace.skills.add(item);
        }

        workspace.teams = observableArrayList();
        for (Team item : workspace.serializableTeams) {
            item.postDeserialization();
            workspace.teams.add(item);
        }

        workspace.roles = observableArrayList();
        for (Role item : workspace.serializableRoles) {
            item.postDeserialization();
            workspace.roles.add(item);
        }

        workspace.globalTags = observableArrayList();
        for (Tag item : workspace.serializableGlobalTags) {
            workspace.globalTags.add(item);
        }

        workspace.postTagDeserialization();

        // Unset saved changes flag, we just opened the workspace.
        workspace.hasUnsavedChanges = false;

        // Adds listeners to the workspace (mainly for sorting lists)
        workspace.addListeners();
        for (Project proj : workspace.getProjects()) {
            proj.addListeners();
        }

        HierarchyTracker.expandWorkspace();

        SaharaItem.refreshIDs();
    }


    // <editor-fold defaultstate="collapsed" desc="Getters">

    /**
     * Updates the revert state for the workspace
     */
    private void updateDefaultRevert() {
        prepSerialization(this);
        String json = gson.toJson(this);
        RevertManager.updateRevertState(json);
    }

    /**
     * Adds the default elements, such as the product owner and scrum master skills and roles, as
     * well as the unassigned team, to the project.
     */
    private void createDefaultElements() {
        Team unassignedTeam = Team.createUnassignedTeam();
        this.addWithoutUndo(unassignedTeam);

        Skill productOwnerSkill = new Skill("Product Owner",
                "Knows how to work as a Teams Product Owner");
        this.getSkills().add(productOwnerSkill);

        ObservableList<Skill> poSkillList = observableArrayList();
        poSkillList.add(productOwnerSkill);

        Skill scrumMasterSkill = new Skill("Scrum Master", "Can be Scrum Master for a Team");
        this.getSkills().add(scrumMasterSkill);

        ObservableList<Skill> smSkillList = observableArrayList();
        smSkillList.add(scrumMasterSkill);

        Role scrumMaster = new Role("Scrum Master", Role.RoleType.SCRUM_MASTER,
                "The Scrum Master for a Team", smSkillList);
        this.getRoles().add(scrumMaster);

        Role productOwner = new Role(
                "Product Owner", Role.RoleType.PRODUCT_OWNER, "The Product Owner for a Team",
                poSkillList);
        this.getRoles().add(productOwner);

        Role developmentTeamMember = new Role(
                "Developer", Role.RoleType.DEVELOPMENT_TEAM_MEMBER,
                "A member of the Dev Team");
        this.getRoles().add(developmentTeamMember);
    }

    /**
     * Adds listeners to the workspace's property lists, primarily for sorting.
     */
    public void addListeners() {
        people.addListener((ListChangeListener<Person>) change ->
            {
                if (change.next() && !change.wasPermutated()) {
                    Collections.sort(people);
                }
            });

        teams.addListener((ListChangeListener<Team>) change ->
            {
                if (change.next() && !change.wasPermutated()) {
                    Collections.sort(teams);
                }
            });

        projects.addListener((ListChangeListener<Project>) change ->
            {
                if (change.next() && !change.wasPermutated()) {
                    Collections.sort(projects);
                }
            });

        skills.addListener((ListChangeListener<Skill>) change ->
            {
                if (change.next() && !change.wasPermutated()) {
                    Collections.sort(skills);
                }
            });

        getAllTags().addListener((ListChangeListener<Tag>) change ->
        {
            if (change.next() && !change.wasPermutated()) {
                Collections.sort(getAllTags());
            }
        });
    }

    /**
     * Gets if the workspace has unsaved changes.
     *
     * @return true or false depending if the workspace has unsaved changes
     */
    public boolean getHasUnsavedChanges() {
        return hasUnsavedChanges;
    }

    /**
     * Gets the workspace's short name.
     *
     * @return The short name of the workspace
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * Counts the amount of people in the workspace
     * @return number of people in the workspace
     */
    public Integer getNumPeople() {
        int num = 0;
        for (Person person : this.getPeople()) {
            num += 1;
        }
        return num;
    }

    /**
     * Counts the amount of teams in the workspace
     * @return number of teams in the workspace
     */
    public Integer getNumTeams() {
        int num = 0;
        for (Team team : this.getTeams()) {
            num += 1;
        }
        return num - 1;
    }

    /**
     * Counts the amount of teams in the workspace
     * @return number of teams in the workspace
     */
    public Integer getNumProjects() {
        int num = 0;
        for (Project project : this.getProjects()) {
            num += 1;
        }
        return num;
    }

    /**
     * Counts the amount of teams in the workspace
     * @return number of teams in the workspace
     */
    public Integer getNumSprints() {
        int num = 0;
        for (Project project : this.getProjects()) {
            for (Sprint sprint : project.getSprints()) {
                num += 1;
            }
        }
        return num;
    }

    /**
     * Counts the amount of teams in the workspace
     * @return number of teams in the workspace
     */
    public Integer getNumStories() {
        int num = 0;
        for (Project project : this.getProjects()) {
            for (Story story : project.getAllStories()) {
                num += 1;
            }
        }
        return num;
    }

    /**
     * Counts the amount of teams in the workspace
     * @return number of teams in the workspace
     */
    public Integer getNumTasks() {
        int num = 0;
        for (Project project : this.getProjects()) {
            for (Story story : project.getAllStories()) {
                for (Task task : story.getTasks()) {
                    num += 1;
                }
            }
        }
        return num;
    }

    /**
     * Counts the amount of teams in the workspace
     * @return number of teams in the workspace
     */
    public Integer getNumLogs() {
        int num = 0;
        for (Project project : this.getProjects()) {
            for (Log log : project.getLogs()) {
                num += 1;
            }
        }
        return num;
    }

    /**
     * Sets the workspace's short name.
     *
     * @param shortName The new short name for the workspace
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the workspace's long name.
     *
     * @return The long name of the workspace
     */
    public String getLongName() {
        return this.longName;
    }

    /**
     * Sets the workspace's long name.
     *
     * @param longName The new long name for the workspace
     */
    public void setLongName(String longName) {
        this.longName = longName;
    }

    /**
     * Gets the workspace's description.
     *
     * @return The description of the workspace
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the workspaces' estimation scale dictionary.
     *
     * @return The dictionary of estimation scales for the workspace.
     */
    public EstimationScalesDictionary getEstimationScales() {
        return this.estimationScales;
    }

    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Setters">

    /**
     * Sets the workspace's description.
     *
     * @param description The new description for the workspace
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the workspace's list of projects.
     *
     * @return The projects associated with the workspace
     */
    public ObservableList<Project> getProjects() {
        return this.projects;
    }

    /**
     * Gets the workspace's list of globalTags.
     * @return The tag associated with the workspace.
     */
    public ObservableList<Tag> getAllTags() {
        return globalTags;
    }



    /**
     * Gets the workspace's list of Persons.
     *
     * @return The people associated with the workspace
     */
    public ObservableList<Person> getPeople() {
        return this.people;
    }

    /**
     * Gets the workspace's list of Skills.
     *
     * @return The skills associated with a workspace
     */
    public ObservableList<Skill> getSkills() {
        return this.skills;
    }

    /**
     * Gets the workspace's list of Teams.
     *
     * @return The teams associated with a workspace
     */
    public ObservableList<Team> getTeams() {
        return this.teams;
    }

    /**
     * Gets the workspace's list of teams, minus the unassigned team.
     *
     * @return The teams associated with a workspace minus the unassigned teams
     */
    public ObservableList<Team> getTeamsWithoutUnassigned() {
        ObservableList<Team> teamList = observableArrayList();
        teamList = this.teams;
        teamList.remove(Global.getUnassignedTeam());
        return  teamList;
    }

    //</editor-fold>

    /**
     * Gets the workspace's list of Roles
     *
     * @return the Roles associated with a workspace
     */
    public ObservableList<Role> getRoles() {
        return this.roles;
    }
    
    /**
     * Gets the workspace's list of RoadMaps
     *
     * @return the RoadMaps associated with a workspace
     */
    public ObservableList<RoadMap> getRoadMaps() {
        return this.roadMaps;
    }

    /**
     * Marks the workspace as not having unsaved changes.
     */
    public void setUnchanged() {
        this.hasUnsavedChanges = false;
    }

    /**
     * Marks the workspace as having unsaved changes.
     */
    public void setChanged() {
        this.hasUnsavedChanges = true;
    }

    /**
     * Adds a Person to the Workspace's list of Persons.
     *
     * @param person The person to add
     */
    public void add(Person person) {
        Command command = new AddPersonCommand(person);
        Global.commandManager.executeCommand(command);
    }

    /**
     * Adds a Tag to the workspace's list of tags.
     *
     * @param tag The tag to add
     */
    public void add(Tag tag) {
        Command command = new AddTagCommand(tag);
        Global.commandManager.executeCommand(command);
    }

    /**
     * Adds a Skill to the Workspace's list of Skills.
     *
     * @param skill The skill to add
     */
    public void add(Skill skill) {
        if (skill.toString().equals("Product Owner") || skill.toString().equals("Scrum Master")) {
            this.skills.add(skill);
            return;
        }

        Command addSkill = new AddSkillCommand(skill);
        Global.commandManager.executeCommand(addSkill);
    }

    /**
     * Adds a Team to the Workspace's list of Teams.
     *
     * @param team The team to add
     */
    public void add(Team team) {
        Command command = new AddTeamCommand(team);
        Global.commandManager.executeCommand(command);
    }

    /**
     * Adds a Team to the Workspace's list of Teams without an undoable command
     *
     * @param team The team to add
     */
    public void addWithoutUndo(Team team) {
        this.teams.add(team);
    }

    /**
     * Adds the given project to the workspace
     *
     * @param proj The project to add to the workspace
     */
    public void add(Project proj) {
        Command command = new AddProjectCommand(proj);
        Global.commandManager.executeCommand(command);
    }
    
    /**
     * Adds the given RoadMap to the workspace
     *
     * @param roadMap The roadMap to add to the workspace
     */
    public void add(RoadMap roadMap) {
        Command command = new AddRoadMapCommand(roadMap);
        Global.commandManager.executeCommand(command);
    }


    /**
     * Adds a Role to the Workspace's list of Roles.
     *
     * @param role The role to add
     */
    public void add(Role role) {
        if (role.getType() == Role.RoleType.SCRUM_MASTER
                || role.getType() == Role.RoleType.PRODUCT_OWNER
                || role.getType() == Role.RoleType.DEVELOPMENT_TEAM_MEMBER) {
            this.roles.add(role);
            return;
        }

        this.roles.add(role);
    }

    /**
     * Removes a Role from the Workspace's list of Roles.
     *
     * @param role The role to remove
     */
    public void remove(Role role) {
        this.roles.remove(role);
    }

    /**
     * Gets a list of categories of the workspace based on the workspace's lists.
     *
     * @return A list of categories of the workspace
     */
    public ObservableList<SaharaItem> getCategories() {
        // Prime the list
        ObservableList<SaharaItem> root = observableArrayList();

        // Add the categories
        root.addAll(projectCategory, roadMapCategory, teamsCategory, peopleCategory, skillCategory, rolesCategory);

        return root;
    }


    /**
     * Method for creating an XML element for the Workspace within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element workSpaceElement = ReportGenerator.doc.createElement("workspace");

        //WorkSpace Elements
        Element workSpaceID = ReportGenerator.doc.createElement("ID");
        workSpaceID.appendChild(ReportGenerator.doc.createTextNode(String.valueOf(id)));
        workSpaceElement.appendChild(workSpaceID);

        Element workSpaceShortName = ReportGenerator.doc.createElement("identifier");
        workSpaceShortName.appendChild(ReportGenerator.doc.createTextNode(this.getShortName()));
        workSpaceElement.appendChild(workSpaceShortName);

        Element workSpaceLongName = ReportGenerator.doc.createElement("long-name");
        workSpaceLongName.appendChild(ReportGenerator.doc.createTextNode(this.getLongName()));
        workSpaceElement.appendChild(workSpaceLongName);

        Element workSpaceDescription = ReportGenerator.doc.createElement("description");
        workSpaceDescription.appendChild(ReportGenerator.doc.createTextNode(this.getDescription()));
        workSpaceElement.appendChild(workSpaceDescription);
        ReportGenerator.generatedItems.remove(0);

        for (SaharaItem item : this.getChildren()) {
            if (ReportGenerator.generatedItems.contains(item)) {
                Element xmlElement = item.generateXML();
                if (xmlElement != null) {
                    workSpaceElement.appendChild(xmlElement);
                }
                ReportGenerator.generatedItems.remove(item);
            }
        }

        Element workspaceTagElement = ReportGenerator.doc.createElement("tags");
        for (Tag tag : this.getTags()) {
            Element tagElement = tag.generateXML();
            workspaceTagElement.appendChild(tagElement);
        }
        workSpaceElement.appendChild(workspaceTagElement);

        return workSpaceElement;
    }

    /**
     * An overridden version for the String representation of a Workspace.
     *
     * @return The short name of the Workspace
     */
    @Override
    public String toString() {
        //return getClass().getName() + "@" + Integer.toHexString(hashCode());
        return this.shortName;
    }

    /**
     * Gets the children (categories) of the workspace.
     *
     * @return the children (categories) of the workspace
     */
    @Override
    public ObservableList<SaharaItem> getChildren() {
        return getCategories();
    }

    /**
     * Creates a Workspace edit command and executes it with the Global Command Manager, updating
     * the workspace with the new parameter values.
     *
     * @param newShortName      The new short name
     * @param newLongName       The new long name
     * @param newDescription    The new description
     * @param newTags           The new tags of the workspace
     */
    public void edit(String newShortName, String newLongName, String newDescription, ArrayList<Tag> newTags) {
        Command wsedit = new WorkspaceEditCommand(this, newShortName, newLongName, newDescription, newTags);
        Global.commandManager.executeCommand(wsedit);
    }


    /**
     * Enumerable save and load statuses.
     */
    public enum SaveLoadResult {
        SUCCESS,
        NULLWORKSPACE,
        IOEXCEPTION,
        NOFILESELECTED,     // Cancelled in save
        FILENOTFOUND        // File doesn't exist when opening
    }

    /**
     * A command class that allows the executing and undoing of workspace edits
     */
    private class WorkspaceEditCommand implements Command {

        private Workspace ws;
        private String shortName;
        private String longName;
        private String description;
        private Set<Tag> workspaceTags = new HashSet<>();
        private Set<Tag> globalTags = new HashSet<>();

        private String oldShortName;
        private String oldLongName;
        private String oldDescription;
        private Set<Tag> oldWorkspaceTags = new HashSet<>();
        private Set<Tag> oldGlobalTags = new HashSet<>();

        
        /**
         * Constructor for the Workspace editing command
         * @param ws The workspace to be edited
         * @param newShortName The new short name for the workspace
         * @param newLongName The new long name for the workspace
         * @param newDescription The new description for the workspace
         * @param newTags The new tags of the workspace
         */
        private WorkspaceEditCommand(Workspace ws, String newShortName, String newLongName,
                                     String newDescription, ArrayList<Tag> newTags) {


            this.ws = ws;

            if (newTags == null) {
                newTags = new ArrayList<>();
            }

            this.shortName = newShortName;
            this.longName = newLongName;
            this.description = newDescription;
            this.workspaceTags.addAll(newTags);
            this.globalTags.addAll(newTags);
            this.globalTags.addAll(Global.currentWorkspace.getAllTags());

            this.oldShortName = ws.shortName;
            this.oldLongName = ws.longName;
            this.oldDescription = ws.description;
            this.oldWorkspaceTags.addAll(ws.getTags());
            this.oldGlobalTags.addAll(Global.currentWorkspace.getAllTags());
        }

        /**
         * Executes/Redoes the changes of the workspace edit
         */
        public void execute() {
            ws.shortName = shortName;
            ws.longName = longName;
            ws.description = description;

            //Add any created tags to the global collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(globalTags);

            //Add the tags the workspace has to their list of tags
            ws.getTags().clear();
            ws.getTags().addAll(workspaceTags);
            App.refreshWindowTitle();  // The project name may have changed
        }

        /**
         * Undoes the changes of the workspace edit
         */
        public void undo() {
            ws.shortName = oldShortName;
            ws.longName = oldLongName;
            ws.description = oldDescription;
            App.refreshWindowTitle();  // The project name may have changed

            //Adds the old global tags to the overall collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(oldGlobalTags);

            //Changes the list of tags to what they used to be
            ws.getTags().clear();
            ws.getTags().addAll(oldWorkspaceTags);
        }

        /**
         * Gets the String value of the Command for editting a workspace.
         */
        @Override
        public String getString() {
            return "the edit of Workspace \"" + ws.getShortName() + "\"";
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
                if (item.equivalentTo(ws)) {
                    this.ws = (Workspace) item;
                    mapped = true;
                }
            }

            for (Tag tag : workspaceTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        workspaceTags.remove(tag);
                        workspaceTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : oldWorkspaceTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        oldWorkspaceTags.remove(tag);
                        oldWorkspaceTags.add((Tag)item);
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
     * A command class for allowing the addition of Projects to a Workspace
     */
    private class AddProjectCommand implements Command {

        private Project proj;


        /**
         * Constructor for the project addition command
         * @param proj The project to be added
         */
        AddProjectCommand(Project proj) {
            this.proj = proj;
        }

        /**
         * Executes the project addition command
         */
        public void execute() {
            Global.currentWorkspace.getProjects().add(proj);
        }

        /**
         * Undoes the project addition command
         */
        public void undo() {
            Global.currentWorkspace.getProjects().remove(proj);
        }

        /**
         * Gets the String value of the Command for adding a project.
         */
        public String getString() {
            return "the creation of Project \"" + proj.getShortName() + "\"";        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(proj)) {
                    this.proj = (Project) item;
                    mapped = true;
                }
            }
            return mapped;
        }
    }

    /**
     * A command class for allowing the addition of Teams to a Workspace
     */
    private class AddTeamCommand implements Command {
        private Team team;

        /**
         * Constructor for the team addition command
         * @param team The team to be added
         */
        AddTeamCommand(Team team) {
            this.team = team;
        }

        /**
         * Executes the team addition command
         */
        public void execute() {
            Global.currentWorkspace.getTeams().add(team);
        }

        /**
         * Undoes the team addition command
         */
        public void undo() {
            Global.currentWorkspace.getTeams().remove(team);
        }

        /**
         * Gets the String value of the Command for adding teams.
         */
        public String getString() {
            return "the creation of Team \"" + team.getShortName() + "\"";       }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(team)) {
                    this.team = (Team) item;
                    mapped = true;
                }
            }
            return mapped;
        }
    }
    
    /**
     * A command class for allowing the addition of RoadMaps to a Workspace
     */
    private class AddRoadMapCommand implements Command {
        private RoadMap roadMap;

        /**
         * Constructor for the project addition command
         * @param roadMap The project to be added
         */
        AddRoadMapCommand(RoadMap roadMap) {
            this.roadMap = roadMap;
        }

        /**
         * Executes the Roadmap addition command
         */
        public void execute() {
            Global.currentWorkspace.getRoadMaps().add(roadMap);
            Global.currentWorkspace.getRoadMaps().sort(RoadMap.RoadMapPriorityComparator);
        }

        /**
         * Undoes the Roadmap addition command
         */
        public void undo() {
            Global.currentWorkspace.getRoadMaps().remove(roadMap);
            Global.currentWorkspace.getRoadMaps().sort(RoadMap.RoadMapPriorityComparator);
        }

        /**
         * Gets the String value of the Command for adding roadmaps.
         */
        public String getString() {
            return "the creation of Road map \"" + roadMap.getShortName() + "\"";      }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(roadMap)) {
                    this.roadMap = (RoadMap) item;
                    mapped = true;
                }
            }
            return mapped;
        }
    }

    /**
     * A command class for allowing the addition of Tags to a Workspace
     */
    private class AddTagCommand implements Command {
        private Tag tag;

        /**
         * Constructor for the tag add command
         * @param tag The tag to be added
         */
        AddTagCommand(Tag tag) {
            this.tag = tag;
        }

        /**
         * Executes the person addition command
         */
        public void execute() {
            Global.currentWorkspace.getAllTags().add(tag);
        }

        /**
         * Undoes the tag add command
         */
        public void undo() {
            Global.currentWorkspace.getAllTags().remove(tag);
        }

        /**
         * Gets the String value of the Command for adding Tags.
         */
        public String getString() {
            return "the creation of Tag \"" + tag.getName() + "\"";     }

        /**
         * Searches the stateObjects to find an equal model class to map to
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
     * A command class for allowing the addition of People to a Workspace
     */
    private class AddPersonCommand implements Command {
        private Person person;

        /**
         * Constructor for the person addition command
         * @param person The person to be added
         */
        AddPersonCommand(Person person) {
            this.person = person;
        }

        /**
         * Executes the person addition command
         */
        public void execute() {
            Global.currentWorkspace.getPeople().add(person);
            Global.getUnassignedTeam().getPeople().add(person);
        }

        /**
         * Undoes the person addition command
         */
        public void undo() {
            Global.getUnassignedTeam().getPeople().remove(person);
            Global.currentWorkspace.getPeople().remove(person);
            if (Global.getUnassignedTeam() != null) {
                Global.getUnassignedTeam().getPeople().remove(person);
            }
        }

        /**
         * Gets the String value of the Command for adding people.
         */
        public String getString() {
            return "the creation of Person \"" + person.getShortName() + "\"";    }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(person)) {
                    this.person = (Person) item;
                    mapped = true;
                }
            }
            return mapped;
        }
    }

    /**
     * A command class for allowing the addition of Skills to a Workspace
     */
    private class AddSkillCommand implements Command {
        private Skill skill;

        /**
         * Constructor for the skill addition command
         * @param skill The skill to be added
         */
        AddSkillCommand(Skill skill) {
            this.skill = skill;
        }

        /**
         * Executes the skill addition command
         */
        public void execute() {
            Global.currentWorkspace.getSkills().add(skill);
        }

        /**
         * Undoes the skill addition command
         */
        public void undo() {
            Global.currentWorkspace.getSkills().remove(skill);
        }

        /**
         * Gets the String value of the Command for adding skills.
         */
        public String getString() {
            return "the creation of Skill \"" + skill.getShortName() + "\"";
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
