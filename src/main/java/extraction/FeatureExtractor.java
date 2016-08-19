package extraction;

import org.opencv.core.Mat;

/**
 * Created by Renaud on 2016-08-10.
 */
public interface FeatureExtractor {

    //Setters for the variables
    void setNumberOfComponents (int numberOfComponent);
//    void setExtractorMatrix (Mat extractorMatrix);
//    void setTranslationVector (Mat translationVector);
//    void setImageSize( int imageSize );

    //getters for the variables
    int getNumberOfComponents();
//    int getImageSize();
//    Mat getExtractorMatrix ();
//    Mat getTranslationVector ();

    //Required extractor
    Mat extractFeatures ( Mat imageMatrix );

}
