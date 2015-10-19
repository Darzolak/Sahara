package seng302.group2.workspace.roadMap;

import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.sceneswitch.switchStrategies.workspace.RoadMapInformationSwitchStrategy;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.tag.Tag;

import java.io.Serializable;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * RoadMap Model class.
 * 
 * Created by cvs20 on 11/09/15.
 */
public class RoadMap extends SaharaItem implements Serializable, Comparable<RoadMap> {
    private String shortName;
    private transient ObservableList<Release> releases = observableArrayList();
    private List<Release> serializableReleases = new ArrayList<>();
    private Integer priority = 0;


    /**
     * A comparator that returns the comparison of two story's priorities
     */
    public static Comparator<RoadMap> RoadMapPriorityComparator = (roadMap1, roadMap2) -> {
        return roadMap2.getPriority().compareTo(roadMap1.getPriority());
    };
    

    /**
     * Basic RoadMap constructor
     */
    public RoadMap() {
        super("Untitled RoadMap");
        this.shortName = "Untitled RoadMap";

        setInformationSwitchStrategy(new RoadMapInformationSwitchStrategy());
    }
    
    /**
     * Basic RoadMap constructor
     *
     * @param shortName The short name of the road map
     * @param priority The priority of the road map
     */
    public RoadMap(String shortName, Integer priority) {
        super(shortName);
        this.shortName = shortName;
        this.priority = priority;

        setInformationSwitchStrategy(new RoadMapInformationSwitchStrategy());
    }

    /**
     * Gets a roadmap short name
     *
     * @return The short name of the roadmap
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * Gets a roadmap priority
     *
     * @return The priority of the roadmap
     */
    public Integer getPriority() {
        return this.priority;
    }

    /**
     * Gets the releases of the RoadMap
     *
     * @return list of releases
     */
    public ObservableList<Release> getReleases() {
        return this.releases;
    }
    
    /**
     * Adds a Release to the RoadMaps's list of Releases.
     *
     * @param release The release to add
     */
    public void add(Release release) {
        Command command = new AddReleaseCommand(this, release);
        Global.commandManager.executeCommand(command);
    }

    /**
     * Adds a Release to the RoadMaps's list of Releases.
     *
     * @param release The release to add
     */

    /**
     * Adds a Release to the RoadMaps's list of Releases.
     *
     * @param newRoadMap The new roadmap to add the release to
     * @param oldRoadMap The old roadmap to delete the release from
     * @param release The release to add
     */
    public void addRemove(RoadMap newRoadMap, RoadMap oldRoadMap, Release release) {
        Command command = new AddRemoveReleaseCommand(newRoadMap, oldRoadMap, release);
        Global.commandManager.executeCommand(command);
    }
    
    /**
     * Adds a Release to the RoadMaps's list of Releases.
     *
     * @param shortName The Shortname to edit
     * @param priority  The Priority to edit
     * @param releases  The release to add
     * @param newTags   The new tags of the roadmap
     */
    public void edit(String shortName, Integer priority, Collection<Release> releases, Collection<Tag> newTags) {
        Command command = new RoadMapEditCommand(this, shortName, priority, releases, newTags);
        Global.commandManager.executeCommand(command);
    }
    
    /**
     * Deletes the RoadMap 
     */
    public void deleteRoadMap() {
        Command command = new DeleteRoadMapCommand(this);
        Global.commandManager.executeCommand(command);
    }


    /**
     * Serialization pre-processing.
     */
    public void prepSerialization() {
        serializableReleases.clear();
        for (Release release : releases) {
            this.serializableReleases.add(release);
        }

        prepTagSerialization();
    }

    /**
     * Deserialization post-processing.
     */
    public void postDeserialization() {
        releases.clear();
        for (Release release : serializableReleases) {
            this.releases.add(release);
        }

        postTagDeserialization();
    }
    

    /**
     * Returns the items held by the RoadMap, blank as the RoadMap has no child items.
     * @return a blank hash set
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        Set<SaharaItem> items = new HashSet<>();
        for (Release release : releases) {
            items.addAll(release.getItemsSet());
        }
        items.addAll(releases);
        return items;
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
     * Compares the RoadMap to another RoadMap by their short names
     *
     * @param compareRoadmap The skill to compare to
     * @return The result of the string comparison between the teams' short names
     */
    @Override
    public int compareTo(RoadMap compareRoadmap) {
        String roadMap1ShortName = this.getShortName();
        String roadMap2ShortName = compareRoadmap.getShortName();
        return roadMap1ShortName.compareTo(roadMap2ShortName);
    }

    /**
     * An overridden version for the String representation of a RoadMap
     *
     * @return The short name of the RoadMap
     */
    @Override
    public String toString() {
        return this.shortName;
    }

    /**
     * Method for creating an XML element for the Skill within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        return null;
    }
 
    
    /**
     * A command class for allowing the adding of Releases to RoadMaps.
     */
    private class AddReleaseCommand implements Command {
        private Release release;
        private RoadMap roadMap;
        
        /**
         * Constructor for the release addition command.
         * @param roadMap The roadMap to which the release is being added.
         * @param release The release to be added.
         */
        AddReleaseCommand(RoadMap roadMap, Release release) {
            this.roadMap = roadMap;
            this.release = release;
        }

        /**
         * Executes the release addition command.
         */
        public void execute() {
            roadMap.getReleases().add(release);
            
        }

        /**
         * Undoes the release addition command.
         */
        public void undo() {
            roadMap.getReleases().remove(release);
        }

        /**
         * Gets the String value of the Command for adding releases.
         */
        public String getString() {
            return null;
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
                if (item.equivalentTo(roadMap)) {
                    this.roadMap = (RoadMap) item;
                    mapped = true;
                }
            }
            boolean mapped_release = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(release)) {
                    this.release = (Release) item;
                    mapped_release = true;
                }
            }
            return mapped && mapped_release;
        }
    }

    /**
     * A command class for allowing the editting of RoadMaps.
     */
    private class RoadMapEditCommand implements Command {
        private RoadMap roadMap;
        
        private String shortName;
        private Integer priority;
        private Collection<Release> releases = new HashSet<>();
        private Set<Tag> roadMapTags = new HashSet<>();
        private Set<Tag> globalTags = new HashSet<>();
        
        private String oldShortName;
        private Integer oldPriority;
        private Collection<Release> oldReleases = new HashSet<>();
        private Set<Tag> oldRoadMapTags = new HashSet<>();
        private Set<Tag> oldGlobalTags = new HashSet<>();

        /**
         * Constructor for the release addition command.
         * @param roadMap The roadMap to which the release is being added.
         * @param shortName The new short name
         * @param priority The new priority
         * @param releases The release to be added.
         * @param newTags   The new tags of the roadmap
         */
        RoadMapEditCommand(RoadMap roadMap, String shortName, Integer priority, Collection<Release> releases,
                           Collection<Tag> newTags) {
            this.roadMap = roadMap;

            if (newTags == null) {
                newTags = new ArrayList<>();
            }

            this.releases.addAll(releases);
            this.priority = priority;
            this.shortName = shortName;
            this.roadMapTags.addAll(newTags);
            this.globalTags.addAll(newTags);
            this.globalTags.addAll(Global.currentWorkspace.getAllTags());

            this.oldShortName = roadMap.getShortName();
            this.oldPriority = roadMap.getPriority();
            this.oldReleases.addAll(roadMap.releases);
            this.oldRoadMapTags.addAll(roadMap.getTags());
            this.oldGlobalTags.addAll(Global.currentWorkspace.getAllTags());
        }

        /**
         * Executes the release addition command.
         */
        public void execute() {
            roadMap.shortName = shortName;
            roadMap.priority = priority;

            roadMap.releases.removeAll(oldReleases);
            roadMap.releases.addAll(releases);

            //Add any created tags to the global collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(globalTags);
            //Add the tags a roadmap has to their list of tags
            roadMap.getTags().clear();
            roadMap.getTags().addAll(roadMapTags);


            Global.currentWorkspace.getRoadMaps().sort(RoadMap.RoadMapPriorityComparator);
        }

        /**
         * Undoes the release addition command.
         */
        public void undo() {
            roadMap.shortName = oldShortName;
            roadMap.priority = oldPriority;

            roadMap.releases.removeAll(releases);
            roadMap.releases.addAll(oldReleases);

            //Adds the old global tags to the overall collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(oldGlobalTags);

            //Changes the roadmaps list of tags to what they used to be
            roadMap.getTags().clear();
            roadMap.getTags().addAll(oldRoadMapTags);

            Global.currentWorkspace.getRoadMaps().sort(RoadMap.RoadMapPriorityComparator);
        }

        /**
         * Gets the String value of the Command for editting releases.
         */
        public String getString() {
            return "the edit of Road Map \"" + roadMap.getShortName() + "\"";
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
                if (item.equivalentTo(roadMap)) {
                    this.roadMap = (RoadMap) item;
                    mapped = true;
                }
            }
            for (Release release : releases) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(release)) {
                        releases.remove(release);
                        releases.add((Release)item);
                        break;
                    }
                }
            }

            for (Release release : oldReleases) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(release)) {
                        oldReleases.remove(release);
                        oldReleases.add((Release)item);
                        break;
                    }
                }
            }

            //Tag collections
            for (Tag tag : roadMapTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        roadMapTags.remove(tag);
                        roadMapTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : oldRoadMapTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        oldRoadMapTags.remove(tag);
                        oldRoadMapTags.add((Tag) item);
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
     * A command class for allowing the deletion of RoadMaps from a Workspace.
     */
    private class DeleteRoadMapCommand implements Command {
        private RoadMap roadMap;

        /**
         * Constructor for the roadMap deletion command
         * @param roadMap The roadMap to be deleted
         */
        DeleteRoadMapCommand(RoadMap roadMap) {
            this.roadMap = roadMap;
        }

        /**
         * Executes the roadMap deletion command
         */
        public void execute() {
            Global.currentWorkspace.getRoadMaps().remove(roadMap);
            Global.currentWorkspace.getRoadMaps().sort(RoadMap.RoadMapPriorityComparator);
        }

        /**
         * Undoes the roadMap deletion command
         */
        public void undo() {
            Global.currentWorkspace.getRoadMaps().add(roadMap);
            Global.currentWorkspace.getRoadMaps().sort(RoadMap.RoadMapPriorityComparator);
        }

        /**
         * Gets the String value of the Command for deleting roadmaps.
         */
        public String getString() {
            return "the deletion of Road Map \"" + roadMap.getShortName() + "\"";
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
                if (item.equivalentTo(roadMap)) {
                    this.roadMap = (RoadMap) item;
                    mapped = true;
                }
            }
            return mapped;
        }
    }

    /**
     * A command for the adding of a release from one roadmap to another.
     */
    private class AddRemoveReleaseCommand implements Command {
        private Release release;
        private RoadMap newRoadMap;
        private RoadMap oldRoadMap;

        /**
         * Constructor for the release addition command.
         * @param newRoadMap The roadMap to which the release is being added.
         * @param oldRoadMap The roadMap to which the release is being added.
         * @param release The release to be added.
         */
        AddRemoveReleaseCommand(RoadMap newRoadMap, RoadMap oldRoadMap, Release release) {
            this.newRoadMap = newRoadMap;
            this.oldRoadMap = oldRoadMap;
            this.release = release;
        }

        /**
         * Executes the release addition command.
         */
        public void execute() {
            newRoadMap.getReleases().add(release);
            oldRoadMap.getReleases().remove(release);

        }

        /**
         * Undoes the release addition command.
         */
        public void undo() {
            newRoadMap.getReleases().remove(release);
            oldRoadMap.getReleases().add(release);
        }

        /**
         * Gets the String value of the Command for adding releases.
         */
        public String getString() {
            return null;
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_new = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(newRoadMap)) {
                    this.newRoadMap = (RoadMap) item;
                    mapped_new = true;
                }
            }
            boolean mapped_old = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(oldRoadMap)) {
                    this.oldRoadMap = (RoadMap) item;
                    mapped_old = true;
                }
            }

            boolean mapped_release = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(release)) {
                    this.release = (Release) item;
                    mapped_release = true;
                }
            }
            return mapped_new && mapped_old && mapped_release;
        }
    }
}
