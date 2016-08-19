package extraction;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * This class is to centralize all reshaping methods. Most are built to work with the outputs from our Loader class
 *
 * Created by Renaud on 2016-08-11.
 */
public class Reshaper {

    /**
     * This method takes data loaded with Loader.getAllImageByPerson and reshape all images as row vectors in a single matrix
     * @param dataForTraining
     * @return A matrix where all images are considered as row vectors.
     */
    // Added by Renaud
    public static Mat reshapeForTraining (ArrayList<ArrayList<Mat>> dataForTraining) {

        Mat tempMatrix;
        Mat reshapedData = new Mat();

        for (ArrayList<Mat> person: dataForTraining) {
            for (int i = 0; i < person.size(); i++) {
                tempMatrix = person.get(i).reshape(0, 1).clone();
                reshapedData.push_back(tempMatrix);
            }
        }

        return reshapedData;
    }

    /**
     * This method takes the data of one person that was loaded from the Loader class and reshape them into a matrix,
     * where the images are row vectors.
     * @param dataForEnrollment
     * @return A matrix where the images are seen a row vectors
     */
    public static Mat reshapeForEnrollment (ArrayList<Mat> dataForEnrollment) {

        Mat tempMatrix;
        Mat reshapedData = new Mat();

        for (int i = 0; i < dataForEnrollment.size(); i++) {
            tempMatrix = dataForEnrollment.get(i).reshape(0, 1).clone();
            reshapedData.push_back(tempMatrix);
        }

        return reshapedData;
    }

    /**
     * This method takes data loaded with getAllImageByPerson and reshape all images as column
     * vectors in a single matrix
     * @param dataForTraining
     * @return A matrix where all images are considered as row vectors.
     */
    // Added by Renaud
    public static Mat reshapeForTrainingCol (ArrayList<ArrayList<Mat>> dataForTraining) {

        Mat tempMatrix;
        Mat reshapedData = new Mat();

        for (ArrayList<Mat> person: dataForTraining) {
            for (int i = 0; i < person.size(); i++) {
                tempMatrix = person.get(i).reshape(0, 1).clone();
                reshapedData.push_back(tempMatrix);
            }
        }

        return reshapedData.t();
    }


    /**
     * Reshapes the images of a single person into a matrix, where the images are row vectors where the parameter
     * "vector" is subtracted from each images. Data is automatically converted to the highest type of the parameters
     * acording to the opencv type hierarchy.
     * @param singlePersonImages
     * @param vector
     * @return
     */
    public static Mat reshapeAndTranslate (ArrayList<Mat> singlePersonImages, Mat vector) {

        if (singlePersonImages.size() == 0) {
            throw new IllegalArgumentException("No images are in the list");
        }

        Mat vec;
        Mat reshapedData = new Mat();
        Mat tempMatrix;

        //Checking types for conversion to the highest type
        int imageType = singlePersonImages.get(0).type();
        int vectType = vector.type();
        boolean toVectType = false;
        if (imageType < vectType) toVectType = true;

        //Checking if vector is row or column. Forces a reshape if vector is a Matrix.
        if (vector.rows() != 1) {
            vec = vector.reshape(0, 1);
        } else {
            vec = vector;
        }

        for (int i = 0; i < singlePersonImages.size(); i++) {
            tempMatrix = singlePersonImages.get(i).reshape(0, 1).clone();
            if (toVectType) {
                tempMatrix.convertTo(tempMatrix, vectType);
            } else {
                vec.convertTo(vec, imageType);
            }
            Core.subtract(tempMatrix, vec, tempMatrix);
            reshapedData.push_back(tempMatrix);
        }

        return reshapedData;
    }

    public static Mat reshapeAndTranslateCol (ArrayList<Mat> singlePersonImages, Mat vector) {

        return reshapeAndTranslate( singlePersonImages, vector).t();
    }


}
