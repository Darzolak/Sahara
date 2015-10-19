package seng302.group2.workspace.project.story.estimation;

import java.util.*;

/**
 * A singleton class containing a dictionary mapping the name of a story estimation scale to an
 * array of string representing the estimation values. Contains a method for creating new estimation
 * scales to use alongside the three default scales.
 * Created by drm127 and btm38 on 29/06/15.
 */
public class EstimationScalesDictionary {

    private static EstimationScalesDictionary scales;
    private Map<String, ArrayList<String>> estimationScaleDict = new HashMap<>();

    private static Map<DefaultValues, String> defaultValuesDict = new HashMap<>();

    public enum DefaultValues {
        NONE,
        ZERO,
        INFINITE,
        QUESTION
    }

    public static String infSymbol = "\u221E";


    /**
     * Initialises the default values dictionary for use in all estimation scales
     */
    static {
        defaultValuesDict = new HashMap<>();
        defaultValuesDict.put(DefaultValues.NONE, "-");
        defaultValuesDict.put(DefaultValues.ZERO, "0");
        defaultValuesDict.put(DefaultValues.INFINITE, infSymbol);
        defaultValuesDict.put(DefaultValues.QUESTION, "?");
    }


    /**
     * Looks up the default values dictionary to find the string value for the given default scale enum
     * @param value the value to return the string of
     * @return the string value for the given default scale enum
     */
    public static String getScaleValue(DefaultValues value) {
        return defaultValuesDict.get(value);
    }


    /**
     * Creates the default scales for use in the application
     */
    private void createDefaultScales() {
        ArrayList<String> valueList = new ArrayList<>(Arrays.asList("0.5", "1", "2", "3", "5", "8", "13", "20",
                "40", "100"));
        createScales("Fibonacci", valueList);

        valueList = new ArrayList<>(Arrays.asList("Chihuahua", "Pug", "Bulldog", "Shiba Inu",
                "Border Collie", "Siberian Husky", "German Shepherd", "Great Dane"));
        createScales("Dog Breeds", valueList);

        valueList = new ArrayList<>(Arrays.asList("2XS", "XS", "S", "M", "L", "XL", "2XL", "3XL", "4XL"));
        createScales("T-Shirt Sizes", valueList);
    }


    /**
     * The private constructor for the estimationScalesDictionary class. Is only accessible by itself,
     * to allow conformance to the singleton pattern.
     */
    private EstimationScalesDictionary() {
        createDefaultScales();
    }


    /**
     * The publicly accessible method for addition of the estimation scale dictionary to a workspace.
     *
     * @return The single instance of the estimationScalesDictionary.
     */
    public static EstimationScalesDictionary getEstimationScale() {
        if (scales == null) {
            scales = new EstimationScalesDictionary();
        }
        return scales;
    }

    /**
     * Returns a type of Map%lt;String%gt;%lt;String%gt;, ArrayList%lt;String%gt;
     * which contains all the various estimation scales.
     *
     * @return estimation scale dictionary
     */
    public Map<String, ArrayList<String>> getEstimationScaleDict() {
        return this.estimationScaleDict;
    }

    /**
     * A method for the creation of new estimation scales, and their addition to the dictionary.
     * @param scaleName The name of the new scale.
     * @param scaleValues An arraylist of strings representing the values to be used in the new scale.
     */
    public void createScales(String scaleName, ArrayList<String> scaleValues) {
        scaleValues.add(0, defaultValuesDict.get(DefaultValues.NONE));
        scaleValues.add(1, defaultValuesDict.get(DefaultValues.ZERO));
        scaleValues.add(defaultValuesDict.get(DefaultValues.INFINITE));
        scaleValues.add(defaultValuesDict.get(DefaultValues.QUESTION));

        estimationScaleDict.put(scaleName, scaleValues);
    }


    /**
     * Given a list of scales, and the sequence number of an element inside the sequence (index + 1), returns an
     * approximate fibonacci equivalent of the element
     * @param scale A list of values inside the scale
     * @param seqNo The sequence number of the element to find the equivalent scale for
     * @return An approximate fibonacci equivalent for the element inside the given scale
     */
    public int getFibScaleEquivalent(List<String> scale, int seqNo) {
        if (seqNo == 0) {
            return 0;
        }

        List<String> values = new ArrayList<>();
        values.addAll(scale);
        values.removeAll(defaultValuesDict.values());
        values.add(0, defaultValuesDict.get(DefaultValues.ZERO));
        return modifiedFibEquivalent(seqNo, values.size() - 1);
    }


    /**
     * Given a list of scales, and the sequence number of an element inside the sequence (index + 1), returns an
     * approximate fibonacci equivalent of the element
     * @param scale A list of values inside the scale
     * @param value The value of the element to find the equivalent scale for
     * @return An approximate fibonacci equivalent for the element inside the given scale
     */
    public int getFibScaleEquivalent(List<String> scale, String value) {
        if (value.equals(scale.get(0))) {
            return getFibScaleEquivalent(scale, 1);
        }

        List<String> values = new ArrayList<>();
        values.addAll(scale);
        values.removeAll(defaultValuesDict.values());
        values.add(0, defaultValuesDict.get(DefaultValues.ZERO));

        int i = 0;
        while (i < values.size() && !value.equals(values.get(i))) {
            i++;
        }


        return getFibScaleEquivalent(scale, i);  // Account for added first '0'
    }


    /**
     * Given a number and a total/maximum number in a scale, calculates an approximate fibonacci equivalent for the
     * number, based on the total number
     * @param seqNo The number to find an approximate fibonacci value for
     * @param totalNo The total number of elements in the scale of the given seqNo
     * @return A rounded, approximate fibonacci scaled value for the given seqNo
     */
    int modifiedFibEquivalent(int seqNo, int totalNo) {
        if (seqNo == 0) {
            return 0;
        }

        int fibSeqLength = estimationScaleDict.get("Fibonacci").size() - defaultValuesDict.size() + 1;
        double phi = (1 + Math.sqrt(5)) / 2.0;
        Double approxFib = ((Math.pow(phi, (seqNo / (1.0 * totalNo) * (fibSeqLength * 1.0) ))) / Math.sqrt(5) + 0.5);

        return new Double(approxFib * 100.0 / fib(fibSeqLength)).intValue(); // Normalise and return
    }


    /**
     * Calculates the fibonacci number for the given n
     * @param seqNo The sequence number to calculate the fibonacci value of
     * @return The fibonacci number at element n
     */
    static double fib(double seqNo) {
        if (seqNo < 2) {
            return seqNo;
        }
        else {
            return fib(seqNo - 2) + fib(seqNo - 1);
        }
    }

}
