import org.opencv.core.Mat;
import org.opencv.core.Core;
import java.util.ArrayList;

/**
 * Created by trill on 2016-03-26.
 */

public class Runner {
    static public void main(String[] args) {

        //import opencv libraries
        String libopencv_java = "/Users/Erica/Desktop/opencv-2.4.13/build/lib/libopencv_java2413.dylib";
        System.load(libopencv_java);
        System.out.println("We're Running It Now!");

        //get an array list of people
        ArrayList<ArrayList<Mat>> trainingPeople = Loader.getAllImageByPerson("faces", true);
        extraction.PCATemplate template = new extraction.PCATemplate(trainingPeople);
        DataSet singlePerson = Loader.loadOnePerson("faces", 0);

        //checks if user is in database
        System.out.println(template.testIfIncluded(singlePerson.getTestFaces()));

    }
}
