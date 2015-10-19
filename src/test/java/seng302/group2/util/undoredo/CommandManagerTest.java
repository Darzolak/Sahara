package seng302.group2.util.undoredo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.group2.Global;
import seng302.group2.workspace.SaharaItem;

import java.util.Set;

public class CommandManagerTest {

    private TestObject testObject;
    private Command testCommand;

    /**
     * Create and reset objects for testing
     */
    @Before
    public void setupBefore() {
        Global.commandManager.clear();  // Clear the command stacks
        testObject = new TestObject("original");  // Create a test object for quick use
        testCommand = new TestCommand(testObject, "unoriginal");  // A command that changes testObj
    }

    @Test
    public void testExecuteCommand() {
        Assert.assertEquals(testObject.testString, "original");  // Created, not executed
        Global.commandManager.executeCommand(testCommand);  // Execute
        Assert.assertEquals(testObject.testString, "unoriginal");
    }

    @Test
    public void testIsUndoAvailable() {
        Global.commandManager.clear();
        Assert.assertFalse(Global.commandManager.isUndoAvailable());  // No objects pushed
        Global.commandManager.executeCommand(testCommand);  // Push an object
        Assert.assertTrue(Global.commandManager.isUndoAvailable());
    }

    @Test
    public void testUndo() throws Exception {
        Assert.assertEquals(testObject.testString, "original");  // Created, not executed
        Global.commandManager.executeCommand(testCommand);  // Execute
        Assert.assertEquals(testObject.testString, "unoriginal");
        Global.commandManager.undo();
        Assert.assertEquals(testObject.testString, "original");  // Back to original state
    }

    @Test
    public void testIsRedoAvailable() throws Exception {
        Assert.assertFalse(Global.commandManager.isRedoAvailable());  // No objects undone
        Global.commandManager.executeCommand(testCommand);  // Execute
        Assert.assertFalse(Global.commandManager.isRedoAvailable());  // Still no objects undone
        Global.commandManager.undo();
        Assert.assertTrue(Global.commandManager.isRedoAvailable());
    }

    @Test
    public void testRedo() throws Exception {
        Assert.assertEquals(testObject.testString, "original");  // Created, not executed
        Global.commandManager.executeCommand(testCommand);  // Execute
        Assert.assertEquals(testObject.testString, "unoriginal");  // Executed, not undone
        Global.commandManager.undo();
        Assert.assertEquals(testObject.testString, "original");  // Back to original state
    }

    @Test
    public void testNumUndos() {
        Assert.assertEquals(0, Global.commandManager.numUndos());
        Global.commandManager.executeCommand(testCommand);
        Assert.assertEquals(1, Global.commandManager.numUndos()); //One more for the changes tracker
        Global.commandManager.executeCommand(testCommand);
        Assert.assertEquals(2, Global.commandManager.numUndos());
    }

    @Test
    public void testNumRedos() {
        Assert.assertEquals(0, Global.commandManager.numRedos());
        Global.commandManager.executeCommand(testCommand);
        Global.commandManager.undo();
        Assert.assertEquals(1, Global.commandManager.numRedos());
        Global.commandManager.redo();
        Assert.assertEquals(0, Global.commandManager.numRedos());
    }

    @Test
    public void testClear() {
        Global.commandManager.executeCommand(testCommand);
        Global.commandManager.executeCommand(testCommand);
        Global.commandManager.undo();
        Assert.assertNotEquals(0, Global.commandManager.numUndos());
        Assert.assertNotEquals(0, Global.commandManager.numRedos());
        Global.commandManager.clear();
        Assert.assertEquals(0, Global.commandManager.numUndos());
        Assert.assertEquals(0, Global.commandManager.numRedos());
    }

    @Test
    public void testTrackSave() {
        Global.commandManager.clear();
        Assert.assertTrue(Global.currentWorkspace.getHasUnsavedChanges());
        Global.commandManager.trackSave();
        Assert.assertFalse(Global.currentWorkspace.getHasUnsavedChanges());
        Global.commandManager.executeCommand(testCommand);
        Assert.assertTrue(Global.currentWorkspace.getHasUnsavedChanges());
        Global.commandManager.undo();
        Assert.assertFalse(Global.currentWorkspace.getHasUnsavedChanges());
        Global.commandManager.redo();
        Assert.assertTrue(Global.currentWorkspace.getHasUnsavedChanges());
    }

    private class TestObject {
        public String testString;

        public TestObject(String string) {
            this.testString = string;
        }
    }

    private class TestCommand implements Command {
        TestObject obj;
        String newName;
        String oldName;

        public TestCommand(TestObject obj, String newName) {
            this.obj = obj;
            this.newName = newName;
            this.oldName = obj.testString;
        }

        @Override
        public void execute() {
            obj.testString = newName;
        }

        @Override
        public void undo() {
            obj.testString = oldName;
        }

        @Override
        public String getString() {
            return null;
        }

        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            return true;
        }
    }
}