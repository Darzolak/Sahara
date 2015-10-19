package seng302.group2.workspace.project.story.acceptanceCriteria;

import org.junit.Test;

public class AcEnumStringConverterTest {

    private AcEnumStringConverter converter = new AcEnumStringConverter();

    @Test
    public void testFromString() throws Exception {
        org.junit.Assert.assertEquals(AcceptanceCriteria.AcState.ACCEPTED, converter.fromString("accepted"));
        org.junit.Assert.assertEquals(AcceptanceCriteria.AcState.ACCEPTED, converter.fromString("ACCEPTED"));
        org.junit.Assert.assertEquals(AcceptanceCriteria.AcState.UNACCEPTED, converter.fromString("unaccepted"));
        org.junit.Assert.assertEquals(AcceptanceCriteria.AcState.UNACCEPTED, converter.fromString("UNACCEPTED"));
    }

    @Test
    public void testToString() throws Exception {
        org.junit.Assert.assertEquals("Accepted", converter.toString(AcceptanceCriteria.AcState.ACCEPTED));
        org.junit.Assert.assertEquals("Unaccepted", converter.toString(AcceptanceCriteria.AcState.UNACCEPTED));
    }
}