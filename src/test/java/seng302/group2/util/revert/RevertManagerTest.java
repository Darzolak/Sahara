package seng302.group2.util.revert;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.group2.Global;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.workspace.Workspace;

/**
 * Revert Manager Test.
 * Created by Jordane on 30/06/2015.
 */
public class RevertManagerTest {

    @BeforeClass
    public static void before() {
        //JavaFxTestApp.initJFX();
    }


    /**
     * Revert test - simple renaming of a workspace
     */
    @Test
    public void testRevertBasic() {

        SaharaItem.refreshIDs();

        Workspace ws = new Workspace();
        Global.currentWorkspace = ws;

        try {
            ws.setShortName("A Name");
            RevertManager.updateRevertState();
            Assert.assertEquals("A Name", Global.currentWorkspace.getShortName());

            ws.setShortName("A New Name");
            Assert.assertEquals("A New Name", Global.currentWorkspace.getShortName());

            RevertManager.revertWorkspace();
            Assert.assertEquals("A Name", Global.currentWorkspace.getShortName());
        }
        catch (IllegalStateException ex) {

        }
    }
}