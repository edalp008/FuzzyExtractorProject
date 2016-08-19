package extraction;

import org.opencv.core.Mat;

/**
 * This is the extractor class for using a RandomProjection based approach.
 * Created by Renaud on 2016-08-11.
 */
public class RPExtractor implements FeatureExtractor {

    private int numberOfComponents;
    private int imageSize;
    private Mat extractorMatrix;
    private Mat translationVector;

    public RPExtractor (int numberOfComponents, int imageSize) {
        setNumberOfComponents( numberOfComponents);
        setImageSize( imageSize );
    }

    public void setNumberOfComponents (int numberOfComponents) {
        this.numberOfComponents = numberOfComponents;
    }

    public void setImageSize (int imageSize) {
        this.imageSize = imageSize;
    }

    public void setExtractorMatrix (Mat extractorMatrix) {
        this.extractorMatrix = extractorMatrix;
    }

    public void setTranslationVector (Mat translationVector) {
        this.translationVector = translationVector;
    }

    public int getNumberOfComponents () {
        return numberOfComponents;
    }

    public int getImageSize () {
        return imageSize;
    }

    public Mat getExtractorMatrix () {
        return extractorMatrix;
    }

    public Mat getTranslationVector () {
        return translationVector;
    }

    public Mat extractFeatures ( Mat imageMatrix ){
        return new Mat();
    }

}
