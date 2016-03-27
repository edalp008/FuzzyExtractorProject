package recognition;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_face.*;
import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;

/**
 * Created by trill on 2016-03-26.
 */
public class EigenFacesRecognizer {
    private MatVector images;
    private int[] labels;
    BasicFaceRecognizer model;
    public EigenFacesRecognizer(MatVector images, int[] lables) {
        this.images = images;
        this.labels = lables;
        Mat matLabels = new Mat(new IntPointer(lables));
        model = createEigenFaceRecognizer();
        model.train(images, matLabels);
    }
}
