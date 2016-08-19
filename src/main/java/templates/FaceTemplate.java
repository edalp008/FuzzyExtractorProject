package templates;

import extraction.FeatureExtractor;
import codebooks.Codebook;
import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Created by Renaud on 2016-08-13.
 */
public interface FaceTemplate {

    double[] getSecureSketch();

//    void setExtractor (FeatureExtractor extractor1);

    FeatureExtractor getExtractor ();

    void makeTemplate ( ArrayList<Mat> enrollmentData );

    void computeCodeook (Codebook mainCodebook);

}
