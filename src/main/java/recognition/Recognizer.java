package recognition;

import org.bytedeco.javacpp.opencv_core.Mat;

/**
 * Created by trill on 2016-03-29.
 */
public interface Recognizer {
    public Result test(Mat img);
}
