package extraction;

import org.bytedeco.javacpp.indexer.FloatBufferIndexer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.PCA;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by trill on 2016-03-29.
 */
public class PCATemplate {

    private static final int NUM_OF_COMPONENTS = 20;
    private ArrayList<Mat> imgs;
    private float[][] features;
    private float[] midPoints;
    private float[] ranges;
    private float[] averages;
    public PCATemplate(ArrayList<Mat> imgs) {
        this.imgs = imgs;
        this.extractFeatures();
        this.process();
    }

    private void extractFeatures() {
        int index = 0;
        int totalFeatures = 0;
        for (int k = 0; k < imgs.size(); k++) {
            PCA pca = new PCA(imgs.get(k), new Mat(), PCA.DATA_AS_COL, NUM_OF_COMPONENTS);
            Mat vectors = pca.eigenvectors();

            // Initialize the features data structure
            if (k == 0) {
                totalFeatures = imgs.size() * NUM_OF_COMPONENTS;
                features = new float[totalFeatures][vectors.cols()];
            }

            System.out.println("rows: " + vectors.rows() + ", cols: " + vectors.cols());
            FloatBufferIndexer indexer = vectors.createIndexer();

            for (int i = 0; i < vectors.rows(); i++) {
                for (int j = 0; j < vectors.cols(); j++) {
                    features[index + i][j] = indexer.get(i, j);
                }
            }

            index += NUM_OF_COMPONENTS;
        }
    }

    private void process() {
        // Process the data
        midPoints = new float[features[0].length];
        ranges = new float[features[0].length];
        averages = new float[features[0].length];
        // Find the midpoints and ranges
        for (int j = 0; j < features[0].length; j++) {
            float[] tempComponent = new float[features.length];
            for (int i = 0; i < features.length; i++) {
                tempComponent[i] = features[i][j];
                averages[j] += features[i][j];
            }
            averages[j] = averages[j]/tempComponent.length;
            // Sort the component array
            Arrays.sort(tempComponent);
            // Find the midpoint
            if (tempComponent.length % 2 == 0) {
                midPoints[j] = (tempComponent[tempComponent.length/2] + tempComponent[tempComponent.length/2 - 1])/2;
            } else {
                midPoints[j] = tempComponent[tempComponent.length/2];
            }
            // Compute the range
            ranges[j] = (tempComponent[tempComponent.length - 1] - tempComponent[0])/2;

            //System.out.println("midpoint: " + midPoints[j] + ", avg: " + averages[j] + ", range: " + ranges[j]);
        }
    }

    public void test(Mat img) {
        System.out.println("Test image");
        int trueCounter = 0;
        int falseCounter = 0;
        PCA pca = new PCA(img, new Mat(), PCA.DATA_AS_COL, NUM_OF_COMPONENTS);
        Mat vectors = pca.eigenvectors();
        FloatBufferIndexer indexer = vectors.createIndexer();
        float[][] testFeatures = new float[vectors.rows()][vectors.cols()];
        for (int i = 0; i < vectors.rows(); i++) {
            for (int j = 0; j < vectors.cols(); j++) {
                testFeatures[i][j] = indexer.get(i, j);
            }
        }
        // Test the equality for all components
        for (int i = 0; i < testFeatures.length; i++) {
            for (int j = 0; j < testFeatures[0].length; j++) {
                // Test each component against the template
                if ((midPoints[j] - ranges[j]) <= testFeatures[i][j]
                        && testFeatures[i][j] <= (midPoints[j] + ranges[j])) {
                    trueCounter++;
                } else {
                    falseCounter++;
                }
            }
        }
        System.out.println("true count: " + trueCounter);
        System.out.println("false count: " + falseCounter);
    }
}
