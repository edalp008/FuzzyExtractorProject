import org.bytedeco.javacpp.opencv_core.*;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Alain, Erica, Renaud.
 */
public class Loader {
    private static final double FACE_RATIO = 1.0;
    private static final double IMAGE_RATIO = .8;
    private static final double ALT_IMAGE_RATIO = 0.9;


    /*
        This function returns an ArrayList of ArrayList [ [ Image 1 of person 1, ... , Image k of person 1 ],
                                                            ... ,
                                                          [ Image 1 of person N, ... , Image k or person N ] ]
     */
    public static ArrayList<ArrayList<Mat>> getAllImageByPerson (String path, boolean includeTests){
        /* Define variables */
        int faceLimit = 0;
        int imageLimit = 0;

        ClassLoader classLoader = Loader.class.getClassLoader();
        File folder = new File(classLoader.getResource(path).getFile());

        //ArrayList<ArrayList<Mat>> people
        //ArrayList - array list of everyone
        //<ArrayList> - all images of one person
        ArrayList<ArrayList<Mat>> people = new ArrayList<ArrayList<Mat>>();

        /* Only proceed if the specified path is a directory */
        if (folder.isDirectory()) {

            ArrayList<File> contents = new ArrayList<File>(Arrays.asList(folder.listFiles()));
            faceLimit = (int) Math.round(contents.size() * FACE_RATIO);

            for (int i = 0; i < contents.size(); i++) {
                ArrayList<Mat> tempPerson = new ArrayList<Mat>();
                File file = contents.get(i);
                ArrayList<File> images = new ArrayList<File>(Arrays.asList(file.listFiles()));
                imageLimit = (int) Math.round(images.size() * IMAGE_RATIO);
                for (int j = 0; j < images.size(); j++) {
                    File image = images.get(j);
                    if (i >= faceLimit || j >= imageLimit)
                    {
                    } else {
                        tempPerson.add(Highgui.imread(image.getPath(), 0));
                    }
                }
                if (i < faceLimit){people.add(tempPerson);}

            }
        } else {
            throw new IllegalArgumentException("The path does not lead to a directory.");
        }
        return people;
    }

    public static DataSet loadOnePerson(String path, int person) {
        int imageLimit = 0;
        ArrayList<Mat> faces = new ArrayList<Mat>();
        ArrayList<Integer> labels = new ArrayList<Integer>();
        ArrayList<Mat> testFaces = new ArrayList<Mat>();
        ArrayList<Integer> testLabels = new ArrayList<Integer>();
        ClassLoader classLoader = Loader.class.getClassLoader();
        File folder = new File(classLoader.getResource(path).getFile());
        /* Only proceed if the specified path is a directory */
        if (folder.isDirectory()) {
            ArrayList<File> contents = new ArrayList<File>(Arrays.asList(folder.listFiles()));
            // Check if the provided index is valid

            person = (person >= 0 && person <= contents.size() - 1) ? person : 0;
            ArrayList<File> images = new ArrayList<File>(Arrays.asList(contents.get(person).listFiles()));
            imageLimit = (int) Math.round(images.size() * ALT_IMAGE_RATIO);
            for (int i = 0; i < images.size(); i++) {
                if (i >= imageLimit) {
                    testFaces.add(Highgui.imread(images.get(i).getPath(), 0));
                    testLabels.add(person + 1);
                } else {
                    faces.add(Highgui.imread(images.get(i).getPath(), 0));

                    labels.add(person + 1);
                }
            }
        } else {
            throw new IllegalArgumentException("The path does not lead to a directory.");
        }
        return new DataSet(faces, labels, testFaces, testLabels);
    }


    /**
     * This method will return a list containing two lists of lists of Mat objects.
     * The first one is the training set of images.
     * The second one is the set of images that have been chosen to NOT be in the training set. They are to be
     * used for testing.
     * @param path The path to the folder
     * @param imagesForTraining An array of indices
     * @return
     */
    public static ArrayList<ArrayList<ArrayList<Mat>>> getTrainingAndTestingImages ( String path,
                                                                                     ArrayList<Integer> imagesForTraining) {

        /* This implementation uses the List.contain(object) method from the list. It is not the most efficient
        * method to select things. We might want to optimize this. -Renaud*/

        //The list of images to keep for testing
        ArrayList<Integer> listTraining = imagesForTraining;

        //training has the images to train and/or enroll
        //testing keeps the images for testing after enrollment
        ArrayList<ArrayList<Mat>> training = new ArrayList<ArrayList<Mat>>();
        ArrayList<ArrayList<Mat>> testing = new ArrayList<ArrayList<Mat>>();

        //Loader to get the files
        ClassLoader classLoader = Loader.class.getClassLoader();
        File folder = new File(classLoader.getResource(path).getFile());

        /* Only proceed if the specified path is a directory */
        if (folder.isDirectory()) {

            ArrayList<File> contents = new ArrayList<File>(Arrays.asList(folder.listFiles()));

            for (int i = 0; i < contents.size(); i++) {
                ArrayList<Mat> tempPersonTraining = new ArrayList<Mat>();
                ArrayList<Mat> tempPersonTesting = new ArrayList<Mat>();
                File file = contents.get(i);
                ArrayList<File> images = new ArrayList<File>(Arrays.asList(file.listFiles()));
                for (int j = 0; j < images.size(); j++) {
                    File image = images.get(j);
                    if (listTraining.contains(j)) {
                        tempPersonTesting.add(Highgui.imread(image.getPath(), 0));
                    } else {
                        tempPersonTraining.add(Highgui.imread(image.getPath(), 0));
                    }
                }
                training.add(tempPersonTraining);
                testing.add(tempPersonTesting);
            }
        } else {
            throw new IllegalArgumentException("The path does not lead to a directory.");
        }

        ArrayList<ArrayList<ArrayList<Mat>>> output = new ArrayList<ArrayList<ArrayList<Mat>>>();
        output.add(training);
        output.add(testing);

        return output;
    }

}
