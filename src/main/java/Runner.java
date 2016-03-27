import recognition.EigenFacesRecognizer;

/**
 * Created by trill on 2016-03-26.
 */
public class Runner {
    static public void main(String[] args) {
        System.out.println("We Running It Now!");
        DataSet dataSet = Loader.load("faces", true);
        EigenFacesRecognizer recognizer = new EigenFacesRecognizer(Utils.array2MatVector(dataSet.getFaces()),
                Utils.array2IntArray(dataSet.getLabels()));
    }
}
