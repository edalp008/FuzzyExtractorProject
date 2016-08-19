package extraction;

import org.bytedeco.javacpp.opencv_core;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * This is a feature extractor class that uses the PCA approach.
 * Created by Renaud on 2016-08-10.
 */
public class PCAExtractor implements FeatureExtractor {

    private static final int NUM_OF_COMPONENT = 20;
    private int numberOfComponents;
    private int imageSize;
    private Mat extractorMatrix;
    private Mat translationVector;

    /*
        The various constructors.
     */

    public PCAExtractor () {
        setNumberOfComponents( NUM_OF_COMPONENT );
    }

    public PCAExtractor (int numberOfComponents ) {
        setNumberOfComponents( numberOfComponents );
    }

    public PCAExtractor (int numberOfComponents, int imageSize) {
        setNumberOfComponents( numberOfComponents);
        setImageSize( imageSize );
    }

    public PCAExtractor (int numberOfComponents, int imageSize, Mat extractorMatrix, Mat translationVector) {
        setNumberOfComponents( numberOfComponents);
        setImageSize( imageSize );
        setExtractorMatrix( extractorMatrix );
        setTranslationVector( translationVector );
    }

    public PCAExtractor (int numberOfComponents, Mat extractorMatrix, Mat translationVector) {
        setNumberOfComponents( numberOfComponents);
        setExtractorMatrix( extractorMatrix );
        setTranslationVector( translationVector );
        // Add a set for imageSize if needed
    }

    /**
     * Constructor that calls the "trainPCA" method to train the PCA during construction. This constructor uses the
     * default number of components : 20.
     * @param trainingImages
     */
    public PCAExtractor ( ArrayList<ArrayList<Mat>> trainingImages ){
        setNumberOfComponents( NUM_OF_COMPONENT );
        trainPCA(trainingImages);
    }

    /**
     * Constructor that calls the "trainPCA method to train the PCA extractor during construction. The number of
     * component desired is specified in this construction.
     * @param numberOfComponents
     * @param trainingImages
     */
    public PCAExtractor (int numberOfComponents, ArrayList<ArrayList<Mat>> trainingImages) {
        trainPCA(numberOfComponents, trainingImages);
    }

    /*
        Implementing all the setters and getters for some standard variables.
     */

    public void setNumberOfComponents (int numberOfComponents) {
        this.numberOfComponents = numberOfComponents;
    }

    public void setImageSize (int imageSize) {
        this.imageSize = imageSize;
    }

    public void setExtractorMatrix (Mat extractorMatrix) {
        this.extractorMatrix = extractorMatrix;
    }

    public void setTranslationVector (Mat translationVector) {
        this.translationVector = translationVector;
    }

    public int getNumberOfComponents () {
        return numberOfComponents;
    }

    public int getImageSize () {
        return imageSize;
    }

    public Mat getExtractorMatrix () {
        return extractorMatrix;
    }

    public Mat getTranslationVector () {
        return translationVector;
    }


    /**
     * This is a required implementation for a FeatureExtractor class. It takes a matrix of row or column images,
     * and returns the matrix of its projection on the PCA space.
     * @param imageMatrix
     * @return The features of the image, as columns vectors.
     */
    public Mat extractFeatures ( Mat imageMatrix ){

        Mat tempMatrix;
        Mat translationMatrix = new Mat();
        Mat features = new Mat();

        //Getting the matrix as a row matrix of image
        if (imageMatrix.cols() == imageSize) {  //Images are as Rows
            tempMatrix = imageMatrix.clone();
        } else {                                //Images as columns
            tempMatrix = imageMatrix.clone().t();
        }

        //making a matrix with the translation vector in every rows
        for (int i = 0 ; i < tempMatrix.rows() ; i++) {
            translationMatrix.push_back( translationVector );
        }

        //Converting both matrices to the same type
        if (tempMatrix.type() == translationVector.type()) {}
        else if (tempMatrix.type() < translationVector.type()) {
            tempMatrix.convertTo(tempMatrix, translationVector.type());
        } else {
            translationMatrix.convertTo(translationMatrix, tempMatrix.type());
        }

        //Translating
        Core.subtract(tempMatrix, translationMatrix, tempMatrix);

        //Extracting Features
        Core.gemm(extractorMatrix, tempMatrix.t(), 1, new Mat(), 0, features);

        return features;
    }

    /**
     * This method trains the PCA extractor. It saves the eigenvector matrix as the "extractorMatrix" variable
     * and the mean image as the "translationVector". It uses the number of components that is currently defined in the
     * object.
     * @param trainingImages
     */
    public void trainPCA (ArrayList<ArrayList<Mat>> trainingImages) {

        Mat pcaData = Reshaper.reshapeForTraining(trainingImages);

        translationVector = new Mat();
        extractorMatrix = new Mat();

        Core.PCACompute(pcaData, translationVector, extractorMatrix, numberOfComponents);

        setImageSize(translationVector.cols());

    }

    /**
     * This method trains the PCA extractor. It saves the eigenvector matrix as the "extractorMatrix" variable
     * and the mean image as the "translationVector". It redefine the number of components that is currently defined in the
     * object and uses it for the extractor.
     * @param numOfComponents
     * @param trainingImages
     */
    public void trainPCA (int numOfComponents, ArrayList<ArrayList<Mat>> trainingImages) {

        Mat pcaData = Reshaper.reshapeForTraining(trainingImages);
        setNumberOfComponents(numOfComponents);

        translationVector = new Mat();
        extractorMatrix = new Mat();

        Core.PCACompute(pcaData, translationVector, extractorMatrix, numberOfComponents);

        setImageSize(translationVector.cols());

    }

}
