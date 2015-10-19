package seng302.group2.workspace.project.story.acceptanceCriteria;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;
import seng302.group2.Global;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.workspace.Workspace;

/**
 * A series of tests relating to AcceptanceCriteria class
 * Created by swi67 on 31/05/15.
 */
public class AcceptanceCriteriaTest {
    private Story testStory = new Story();
    private AcceptanceCriteria ac = new AcceptanceCriteria("This is a Demo Text", testStory);

    /**
     * Tests the validity of the constructor
     */
    @Test
    public void testConstructor() {
        Assert.assertEquals("This is a Demo Text", ac.getDescription());
        Assert.assertEquals(AcceptanceCriteria.AcState.UNACCEPTED, ac.getState());
        Assert.assertEquals(testStory, ac.getStory());
    }


    /**
     * Tests the undo/redo command for deleting acceptance maintenance
     */
    @Test
    public void testDelete() {
        testStory.add(ac);
        ac.delete();
        Assert.assertTrue(testStory.getAcceptanceCriteria().isEmpty());
        Global.commandManager.undo();
        Assert.assertTrue(testStory.getAcceptanceCriteria().contains(ac));
        Global.commandManager.redo();
        Assert.assertTrue(testStory.getAcceptanceCriteria().isEmpty());
    }

    /**
     * Tests the undo/redo for editing acceptance criteria
     */
    @Test
    public void testEditUndoRedo() {
        ac.edit("Testing edit");
        Assert.assertEquals("Testing edit", ac.getDescription());
        Global.commandManager.undo();
        Assert.assertEquals("This is a Demo Text", ac.getDescription());
        Global.commandManager.redo();
        Assert.assertEquals("Testing edit", ac.getDescription());
    }


    /**
     * Tests edits of the acceptance criteria's state
     */
    @Test
    public void testStateEdit() {
        AcceptanceCriteria ac = new AcceptanceCriteria("", null);
        Assert.assertEquals(AcceptanceCriteria.AcState.UNACCEPTED, ac.getState());
        ac.edit(AcceptanceCriteria.AcState.ACCEPTED);
        Assert.assertEquals(AcceptanceCriteria.AcState.ACCEPTED, ac.getState());
        Global.commandManager.undo();
        Assert.assertEquals(AcceptanceCriteria.AcState.UNACCEPTED, ac.getState());
        Global.commandManager.redo();
        Assert.assertEquals(AcceptanceCriteria.AcState.ACCEPTED, ac.getState());
    }

    @Test
    public void testTagEdit() {
        Global.currentWorkspace = new Workspace();

        AcceptanceCriteria ac = new AcceptanceCriteria("", null);
        Tag tag = new Tag("Tag");
        ObservableList<Tag> newTags = FXCollections.observableArrayList();
        newTags.add(tag);

        ac.editAcTags(newTags);

        Assert.assertEquals(1, ac.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", ac.getTags().get(0).getName());

        Global.commandManager.undo();

        Assert.assertEquals(0, ac.getTags().size());
        Assert.assertEquals(0, Global.currentWorkspace.getAllTags().size());

        Global.commandManager.redo();

        Assert.assertEquals(1, ac.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", ac.getTags().get(0).getName());
    }

}
