package seng302.group2.workspace.allocation;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.team.Team;

import java.time.LocalDate;
import java.time.Month;

public class AllocationTest {

    /**
     *
     */

    //Set test Allocations
    LocalDate startDate = LocalDate.of(2015, Month.APRIL, 12);
    LocalDate endDate = LocalDate.of(2100, Month.MAY, 30);
    Project proj = new Project();
    Team team = new Team();
    Allocation alloc = new Allocation(proj, team, startDate, endDate);

    /**
     * Tests the Allocation constructor
     */
    @Test
    public void testConstructors() {
        Assert.assertEquals("Untitled Project", alloc.getProject().getShortName());
        Assert.assertEquals("Untitled Team", alloc.getTeam().getShortName());
        Assert.assertEquals(startDate, alloc.getStartDate());
        Assert.assertEquals(endDate, alloc.getEndDate());
    }


    /**
     * Tests that the current allocation is returned
     */
    @Test
    public void testIsCurrentAllocation() throws Exception {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        Allocation alloc2 = new Allocation(proj, team, startDate, tomorrow);
        Assert.assertTrue(alloc2.isCurrentAllocation());
    }


    /**
     * Test that the edits are being performed as expected
     */
    @Test
    public void testEditAllocation() {
        LocalDate testDate = LocalDate.of(2015, Month.MAY, 30);
        alloc.editEndDate(testDate);
        Assert.assertEquals(testDate, alloc.getEndDate());

        Global.commandManager.undo();
        Assert.assertEquals(LocalDate.of(2100, Month.MAY, 30), alloc.getEndDate());

        alloc.editStartDate(LocalDate.of(2015, Month.MARCH, 12));
        Assert.assertEquals(LocalDate.of(2015, Month.MARCH, 12), alloc.getStartDate());

        Global.commandManager.undo();
        Assert.assertEquals(LocalDate.of(2015, Month.APRIL, 12), alloc.getStartDate());

    }


    /**
     * Test the delete method of Allocation
     */
    @Test
    public void testDeleteAllocation() {
        team.add(alloc);
        alloc.delete();
        Assert.assertEquals(0, team.getProjectAllocations().size());
        Assert.assertEquals(0, proj.getTeamAllocations().size());

        Global.commandManager.undo();

        Assert.assertTrue(team.getProjectAllocations().contains(alloc));
        Assert.assertTrue(proj.getTeamAllocations().contains(alloc));
    }

    /**
     * Tests for Allocations' XML generator method.
     */
    @Test
    public void testGenerateXML() {
        new ReportGenerator();

        Element allocationElement = alloc.generateXML();
        Assert.assertEquals("[#text: Untitled Team]", allocationElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: Untitled Project]", allocationElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: 12/04/2015]", allocationElement.getChildNodes().item(3).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: 30/05/2100]", allocationElement.getChildNodes().item(4).getChildNodes().item(0).toString());
        Assert.assertEquals(5, allocationElement.getChildNodes().getLength());
    }

}