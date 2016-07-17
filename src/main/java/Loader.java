import org.bytedeco.javacpp.opencv_core.*;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by trill on 2016-03-26.
 */
public class Loader {
    private static final double FACE_RATIO = 0.8;
    private static final double IMAGE_RATIO = 0.7;
    private static final double ALT_IMAGE_RATIO = 0.7;

    public static DataSet load(String path, boolean includeTests) {

        /* Define variables */
        int faceLimit = 0;
        int imageLimit = 0;
        int labelCounter = 1;
        ArrayList<Mat> faces = new ArrayList<Mat>();
        ArrayList<Integer> labels = new ArrayList<Integer>();
        ArrayList<Mat> testFaces = new ArrayList<Mat>();
        ArrayList<Integer> testLabels = new ArrayList<Integer>();
        ClassLoader classLoader = Loader.class.getClassLoader();
        File folder = new File(classLoader.getResource(path).getFile());

        //ArrayList<ArrayList<Mat>> people
        ArrayList<ArrayList<Mat>> people = new ArrayList<ArrayList<Mat>>();

        /* Only proceed if the specified path is a directory */
        if (folder.isDirectory()) {

            /* Determine if test cases are to be included */
            if (includeTests) {

                /* Load 80% of the faces and 90% of the face images as data, all
                * else will be loaded as test data. */
                ArrayList<File> contents = new ArrayList<File>(Arrays.asList(folder.listFiles()));
                faceLimit = (int) Math.round(contents.size() * FACE_RATIO);



                for (int i = 0; i < contents.size(); i++) {
                    ArrayList<Mat> tempPerson = new ArrayList<Mat>();
                    File file = contents.get(i);
                    ArrayList<File> images = new ArrayList<File>(Arrays.asList(file.listFiles()));
                    imageLimit = (int) Math.round(images.size() * IMAGE_RATIO);
                    for (int j = 0; j < images.size(); j++) {
                        File image = images.get(j);
                        if (i >= faceLimit || j >= imageLimit) {

                            testFaces.add(Highgui.imread(image.getPath(), 0));
                            testLabels.add(labelCounter);
                        } else {
                            faces.add(Highgui.imread(image.getPath(), 0));
                            tempPerson.add(Highgui.imread(image.getPath(), 0));
                            labels.add(labelCounter);
                        }
                    }
                    people.add(tempPerson);

                    labelCounter++;

                }

            } else {
                /* Load all faces and labels */
                ArrayList<File> contents = new ArrayList<File>(Arrays.asList(folder.listFiles()));
                for (File file : contents) {
                    ArrayList<File> images = new ArrayList<File>(Arrays.asList(file.listFiles()));
                    for (File image : images) {
                        faces.add(Highgui.imread(image.getPath(), 0));
                        labels.add(labelCounter);
                    }
                    labelCounter++;
                }
            }
        } else {
            throw new IllegalArgumentException("The path does not lead to a directory.");
        }
        return new DataSet(faces, labels, testFaces, testLabels);
    }

    public static ArrayList<ArrayList<Mat>> getAllImageByPerson (String path, boolean includeTests){
        /* Define variables */
        int faceLimit = 0;
        int imageLimit = 0;

        ClassLoader classLoader = Loader.class.getClassLoader();
        File folder = new File(classLoader.getResource(path).getFile());

        //ArrayList<ArrayList<Mat>> people
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
                    if (i >= faceLimit || j >= imageLimit) {
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
}
