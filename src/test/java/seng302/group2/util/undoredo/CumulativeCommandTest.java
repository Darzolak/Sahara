package seng302.group2.util.undoredo;

import org.junit.Assert;
import org.junit.Test;
import seng302.group2.Global;
import seng302.group2.workspace.SaharaItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A series of tests for cumulative commands
 * Created by Jordane on 2/07/2015.
 */
public class CumulativeCommandTest {

    @Test
    public void testAdd() throws Exception {
        CumulativeCommand command = new CumulativeCommand();
        TestObject t1 = new TestObject("original1");
        TestObject t2 = new TestObject("original2");
        command.add(new TestCommand(t1, "executed1"));
        command.add(new TestCommand(t2, "executed2"));

        Assert.assertEquals(2, command.size());
    }

    @Test
    public void testCollectionConstructor() {
        Global.commandManager.clear();

        TestObject t1 = new TestObject("original1");
        TestObject t2 = new TestObject("original2");
        List<Command> commands = new ArrayList<Command>() {{
            add(new TestCommand(t1, "executed1"));
            add(new TestCommand(t2, "executed2"));
        }};

        CumulativeCommand command = new CumulativeCommand(commands);

        Assert.assertEquals(2, command.size());
    }

    @Test
    public void testUndoRedo() throws Exception {
        Global.commandManager.clear();

        TestObject t1 = new TestObject("original1");
        TestObject t2 = new TestObject("original2");
        List<Command> commands = new ArrayList<Command>() {{
            add(new TestCommand(t1, "executed1"));
            add(new TestCommand(t2, "executed2"));
        }};

        CumulativeCommand command = new CumulativeCommand(commands);

        Assert.assertEquals("original1", t1.testString);
        Assert.assertEquals("original2", t2.testString);

        Global.commandManager.executeCommand(command);

        Assert.assertEquals("executed1", t1.testString);
        Assert.assertEquals("executed2", t2.testString);

        Global.commandManager.undo();

        Assert.assertEquals("original1", t1.testString);
        Assert.assertEquals("original2", t2.testString);

        Global.commandManager.redo();

        Assert.assertEquals("executed1", t1.testString);
        Assert.assertEquals("executed2", t2.testString);
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

        public String getString() {
            return null;
        }

        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            return true;
        }
    }
}