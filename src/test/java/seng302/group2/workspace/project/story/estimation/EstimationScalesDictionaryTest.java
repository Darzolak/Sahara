package seng302.group2.workspace.project.story.estimation;

import org.junit.Assert;
import org.junit.Test;
import seng302.group2.Global;
import seng302.group2.workspace.workspace.Workspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EstimationScalesDictionaryTest {
    private EstimationScalesDictionary testDict = EstimationScalesDictionary.getEstimationScale();
    private EstimationScalesDictionary testDict2 = EstimationScalesDictionary.getEstimationScale();
    private Map<String, ArrayList<String>> estimationScaleDict = testDict.getEstimationScaleDict();

    @Test
    /**
     * Tests the construction (uses singleton pattern) of the estimation scale class.
     */
    public void testGetEstimationScale(){
        Assert.assertEquals(testDict, testDict2);
    }

    /**
     * Tests that the estimtaion scale dictionary is correctly returned.
     */
    @Test
    public void testGetEstimationScaleDict() {
        Map<String, ArrayList<String>> testEstimationScaleDict = testDict2.getEstimationScaleDict();

        Map<String, ArrayList<String>> compareEstimationScaleDict = new HashMap<>();

        ArrayList<String> valueList = new ArrayList<>(Arrays.asList("-", "0", "0.5", "1", "2", "3", "5", "8", "13", "20",
                "40", "100", EstimationScalesDictionary.infSymbol, "?"));
        compareEstimationScaleDict.put("Fibonacci", valueList);

        valueList = new ArrayList<>(Arrays.asList("-", "0", "Chihuahua", "Pug", "Bulldog", "Shiba Inu",
                "Border Collie", "Siberian Husky", "German Shepherd", "Great Dane", EstimationScalesDictionary.infSymbol, "?"));
        compareEstimationScaleDict.put("Dog Breeds", valueList);

        valueList = new ArrayList<>(Arrays.asList("-", "0", "2XS", "XS", "S", "M", "L", "XL",
                "2XL", "3XL", "4XL", EstimationScalesDictionary.infSymbol, "?"));
        compareEstimationScaleDict.put("T-Shirt Sizes", valueList);

        valueList = new ArrayList<>(Arrays.asList("-", "0", "A", "B", "C", "D", "E", "F", "G", "H",
                EstimationScalesDictionary.infSymbol, "?"));
        compareEstimationScaleDict.put("Alphabet", valueList);

        Assert.assertEquals(testEstimationScaleDict, compareEstimationScaleDict);

    }

    /**
     * Tests the addition of new scales to the scale dictionary.
     */
    @Test
    public void testCreateScales() {
        ArrayList<String> AlphabetList = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H"));
        testDict.createScales("Alphabet", AlphabetList);


        Assert.assertEquals(estimationScaleDict.get("Alphabet"), AlphabetList);
    }


    @Test
    public void testGetFibEquivalentIndex() {
        Assert.assertEquals(0, Global.currentWorkspace.getEstimationScales().modifiedFibEquivalent(0, 10));
        Assert.assertEquals(7, Global.currentWorkspace.getEstimationScales().modifiedFibEquivalent(5, 10));
        Assert.assertEquals(100, Global.currentWorkspace.getEstimationScales().modifiedFibEquivalent(10, 10));

        Assert.assertEquals(0, Global.currentWorkspace.getEstimationScales().modifiedFibEquivalent(0, 5));
        Assert.assertEquals(4, Global.currentWorkspace.getEstimationScales().modifiedFibEquivalent(2, 5));
        Assert.assertEquals(12, Global.currentWorkspace.getEstimationScales().modifiedFibEquivalent(3, 5));
        Assert.assertEquals(100, Global.currentWorkspace.getEstimationScales().modifiedFibEquivalent(5, 5));
    }


    @Test
    public void testGetFibScaleEquivalentIndex() {
        Global.currentWorkspace = new Workspace();
        ArrayList<String> alphabetList = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H"));

        /*System.out.println("\nindex");
        int i = 0;
        while (i < alphabetList.size()) {
            System.out.println(alphabetList.get(i) + " to " + Global.currentWorkspace.getEstimationScales().getFibScaleEquivalent(alphabetList, i+1));
            i++;
        }*/

        Assert.assertEquals(0, Global.currentWorkspace.getEstimationScales().getFibScaleEquivalent(alphabetList, 0));
        Assert.assertEquals(100, Global.currentWorkspace.getEstimationScales().
                getFibScaleEquivalent(alphabetList, alphabetList.size()));
        Assert.assertEquals(7, Global.currentWorkspace.getEstimationScales().
                getFibScaleEquivalent(alphabetList, 4));


    }


    @Test
    public void testGetFibScaleEquivalentValue() {


        Global.currentWorkspace = new Workspace();
        ArrayList<String> alphabetList = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H"));

        /*System.out.println("\nalpha");
        int i = 0;
        while (i < alphabetList.size()) {
            System.out.println(alphabetList.get(i) + " to " + Global.currentWorkspace.getEstimationScales().getFibScaleEquivalent(alphabetList, alphabetList.get(i)));
            i++;
        }*/

        Assert.assertEquals(1, Global.currentWorkspace.getEstimationScales().getFibScaleEquivalent(alphabetList, "A"));
        Assert.assertEquals(100, Global.currentWorkspace.getEstimationScales().
                getFibScaleEquivalent(alphabetList, "H"));
        Assert.assertEquals(7, Global.currentWorkspace.getEstimationScales().
                getFibScaleEquivalent(alphabetList, "D"));


    }
}