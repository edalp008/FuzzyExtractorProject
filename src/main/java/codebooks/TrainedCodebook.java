package codebooks;

import extraction.FeatureExtractor;
import extraction.Reshaper;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class constructs a global codebook build from user data. The users then create their personal codebooks by
 * taking a subset of this global codebook.
 *
 * Created by Renaud on 2016-08-12.
 */
public class TrainedCodebook implements Codebook{

    private int numberOfComponents;
    private double[] smallestDeltas;
    private double[] minArray;
    private double[] maxArray;

    private ArrayList<double[]> globalCodebook;

    private double alpha;
    private double[] alphaArray;
    private boolean useAlphaArray;


    public TrainedCodebook (int numberOfComponents, double alpha1,
                            ArrayList<ArrayList<Mat>> trainingData, FeatureExtractor extractor) {
        setNumberOfComponents( numberOfComponents );
        trainCodebook(trainingData, extractor);
        useNewAlpha(alpha1);
    }

    public TrainedCodebook (int numberOfComponents, double[] alphaArray1,
                            ArrayList<ArrayList<Mat>> trainingData, FeatureExtractor extractor) {
        setNumberOfComponents( numberOfComponents );
        trainCodebook(trainingData, extractor);
        useNewAlphaArray(alphaArray1);
    }

    public TrainedCodebook (int numberOfComponents, double alpha1, double[] smallestDeltas1,
                            double[] minArray1, double[] maxArray1) {
        setNumberOfComponents( numberOfComponents );
        smallestDeltas = smallestDeltas1;
        minArray = minArray1;
        maxArray = maxArray1;
        useNewAlpha(alpha1);
    }

    public TrainedCodebook (int numberOfComponents, double[] alphaArray1, double[] smallestDeltas1,
                            double[] minArray1, double[] maxArray1) {
        setNumberOfComponents( numberOfComponents );
        smallestDeltas = smallestDeltas1;
        minArray = minArray1;
        maxArray = maxArray1;
        useNewAlphaArray(alphaArray1);
    }

    /* Setters ... */
    private void setNumberOfComponents (int numberOfComponents){
        this.numberOfComponents = numberOfComponents;
    }

    /* ... and getters. */
    public int getNumberOfComponents() {
        return numberOfComponents;
    }

    public ArrayList<double[]> getGlobalCodebook() {
        return globalCodebook;
    }

    /* Other Methods */

    /* Training the values for the codebook, if needed */

    private void trainCodebook ( ArrayList<ArrayList<Mat>> trainingData, FeatureExtractor extractor ) {

        double[] deltas = new double[numberOfComponents];
        double[] mins = new double[numberOfComponents];
        double[] maxs = new double[numberOfComponents];
        boolean firstPerson = true;

        int p = 0;

        for (ArrayList<Mat> person: trainingData) {

            Mat features = extractor.extractFeatures(Reshaper.reshapeForEnrollment(person));

            double[] tempRanges = new double[numberOfComponents];
            double[] tempMins = new double[numberOfComponents];
            double[] tempMaxs = new double[numberOfComponents];

            for (int i = 0; i < numberOfComponents; i++) {
                Core.MinMaxLocResult minsAndMax = Core.minMaxLoc(features.row(i));
                tempMins[i] = minsAndMax.minVal;
                tempMaxs[i] = minsAndMax.maxVal;
                tempRanges[i] = (minsAndMax.maxVal - minsAndMax.minVal) / 2;
            }

/*            System.out.println("Getting the mins, max, range from person " + p );
            p++;
            System.out.println(" The persons values are : ");
            System.out.println( "mins : \n " + Arrays.toString(tempMins));
            System.out.println( "maxs : \n " + Arrays.toString(tempMaxs));
            System.out.println( "deltas : \n " + Arrays.toString(tempRanges) + "\n");
*/
            if (firstPerson) { //Only enters on first loop
                deltas = tempRanges.clone();
                mins = tempMins.clone();
                maxs = tempMaxs.clone();
                firstPerson = false;
            } else {
                for (int i = 0; i < numberOfComponents; i++) {
                    if (tempRanges[i] < deltas[i]) {
                        deltas[i] = tempRanges[i];
                    }
                    if (tempMins[i] < mins[i] ){
                        mins[i] = tempMins[i];
                    }
                    if (tempMaxs[i] > maxs[i] ){
                        maxs[i] = tempMaxs[i];
                    }
                }
            }

/*            System.out.println(" The new saved values for the codebook are : ");
            System.out.println( "mins : \n " + Arrays.toString(mins));
            System.out.println( "maxs : \n " + Arrays.toString(maxs));
            System.out.println( "deltas : \n " + Arrays.toString(deltas) + "\n\n\n\n");
*/
        }

        smallestDeltas = deltas;
        minArray = mins;
        maxArray = maxs;
    }

    /* These two methods are provided so that we can change the alpha without retraining a codebook.
     * For testing purposes.
     */
    public void useNewAlpha (double newAlpha) {
        if (newAlpha <= 0){
            throw new IllegalArgumentException("alpha must be bigger than 0");
        } else {
            this.alpha = newAlpha;
            useAlphaArray = false;
        }
        computeGlobalCodebook();
    }

    public void useNewAlphaArray ( double[] newAlphaArray ) {
        if (newAlphaArray.length != numberOfComponents ) {
            String s = "The Array of alpha values must have size " + this.numberOfComponents + ".";
            throw new IllegalArgumentException(s);
        } else {
            this.alphaArray = newAlphaArray;
            useAlphaArray = true;
        }
    }

    /**
     * This method uses the current known min, max and delta values for each components obtained during training to
     * generate a global codebook.
     * For each component, a random positive value is subtracted from the min value before generating the codewords
     * by using steps of  length "delta". The process will stop some number of steps past the max value for this
     * component.
     * The goal is to make it so that the global codebook is wider in values than what the users will get through
     * their feature extraction.
     */
    public void computeGlobalCodebook () {

        ArrayList<double[]> tempCodebook = new ArrayList<double[]>();
        double a = alpha;

        //looping for all components
        for (int i = 0; i < numberOfComponents ; i++ ) {

            //If we are using an alpha array, we get the values here.
            if (useAlphaArray) {
                a = alphaArray[i];
            }

            // r will be between 0 and half the magnitude of the min values.
            // i.e. if minVal is 3000, r will be between 0 and 500.
            int magnitude = (int)Math.log10(Math.abs(minArray[i]));

            //double r = Math.random()*(Math.pow(10, magnitude)/2);
            double r = (Math.pow(10, magnitude))*(0.5 + Math.random());

            // The upper limit will be between .25 and .50 higher than the range between the min and max values
            //double upperLimit = ((Math.random()*0.25)+0.25)*(maxArray[i] - minArray[i]) + maxArray[i];
            double upperLimit = maxArray[i] + (0.5*(maxArray[i] - minArray[i]));

            // Computing the value L of how many steps will be needed to exceed the upper limit
            int k = (int) Math.ceil(((upperLimit - (minArray[i] - r)) / (a*smallestDeltas[i])) ) + 1;

            /*
            System.out.println("At loop " + i + " the value of k is " + k);
            System.out.println("upper limit is : " + upperLimit + " \n r is : " + r + " \n min value is : "
                                                    + minArray[i] + " \n delts is : " + smallestDeltas[i]
                                                    + "\n max value is " + maxArray[i]);
            */

            //Making, then filling the codebook for this component
            double[] bookSection = new double[k];
            bookSection[0] = minArray[i] - r;
            for (int j = 1; j < k ; j++ ) {
                bookSection[j] = bookSection[j-1] + a*smallestDeltas[i];
            }

            tempCodebook.add(bookSection.clone());
        }

        globalCodebook = tempCodebook;

        //System.out.println("Some values saved in the Codebook : ");
        //System.out.println( "mins : \n " + Arrays.toString(minArray));
        //System.out.println( "maxs : \n " + Arrays.toString(maxArray));
        //System.out.println( "deltas : \n " + Arrays.toString(smallestDeltas));

    }

    /**
     * This method returns a user specific codebook.
     *
     * @param deltas The array of ranges for the user.
     * @param mins  The array of mins for the user. Not used in a TrainedArray setting.
     * @param maxs  The array of maxes for the user. Not used in a TrainedArray setting.
     * @return The user's personal codebook.
     */
    public ArrayList<double[]> generateUserCodebook( double[] deltas, double[] mins, double[] maxs ) {

        ArrayList<double[]> userCodebook = new ArrayList<double[]>();
        double a = alpha;

        for (int i = 0 ; i < numberOfComponents ; i++ ){

            if (useAlphaArray) {
                a = alphaArray[i];
            }

            int steps = 2* (int)Math.ceil(deltas[i] / (a*smallestDeltas[i])) + 1;

            //    Maybe we can try to do
            //int steps = (int)Math.ceil( 2 * deltas[i] / smallestDeltas[i]) + 1;
            //    instead. We would get closer to keeping a 2*deltas[i] +1 distance. It might lower the FAR?
            // */

            int subBookSize = (globalCodebook.get(i).length / steps);

            double[] subBook = new double[subBookSize];
            for (int j = 0 ; j < subBookSize ; j++) {
                int k = j*steps;
                subBook[j] = globalCodebook.get(i)[k];
            }

            userCodebook.add(subBook.clone());
        }

        return userCodebook;
    }

    /**
     * This method returns a user specific codebook in an intelligent fashion. Contrary to generateUserCodebook,
     * where we simply take a subBook, in this method we first find the closest codeword to the midPointVector in
     * the global codebook. The user codebook is then chosen such that this particular codeword is in the codebook.
     *
     * @param deltas The array of ranges for the user.
     * @param midPointVector  The array of midpoints for the user.
     * @return The user's personal codebook.     */

    public ArrayList<double[]> generateUserCodebook2( double[] deltas, double[] midPointVector ) {

        ArrayList<double[]> userCodebook = new ArrayList<double[]>();
        double a = alpha;

        for (int i = 0 ; i < numberOfComponents ; i++ ){

            if (useAlphaArray) {
                a = alphaArray[i];
            }

            int steps = 2* (int)Math.ceil(deltas[i] / (a*smallestDeltas[i])) + 1;

            //    Maybe we can try to do
            //int steps = (int)Math.ceil( 2 * deltas[i] / smallestDeltas[i]) + 1;
            //    instead. We would get closer to keeping a 2*deltas[i] +1 distance. It might lower the FAR?
            // */

            int indexOfClosestCodeword;
            double codeDist = globalCodebook.get(i)[1] - globalCodebook.get(i)[0];
            int leftWordIndex = (int) Math.floor((midPointVector[i] - globalCodebook.get(i)[0])/codeDist);
            if ( (midPointVector[i] - globalCodebook.get(i)[leftWordIndex]) < (codeDist / 2) ) {
                indexOfClosestCodeword = leftWordIndex;
            }
            else {
                indexOfClosestCodeword = leftWordIndex + 1;
            }

            int firstCodeword = indexOfClosestCodeword % steps;

            int subBookSize = ((globalCodebook.get(i).length - firstCodeword )/ steps);

            double[] subBook = new double[subBookSize];
            for (int j = 0 ; j < subBookSize ; j++) {
                int k = j*steps + firstCodeword;
                subBook[j] = globalCodebook.get(i)[k];
            }

            userCodebook.add(subBook.clone());
        }

        return userCodebook;
    }
}
