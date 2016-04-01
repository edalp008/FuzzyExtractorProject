package recognition;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_face.*;

import java.util.ArrayList;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.COLORMAP_COOL;
import static org.bytedeco.javacpp.opencv_imgproc.applyColorMap;

/**
 * Created by trill on 2016-03-26.
 */
public class EigenFacesRecognizer implements Recognizer {
    private MatVector images;
    private int[] labels;
    BasicFaceRecognizer model;
    public EigenFacesRecognizer(MatVector images, int[] labels) {
        this.images = images;
        this.labels = labels;
        Mat matLabels = new Mat(new IntPointer(labels));
        model = createEigenFaceRecognizer();
        model.train(images, matLabels);
    }

    public Result test(Mat img) {
        int[] label = new int[1];
        double[] metric = new double[1];
        model.predict(img, label, metric);
        return new Result(label[0], metric[0]);
    }

    public void generateEigenFaces(int dimensions, int height, int width) {
        ArrayList<Mat> vectors = new ArrayList<Mat>();
        Mat w = model.getEigenVectors();
        for (int i = 0; i < dimensions; i++) {
            Mat ev = w.col(i).clone();
            Mat greyScale = norm(ev.reshape(1, height));
            Mat coloured = new Mat();
            applyColorMap(greyScale, coloured, COLORMAP_COOL);
            vectors.add(coloured);
        }
        int rows = (int) Math.round(Math.sqrt(dimensions));
        int cols = dimensions - rows;
        Mat grid = new Mat(rows*height, cols*width, CV_8UC3, new Scalar(0.0, 0.0, 0.0, 0.0));
        for (int i = 0; i < vectors.size(); i++) {
            int x = (i%cols)*width;
            int y = (i/cols)*height;
            Rect rect = new Rect(x, y, height, width);
        }
        imwrite("eigen-vectors.png", grid);
        System.out.println("The image has been written.");
    }

    private Mat norm(Mat src) {
        Mat dst = new Mat();
        switch (src.channels()) {
            case 1:
                normalize(src, dst, 0.0, 255.0, NORM_MINMAX, CV_8UC1, noArray());
                break;
            case 3:
                normalize(src, dst, 0.0, 255.0, NORM_MINMAX, CV_8UC3, noArray());
                break;
            default:
                src.copyTo(dst);
                break;
        }
        return dst;
    }
}
