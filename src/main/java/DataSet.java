//import org.bytedeco.javacpp.opencv_core.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import java.util.ArrayList;

/**
 * Created by trill on 2016-03-26.
 */
public class DataSet {
    /* Instance variables */
    private ArrayList<Mat> faces;
    private ArrayList<Integer> labels;
    private ArrayList<Mat> testFaces;
    private ArrayList<Integer> testLabels;

    //added by Erica
    private ArrayList<ArrayList<Mat>> imagesByFaces;

    /* Constructor */
    public DataSet(ArrayList<Mat> faces, ArrayList<Integer> labels,
                   ArrayList<Mat> testFaces, ArrayList<Integer> testLabels) {
        this.faces = faces;
        this.labels = labels;
        this.testFaces = testFaces;
        this.testLabels = testLabels;
    }

    /* Getters and Setters */
    public ArrayList<Mat> getFaces() {
        return faces;
    }
    public void setFaces(ArrayList<Mat> faces) {
        this.faces = faces;
    }

    public ArrayList<ArrayList<Mat>> getTrainingPeople() {
        return imagesByFaces;
    }
    public void setPeople(ArrayList<ArrayList<Mat>> faces) {
        this.imagesByFaces = faces;
    }

    public ArrayList<Integer> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<Integer> labels) {
        this.labels = labels;
    }

    public ArrayList<Mat> getTestFaces() {
        return testFaces;
    }

    public void setTestFaces(ArrayList<Mat> testFaces) {
        this.testFaces = testFaces;
    }

    public ArrayList<Integer> getTestLabels() {
        return testLabels;
    }

    public void setTestLabels(ArrayList<Integer> testLabels) {
        this.testLabels = testLabels;
    }
}
