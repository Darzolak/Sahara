package seng302.group2.workspace.roadMap;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.tag.Tag;

import java.util.ArrayList;

/**
 * Created by crw73 on 16/09/15.
 */
public class RoadMapTest {

    /**
     * A simple test for the RoadMap constructors.
     */
    @Test
    public void testRoadMapConstructors() {
        RoadMap RoadMap = new RoadMap();
        Assert.assertEquals("Untitled RoadMap", RoadMap.getShortName());
        Assert.assertEquals("Untitled RoadMap", RoadMap.toString());
        Assert.assertEquals(null, RoadMap.getChildren());

        RoadMap RoadMap2 = new RoadMap("RoadMap 1", 0);
        Assert.assertEquals("RoadMap 1", RoadMap2.getShortName());
        Assert.assertEquals("RoadMap 1", RoadMap2.toString());

    }


    /**
     * Tests the compareTo method of RoadMap to ensure it correctly returns an int representing if a
     * shortName is larger or not.
     */
    @Test
    public void testCompareTo() {
        RoadMap RoadMap1 = new RoadMap("A", 0);
        RoadMap RoadMap2 = new RoadMap("Z", 0);

        Assert.assertTrue(RoadMap1.compareTo(RoadMap2) <= 0);
        Assert.assertTrue(RoadMap2.compareTo(RoadMap1) >= 0);
        Assert.assertTrue(RoadMap1.compareTo(RoadMap1) == 0);
    }

    /**
     * Testing the Add Release command for RoadMaps.
     */
    @Test
    public void testAddReleaseCommand() {
        Project proj = new Project();
        RoadMap roadMap = new RoadMap("Roadmap Item 1", 0);
        Release release = new Release("test release", proj);
        proj.add(release);
        roadMap.add(release);

        Assert.assertTrue(roadMap.getReleases().contains(release));

        Global.commandManager.undo();

        Assert.assertFalse(roadMap.getReleases().contains(release));
    }


    /**
     * Testing the edit command of the RoadMap
     */
    @Test
    public void testEdit() {
        RoadMap RoadMap = new RoadMap("RoadMap 1", 0);
        Tag tag = new Tag("Tag");
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag);

        ArrayList<Release> releases = new ArrayList<>();
        Release release1 = new Release();
        Release release2 = new Release();
        releases.add(release1);
        releases.add(release2);

        RoadMap.edit("Java", 0, releases, tags);

        Assert.assertEquals("Java", RoadMap.getShortName());
        Assert.assertEquals(1, RoadMap.getTags().size());
        Assert.assertEquals(2, RoadMap.getReleases().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", RoadMap.getTags().get(0).getName());

        Global.commandManager.undo();

        Assert.assertEquals("RoadMap 1", RoadMap.getShortName());
        Assert.assertEquals(0, RoadMap.getTags().size());
        Assert.assertEquals(0, RoadMap.getReleases().size());
        Assert.assertEquals(0, Global.currentWorkspace.getAllTags().size());

        Global.commandManager.redo();

        Assert.assertEquals("Java", RoadMap.getShortName());
        Assert.assertEquals(1, RoadMap.getTags().size());
        Assert.assertEquals(2, RoadMap.getReleases().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", RoadMap.getTags().get(0).getName());
    }

    /**
     * Testing the delete Road Map command.
     */
    @Test
    public void testDeleteRoadMap() {
        RoadMap RoadMap = new RoadMap("RoadMap 1", 0);

        RoadMap.deleteRoadMap();
        Assert.assertFalse(Global.currentWorkspace.getRoadMaps().contains(RoadMap));

        Global.commandManager.undo();

        Assert.assertTrue(Global.currentWorkspace.getRoadMaps().contains(RoadMap));
    }

    /**
     * Tests for RoadMaps' XML generator method.
     */
    @Test
    public void testGenerateXML() {
        new ReportGenerator();
        RoadMap RoadMap = new RoadMap("RoadMap 1", 0);

        Element roadMapElement = RoadMap.generateXML();
        Assert.assertEquals(null, roadMapElement);
    }

}
