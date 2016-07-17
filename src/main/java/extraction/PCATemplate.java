package extraction;

import com.sun.corba.se.spi.ior.iiop.MaxStreamFormatVersionComponent;
import com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl;
import com.sun.org.apache.xml.internal.serializer.utils.SystemIDResolver;

import org.opencv.core.Mat;
import org.bytedeco.javacpp.opencv_core;

import org.opencv.core.Core;

import java.util.ArrayList;


/**
 * Created by Erica on 16-07-13.
 */
public class   PCATemplate {

    private static final int NUM_OF_COMPONENTS = 20;
    private static final double alpha = .4;
    private Mat featureMatrix;
    private Mat continuousDomain;
    private int numberOfPeopleInTrainingSet;
    private ArrayList[][] codebook;
    //In this constructor, execute the functions to extract the features and make the template
    /*
        @param ArrayList<ArrayList<Mat>> This is a list of people, each entry for a person contains training data for this person

     */
    public PCATemplate(ArrayList<ArrayList<Mat>> trainingImages){
        featureMatrix = new Mat();
        codebook = getTrainingCodeBook(trainingImages);
        numberOfPeopleInTrainingSet = trainingImages.size();

    }


    /*
        This function will create the continuous domain defined as U in the paper, in order to do so, the function will do the following
        1. loop trough all of the individuals and make a feature vector for this user
            1.1 To run the pictures though the PCA function, this will give feature matrix of size(height, width) 20XNumberOhImagesForOnePerson
            1.2 Take the mins, maxs, ranges and midpoint and put them in a vector
        2.  Push this vector in the continuous domain
        3.  Once the continuous domain is completed the next function can accomplish the discritization of the data
        4. Discretize the data
        5. make the codebook

        @param ArrayList<ArrayList<Mat>> this is a list of user, each user has a set of images
     */
    //            for (int z = 0; z < tempMultipliesMatrix.rows(); z++) {
//                for (int b = 0; b < tempMultipliesMatrix.cols(); b++) {
//                    System.out.print(" " + tempMultipliesMatrix.row(z).col(b).dump());
//                }
//                System.out.println("");
//            }

    private ArrayList[][] getTrainingCodeBook(ArrayList<ArrayList<Mat>> trainingUserImages){

        //used for extracting 1 person
        Mat tempImagePixelsVector;
        Mat tempTemplate = new Mat();

        //used to make the space
        //Make the continuous space
        //Things contained in channels, 1->midpoint, 2->max, 3->mins, 4->ranges

        int numberOfComponents = (trainingUserImages.get(0).size()>20) ? 20:trainingUserImages.get(0).size();
        continuousDomain = new Mat(numberOfComponents, trainingUserImages.size(), opencv_core.CV_64FC4);
        int index = 0;

        //lets create the templates for everyone
        for (ArrayList<Mat> person: trainingUserImages) {

            //loop trough all of the images to get a matrix with all of the image's pixels and make a 10304XNumbimages matrix
            //load the new matrix with imgs (they are in greyscale)

            Mat enrollementMatrix = new Mat();
            Mat tempMultipliesMatrix = new Mat();
            Mat tempMatrix;
            for (int i = 0; i < person.size(); i++) {
                tempMatrix = person.get(i).reshape(0, 1).clone();

                enrollementMatrix.push_back(tempMatrix);
            }

            Mat vectors = new Mat();
            Core.PCACompute(enrollementMatrix, new Mat(), vectors, NUM_OF_COMPONENTS);

            Mat transEnrollementMatrix = new Mat(enrollementMatrix.cols(), enrollementMatrix.rows(), vectors.type());
            transEnrollementMatrix = enrollementMatrix.t();

            Mat tempTrans = new Mat(transEnrollementMatrix.rows(), transEnrollementMatrix.cols(), vectors.type());

            transEnrollementMatrix.convertTo(tempTrans, vectors.type());

            Core.gemm(vectors, tempTrans, 1, new Mat(), 0, tempMultipliesMatrix);

            //get the mins and the max for everyone
            Core.MinMaxLocResult minsAndMax;
            double range;
            double midpoint;

            for (int z = 0; z < tempMultipliesMatrix.rows();z++)
            {
                double [] tuple = new double[4];
                minsAndMax = Core.minMaxLoc(tempMultipliesMatrix.row(z));
                range = minsAndMax.maxVal-minsAndMax.minVal;
                midpoint = (minsAndMax.maxVal-minsAndMax.minVal)/2+minsAndMax.minVal;
                tuple[0] = minsAndMax.minVal;tuple[1] = minsAndMax.maxVal;tuple[2] = range;tuple[3] = midpoint;
                continuousDomain.put(z,index, tuple);
            }
            index++;
        }

//        for (int i = 0; i<7;i++){
//            for (int b = 0;b<40;b++){
//                System.out.print(continuousDomain.row(i).col(b).dump()+" | ");
//            }
//            System.out.println("");
//        }


        //now let discretize the matrix

        double componentMins [] = new double [continuousDomain.rows()];
        double componentMaxs [] = new double [continuousDomain.rows()];
        double minRanges[] = new double [continuousDomain.rows()];
        double elementTuple [];
        double discreteRanges [][] = new double [continuousDomain.rows()][continuousDomain.cols()];
        ArrayList<double []> discreteDomain = new ArrayList<double[]>();
        ArrayList<double []> codeBook = new ArrayList<double[]>();

        double R = 0;
        int L = 0;

        for (int i = 0; i<continuousDomain.rows();i++){
            elementTuple = continuousDomain.get(i,0);
            componentMins[i] = elementTuple[0];
            componentMaxs[i] = elementTuple[1];
            minRanges[i] = elementTuple[2];
            for (int b = 0;b<continuousDomain.cols();b++){
                elementTuple = continuousDomain.get(i,b);
                if ( elementTuple[0]<componentMins[i] ){componentMins[i] = elementTuple[0];}
                if ( elementTuple[1]>componentMaxs[i] ){componentMaxs[i] = elementTuple[1];};
                if ( elementTuple[2]<minRanges[i]){minRanges[i] = elementTuple[2];}
            }



            //build the discrete ranges for users and components

            for (int b = 0;b<continuousDomain.cols();b++){
                elementTuple = continuousDomain.get(i,b);

                discreteRanges[i][b] = Math.ceil(elementTuple[2]/(alpha*minRanges[i]));
//                System.out.print(discreteRanges[i][b]+" ");
            }
//            System.out.println(" ");

            R = Math.floor(Math.random()*10);
            L = 0;

            //find an L such that MNj -rj + Lj*Alphaj >= MXj
            while((componentMins[i]-R+L*alpha*minRanges[i])<componentMaxs[i]){
                L++;//System.out.println((componentMins[i]-R+L*minRanges[i])+"\t\t\t"+componentMaxs[i]);
            }

            double dicrete[] = new double[L];
            for(int l = 0; l<L;l++) {
                dicrete[l] = componentMins[i]-R +l*alpha*minRanges[i];
            }

            discreteDomain.add(dicrete);

//            System.out.println(componentMins[i]+" \t\t"+componentMaxs[i]+" \t\t"+minRanges[i]);
        }


        //create the codebook
        //The codebook for a given user is given by the following 2dij + 1 consecutive points in Cj .
        ArrayList<Double> codebook [][] = new ArrayList[continuousDomain.rows()][continuousDomain.cols()];
        ArrayList<Double> tempVec = new ArrayList<Double>();
        for (int i = 0; i<continuousDomain.rows();i++) {
            for (int b = 0; b < continuousDomain.cols(); b++) {
                tempVec = new ArrayList<Double>();

                //get the codewords from the C discrete domain and find out the number of terms that we can get
                int indexTerm = 0;
                while((indexTerm*(int)(2*discreteRanges[i][b]+1))<discreteDomain.get(i).length){
                    tempVec.add(discreteDomain.get(i)[(indexTerm*(int)(2*discreteRanges[i][b])+1)]);
                    indexTerm++;
                }

                codebook[i][b] = tempVec;
            }
        }
        //Print the codebook


//        for (int i = 0; i<continuousDomain.rows();i++) {
//            for (int b = 0; b < continuousDomain.cols(); b++) {
//                System.out.print(codebook[i][b].size());
//                for (Double code:codebook[i][b]
//                     ) {
//                    System.out.print(code+" ");
//                }
//                System.out.print("\t\t\t|");
//            }
//            System.out.println("");
//        }
        return codebook;
    }

    /*
    Testing whether or not the codebook is good!
    @param ArrayList<Mat> list of pictures for one person

    1. once we have the list images for this person, we have to make the same code book, but with those images
    2. Then we have to make a codebook for this person
    3. loop through all of the codewords for everyone and their feature, test whether or not the midpoint are close enough to the template
     */

    public boolean testIfIncluded(ArrayList<Mat> testingUserImages){
        boolean userIndb = false;
        //same code as the code to get the codebook, only less images
        //used for extracting 1 person
        Mat tempImagePixelsVector;
        Mat tempTemplate = new Mat();

        //used to make the space
        //Make the continuous space
        //Things contained in channels, 1->midpoint, 2->max, 3->mins, 4->ranges
        int numberOfComponents = (testingUserImages.size()>20) ? 20:testingUserImages.size();
        //we have one col because we are using 1 person
        Mat testDomain = new Mat(numberOfComponents, 1, opencv_core.CV_64FC4);
        System.out.println("Creating the testing domain from "+ testingUserImages.size()+" pictures with Component Count : "+ NUM_OF_COMPONENTS);
        int index = 0;

        //loop trough all of the images to get a matrix with all of the image's pixels and make a 10304XNumbimages matrix
        //load the new matrix with imgs (they are in greyscale)
        Mat enrollementMatrix = new Mat();
        Mat tempMultipliesMatrix = new Mat();
        Mat tempMatrix;
        //lets create the templates for everyone
        for (Mat image: testingUserImages) {
            tempMatrix = image.reshape(0, 1).clone();
            enrollementMatrix.push_back(tempMatrix);
        }
        Mat vectors = new Mat();
        Core.PCACompute(enrollementMatrix, new Mat(), vectors, NUM_OF_COMPONENTS);
        Mat transEnrollementMatrix;
        transEnrollementMatrix = enrollementMatrix.t();

        Mat tempTrans = new Mat(transEnrollementMatrix.rows(), transEnrollementMatrix.cols(), vectors.type());
        transEnrollementMatrix.convertTo(tempTrans, vectors.type());

        Core.gemm(vectors, tempTrans, 1, new Mat(), 0, tempMultipliesMatrix);
        System.out.println(tempMultipliesMatrix);
        //get the mins and the max for everyone
        Core.MinMaxLocResult minsAndMax;
        double range;
        double midpoint;

        for (int z = 0; z < tempMultipliesMatrix.rows();z++)
        {
            double [] tuple = new double[4];
            minsAndMax = Core.minMaxLoc(tempMultipliesMatrix.row(z));
            range = minsAndMax.maxVal-minsAndMax.minVal;
            midpoint = (minsAndMax.maxVal-minsAndMax.minVal)/2+minsAndMax.minVal;
            tuple[0] = minsAndMax.minVal;tuple[1] = minsAndMax.maxVal;tuple[2] = range;tuple[3] = midpoint;
            testDomain.put(z,index, tuple);
            System.out.println(testDomain.col(0).row(z).dump());
        }
        System.out.println(testDomain);
//        for (int i = 0; i<testDomain.rows();i++){
//            for (int b = 0;b<testDomain.cols();b++){
//                System.out.print(testDomain.row(i).col(b).dump()+" | ");
//            }
//            System.out.println("");
//        }
        //now let discretize the matrix


        double elementTuple [];
        double elementTupleTraining[];
        double midpoints[] = new double[testDomain.rows()];
        double midpointsTraining[] = new double[continuousDomain.rows()];
        double closestCodeWordTest[] = new double[testDomain.rows()];
        double closestCodeWordTraining[] = new double[testDomain.rows()];
        double shortestDistance;
        double shortestDistanceTraining;
        //simply get the midpoints

        //test for all users
        for (int i = 0; i<continuousDomain.cols();i++){
            System.out.print (i+") ");
            for (int b = 0; b<testDomain.rows();b++) {
                elementTuple = testDomain.get(b, 0);
                elementTupleTraining = continuousDomain.get(b, i);
                midpointsTraining[b] = elementTupleTraining[3];
                midpoints[b] = elementTuple[3];

                shortestDistance = Math.abs(midpoints[b] - (Double)codebook[b][i].get(0));
                shortestDistanceTraining = Math.abs(midpointsTraining[b] - (Double)codebook[b][i].get(0));
                for (int k = 0; k < codebook[b][i].size(); k++){
                    if ((Math.abs(midpoints[b] - (Double)codebook[b][i].get(k)))<shortestDistance){
                        shortestDistance = Math.abs(midpoints[b] - (Double)codebook[b][i].get(k));
                        closestCodeWordTest[b] = (Double)codebook[b][i].get(k);
                    }
                    if ((Math.abs(midpointsTraining[b] - (Double)codebook[b][i].get(k)))<shortestDistanceTraining){
                        shortestDistanceTraining = Math.abs(midpointsTraining[b] - (Double)codebook[b][i].get(k));
                        closestCodeWordTraining[b] = (Double)codebook[b][i].get(k);
                    }
                }
                //get the distances of the codewords
                System.out.print (midpoints[b]+","+Math.round(closestCodeWordTest[b])+" - "+Math.round(midpointsTraining[b])+","+Math.round(closestCodeWordTraining[b])+"\t"+codebook[b][i].size()+"\tmin:"+elementTupleTraining[0]+"max:"+elementTupleTraining[1]+"\t");
                //get closest codeword from the training data
            }
            System.out.println("-");
        }

        //for each midpoint find the closest codeword

        return userIndb;
    }

}

