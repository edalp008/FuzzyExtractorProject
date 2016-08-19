package templates;

import codebooks.Codebook;
import extraction.FeatureExtractor;
import extraction.Reshaper;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is a class that will compute the necessary values to create a secure sketch for a single user, according to
 * Yagis et Al paper on secure sketch.
 * This class also allows a method to try authentication with an input image.
 *
 * This class will require a global codebook to be available to generate the user's personal codebook.
 *
 * Created by Renaud on 2016-08-12.
 */
public class UserTemplate implements FaceTemplate {

    private double[] secureSketch;
    private double[] midPointVector;
    private double[] rangeVector;
    private FeatureExtractor extractor;
    private ArrayList<double[]> userCodebook;
    private int numberOfComponents;

    /*
        The various constructors.
     */
    //  public UserTemplate (FeatureExtractor extractor1) {
    //      setExtractor(extractor1);
    //  }

    public UserTemplate (FeatureExtractor extractor1, ArrayList<Mat> enrollmentData ) {
        setExtractor(extractor1);
        makeTemplate(enrollmentData);
    }

    public UserTemplate (FeatureExtractor extractor1, ArrayList<Mat> enrollmentData, Codebook globalCodebook ) {
        setExtractor(extractor1);
        makeTemplate(enrollmentData);
        computeCodeook(globalCodebook);
        computeSecureSketch();
    }


    /* The setters... */

    private void setExtractor (FeatureExtractor extractor1) {
        this.extractor = extractor1;
        numberOfComponents = extractor1.getNumberOfComponents();
    }

    private void setMidPointVector (double[] vector) {
        midPointVector = vector;
    }

    private void setRangeVector (double[] vector) {
        rangeVector = vector;
    }

    /* ... and getters. */

    public FeatureExtractor getExtractor () {
        return extractor;
    }

    public double[] getMidPointVector () {
        return midPointVector;
    }

    public double[] getRangeVector() {
        return rangeVector;
    }

    public double[] getSecureSketch() {
        return secureSketch;
    }

    public ArrayList<double[]> getUserCodebook() {
        return userCodebook;
    }

    /*
        Methods to compute the various variables and codebook
     */

    /**
     * Takes enrollment data for one person as obtained from the Loader class and computes the Mid-points vector
     * and the range vector.
     * @param enrollmentData
     */

    public void makeTemplate ( ArrayList<Mat> enrollmentData ) {
        int nbOfComponents = extractor.getNumberOfComponents();

        Mat features = extractor.extractFeatures(Reshaper.reshapeForEnrollment(enrollmentData));

        double[] ranges = new double[nbOfComponents];
        double[] midPoints = new double[nbOfComponents];

        for (int i = 0 ; i < nbOfComponents ; i++) {
            Core.MinMaxLocResult minsAndMax = Core.minMaxLoc(features.row(i));
            ranges[i] = (minsAndMax.maxVal - minsAndMax.minVal) /2;
            midPoints[i] = minsAndMax.minVal + ranges[i];
        }

        setRangeVector(ranges);
        setMidPointVector(midPoints);
    }

    /**
     * Takes the global codebook and enrollment data to generate the user specific codebook.
     * @param globalCodebook
     */
    public void computeCodeook(Codebook globalCodebook) {

        double[] mins = new double[numberOfComponents];
        double[] maxs = new double[numberOfComponents];

        for (int i = 0 ; i < numberOfComponents ; i++) {
            mins[i] = midPointVector[i] - rangeVector[i];
            maxs[i] = midPointVector[i] + rangeVector[i];
        }

        //userCodebook = globalCodebook.generateUserCodebook(rangeVector, mins, maxs);

        userCodebook = globalCodebook.generateUserCodebook2(rangeVector, midPointVector);

    }

    /**
     * Takes a feature vector and returns its closest codeword.
     * @param featureVector a feature Vector as a double[]
     * @return The associated codeword
     */

    public double[] getCodeword( double[] featureVector ) {

        double[] codeword = new double[numberOfComponents];

        for (int i = 0 ; i < numberOfComponents ; i++) {
            double[] subBook = userCodebook.get(i);

            if ( featureVector[i] < subBook[0] ) {
                codeword[i] = subBook[0];
            }
            else if ( featureVector[i] > subBook[subBook.length - 1] ) {
                codeword[i] = subBook[subBook.length - 1];
            }
            else {
                double codeDist = subBook[1] - subBook[0];
                int leftWordIndex = (int) Math.floor((featureVector[i] - subBook[0])/codeDist);
                if ( (featureVector[i] - subBook[leftWordIndex]) < (codeDist / 2) ) {
                    codeword[i] = subBook[leftWordIndex];
                }
                else {
                    codeword[i] = subBook[leftWordIndex + 1];
                }
            }
        }

        return codeword;
    }

    /**
     * Get the codeword of an image.
     * @param image A Mat image of correct size
     * @return The codeword associated to the image
     */

    public double[] getCodeword ( Mat image ) {

        Mat matFeature = extractor.extractFeatures( image.reshape(0,1) );

        double[] featureVect = new double[numberOfComponents];

        for (int i = 0 ; i < numberOfComponents ; i++ ) {
            featureVect[i] = matFeature.get(i,0)[0];
        }

        return getCodeword(featureVect);
    }

    /**
     * Computing the secure sketch. Throws an error if the codebook has not been constructed yet.
     */

    public void computeSecureSketch () {
        if (userCodebook == null) {
            throw new IllegalArgumentException("The user codebook is not constructed!");
        }
        else {
            double[] midPointCodeword = getCodeword(midPointVector);
            double[] sketch = new double[numberOfComponents];
            for (int i = 0 ; i < numberOfComponents ; i++ ) {
                sketch[i] = midPointCodeword[i] - midPointVector[i];
            }
            secureSketch = sketch;
        }
    }

    public boolean isSamePerson ( Mat image ) {
        boolean samePerson = false;

        double[] imageCodeword = getCodeword( image.reshape(0,1) );
        double[] rebuiltSecret = new double[numberOfComponents];

        for (int i = 0 ; i < numberOfComponents ; i++ ) {
            rebuiltSecret[i] = imageCodeword[i] - secureSketch[i];
        }

        /*
        System.out.println( "\n Codeword : \n " + Arrays.toString(imageCodeword));
        System.out.println( "midPointCodeword : \n " + Arrays.toString( getCodeword(midPointVector)));

        Mat matFeature = extractor.extractFeatures( image.reshape(0,1) );
        System.out.println(matFeature);
        double[] featureVect = new double[numberOfComponents];
        for (int i = 0 ; i < numberOfComponents ; i++ ) {
            featureVect[i] = matFeature.get(i,0)[0];
        }

        System.out.println( "\n image features : \n " + Arrays.toString(featureVect));
        System.out.println( "midPointvector : \n " + Arrays.toString( midPointVector));
        System.out.println( "ranges : \n " + Arrays.toString( rangeVector ));
        /* */

        return Arrays.equals(rebuiltSecret, midPointVector);

    }
}
