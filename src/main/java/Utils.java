import org.bytedeco.javacpp.opencv_core.*;

import java.util.ArrayList;

/**
 * Created by trill on 2016-03-27.
 */
public class Utils {
    public static MatVector arrayList2MatVector(ArrayList<Mat> list) {
        Mat[] matArray = new Mat[list.size()];
        matArray = list.toArray(matArray);
        return new MatVector(matArray);
    }

    public static int[] arrayList2IntArray(ArrayList<Integer> list) {
        int[] intArray = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            intArray[i] = list.get(i).intValue();
        }
        return intArray;
    }
}
