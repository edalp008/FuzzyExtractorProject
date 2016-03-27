import org.bytedeco.javacpp.opencv_core.*;

import java.util.ArrayList;

/**
 * Created by trill on 2016-03-26.
 */
public class Runner {
    static public void main(String[] args) {
        System.out.println("We Running It Now!");
        DataSet dataSet = Loader.load("faces", true);
        ArrayList<Mat> faces = dataSet.getFaces();
        ArrayList<Integer> labels = dataSet.getLabels();
        ArrayList<Mat> testFaces = dataSet.getTestFaces();
        ArrayList<Integer> testLabels = dataSet.getTestLabels();
        System.out.println("Faces: " + faces.size());
        System.out.println("Labels: " + labels.size());
        System.out.println("Test faces: " + testFaces.size());
        System.out.println("Test labels: " + testLabels.size());
    }
}
