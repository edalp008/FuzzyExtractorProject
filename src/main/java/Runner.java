import codebooks.TrainedCodebook;
import extraction.PCAExtractor;
import extraction.PCATemplate;
import extraction.Reshaper;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import templates.UserTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by trill on 2016-03-26.
 */

public class Runner {
    static public void main(String[] args) {

        //import opencv libraries
        String libopencv_java = "/Users/Erica/Desktop/opencv-2.4.13/build/lib/libopencv_java2413.dylib";
        System.load(libopencv_java);
        System.out.println("We're Running It Now!");

        //get an array list of arrayliat the lower level array list will contain the pictures and the everyone has an
        //ArrayList<ArrayList<Mat>> trainingPeople = Loader.getAllImageByPerson("faces", true);
        //This will call the constructor of the class and will build the codebook, the codebook is stored in the class
       // extraction.PCATemplate template = new extraction.PCATemplate(trainingPeople);
       // DataSet singlePerson = Loader.loadOnePerson("faces", 25);

        //checks if user is in database, this fucntion will make the comparison to the codebook
//        System.out.println(template.testIfIncluded(singlePerson.getTestFaces()));

        //get an array list of people
//        ArrayList<ArrayList<Mat>> trainingPeople2 = Loader.getAllImageByPerson("faces", true);
//        extraction.FisherfaceTemplate template2 = new extraction.FisherfaceTemplate(trainingPeople2);
//        DataSet singlePerson2 = Loader.loadOnePerson("faces", 25);
//        System.out.println(template2.testIfIncluded(singlePerson2.getTestFaces()));


        //get an array list of array lists the lower level array list will contain the pictures and the everyone has an
        //ArrayList<ArrayList<Mat>> trainingPeople = Loader.getAllImageByPerson("faces", true);

        ArrayList<Integer> indices = new ArrayList<Integer>();
        indices.add(8);
        indices.add(9);

        ArrayList<ArrayList<ArrayList<Mat>>> wrapper = Loader.getTrainingAndTestingImages("faces", indices );
        ArrayList<ArrayList<Mat>> trainingPeople = wrapper.get(0);
        ArrayList<ArrayList<Mat>> testingPeople = wrapper.get(1);

        //Renaud Testing some stuff

        PCAExtractor pca = new PCAExtractor( 30, trainingPeople );
        TrainedCodebook codebook = new TrainedCodebook(pca.getNumberOfComponents(), 0.3, trainingPeople, pca);

        System.out.println("User :  False Accept - False Reject - nb of tests");
        int totalFA = 0;
        int totalFR = 0;
        int totalTests = 0;
        int totalFAtests = 0;
        int totalFRtests = 0;

        for (int i = 0; i < trainingPeople.size() ; i++ ){
            UserTemplate person = new UserTemplate(pca, trainingPeople.get(i), codebook);
            int falseAcceptance = 0;
            int falseRejections = 0;
            int numberOfTests = 0;
            int frTests = 0;
            int faTests = 0;

            int k = 0;
            for (ArrayList<Mat> personImages : testingPeople ) {
                for (Mat images : personImages) {
                    if (k == i) {
                        frTests++;
                        if (!(person.isSamePerson(images))) {
                            falseRejections++;
                        }
                    } else {
                        faTests++;
                        if (person.isSamePerson(images)) {
                            falseAcceptance++;
                        }
                    }
                    numberOfTests++;
                }
                k++;
            }

            totalFA += falseAcceptance;
            totalFR += falseRejections;
            totalTests += numberOfTests;
            totalFAtests += faTests;
            totalFRtests += frTests;

            //System.out.println( "User "+ i + ":  " + falseAcceptance + " - " + falseRejections + " - " + numberOfTests);
        }

        double far = (double)totalFA / totalFAtests;
        double frr = (double)totalFR / totalFRtests;

        System.out.println( "Total:  " + totalFA + " - " + totalFR + " - " + totalTests);

        System.out.println("\n We had : \n FAR of " + far + "\n FRR of " + frr);

        /*
        for (int i = 0 ; i < trainingPeople.size() ; i++ ){

            UserTemplate person = new UserTemplate(pca, trainingPeople.get(i), codebook);
            int falseAcceptance = 0;
            int falseRejections = 0;

            int k = 0;
            for (ArrayList<Mat> personImages : trainingPeople ) {
                for (Mat images : personImages) {
                    if (k == i) {
                        if (!(person.isSamePerson(images))) {
                            falseRejections++;
                        }
                    } else {
                        if (person.isSamePerson(images)) {
                            falseAcceptance++;
                        }
                    }
                }
                k++;
            }


            System.out.println("For person " + i + " we had : \n False Accept : " + falseAcceptance
                                    + "\n False Rejections : " + falseRejections + "\n");

        }

        /*
        UserTemplate person1 = new UserTemplate(pca, trainingPeople.get(0), codebook);

        int k = 0;
        for (ArrayList<Mat> personImages : trainingPeople) {
            System.out.println("\n Testing person " + k + " against person 0 : ");
            for (Mat images : personImages ) {
                System.out.println(person1.isSamePerson(images));
            }
            k++;
        }
        /*
        for (Mat images : trainingPeople.get(0) ) {
            System.out.println(person1.isSamePerson(images));
        }
        /*
        System.out.println("Global codebook : \n");
        for (double[] section: codebook.getGlobalCodebook()) {
            System.out.println(Arrays.toString(section));
        }
        */

        //System.out.println("User codebook : \n");
        //for (double[] section: person1.getUserCodebook()) {
        //    System.out.println(Arrays.toString(section));
        //}

        /*
        System.out.println("User mid point vector : \n" + Arrays.toString(person1.getMidPointVector()));
        System.out.println("MidPointVector's codeword : \n " + Arrays.toString(person1.getCodeword(person1.getMidPointVector())));
        */

        //Mat feature = new Mat();
        //Mat image = trainingPeople.get(0).get(0).reshape(0,1);
        //System.out.println(image);
        //feature = pca.extractFeatures(image);

        //System.out.println(Arrays.toString(feature.get(3,0)));
        //System.out.println(feature.get(3,0)[0]);
        //System.out.println(feature.dump());

        /*
        System.out.println("Training People : \n " + trainingPeople + "\n\n");

        Mat reshapedRow = Reshaper.reshapeForTraining(trainingPeople);
        Mat reshapedCol = Reshaper.reshapeForTrainingCol(trainingPeople);

        System.out.println("Reshaped As Rows: \n" + reshapedRow);
        System.out.println("Reshaped As Cols: \n" + reshapedCol);

        System.out.println(reshapedCol.type());

        Mat oneSmallRow = reshapedCol.row(0);
        System.out.println("One small row : \n" + oneSmallRow + "\n");
        System.out.println("Values of the small row : \n" + oneSmallRow.dump() + "\n");
        Mat oneSmallConvRow = new Mat();
        oneSmallRow.convertTo(oneSmallConvRow, 5);
        System.out.println("Values of the row after conversion : \n " + oneSmallConvRow.dump());
        */


        //Renaud testing more stuff

        /*
        System.out.println("Global codebook : \n");
        for (double[] section: codebook.getGlobalCodebook()) {
            System.out.println(Arrays.toString(section));
        }
        */

        /*
        Mat person1Images = Reshaper.reshapeForEnrollment( trainingPeople.get(0));

        Mat features1 = pca.extractFeatures(person1Images);

        System.out.println(features1);

        Core.MinMaxLocResult minsAndMax = Core.minMaxLoc(features1.row(0));

        System.out.println(features1.dump());

        System.out.println( "MinAndMax \n" + minsAndMax + "\n Min : " + minsAndMax.minVal
                                                        + "\n Max : " + minsAndMax.maxVal);
        */



        //  System.out.println("Extractor Matrix : \n" + pca.getExtractorMatrix());
        //  System.out.println("Mean Image Vector : \n" + pca.getTranslationVector());


/*
        //This will call the constructor of the class and will build the codebook, the codebook is stored in the class
        extraction.PCATemplate template = new extraction.PCATemplate(trainingPeople);

        //*Test Everyone
        //tempResult[0] is highest match for user i, tempResult[1] is 2nd highest match for user i, tempResult[2] is avg match for user i, tempResult[3] is the use who matched user i (which may be, and often is, another user)
        int accepted = 0;
        int fakeFalseNegative = 0;
        int hightestHighestTrue =0;
        double avgHighestTrue = 0;
        double avgNextHighest = 0;
        double avgTrue = 0;
        int numberOfTests = 40;
        for(int i = 0; i< numberOfTests;i++){
            DataSet singlePerson = Loader.loadOnePerson("faces", i);
            double[] tempResult = template.testIfIncluded(singlePerson.getTestFaces());
            avgHighestTrue = avgHighestTrue + tempResult[0];
            avgNextHighest = avgNextHighest + tempResult[1];
            avgTrue = avgTrue + tempResult[2];
            if(tempResult[0] > hightestHighestTrue) hightestHighestTrue = (int)tempResult[0];
            if(tempResult[0] == 20)accepted++;
            if(tempResult[3] < 0)System.out.println("Weird things happened.");
            if(tempResult[3] != i)fakeFalseNegative++;
        }
        avgHighestTrue = avgHighestTrue/numberOfTests;
        avgNextHighest = avgNextHighest/numberOfTests;
        avgTrue = avgTrue/numberOfTests;
        System.out.println("\n\nalpha: ");
        if(PCATemplate.useAlphaList){
            for(int t = 0;t < PCATemplate.alphaList.length;t++){System.out.print(PCATemplate.alphaList[t]+", ");}} else {
            System.out.print(PCATemplate.alpha);
        }
        System.out.print("\nNumber of Tests: " + numberOfTests + "\nAccepted: " + accepted + "\nFake false acceptance: " + fakeFalseNegative + "\nHighest highest value: " + hightestHighestTrue + "\nAverage highest count: " + avgHighestTrue + "\nAverage next highest count: " + avgNextHighest + "\nAverage true count: " + avgTrue);
 */
        //End test everyone */
    }
}



