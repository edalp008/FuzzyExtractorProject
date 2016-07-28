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

        //get an array list of arrayliat the lower level array list will contain the pictures and the everyone has an
        ArrayList<ArrayList<Mat>> trainingPeople = Loader.getAllImageByPerson("faces", true);
        //This will call the constructor of the class and will build the codebook, the codebook is stored in the class
        extraction.PCATemplate template = new extraction.PCATemplate(trainingPeople);
        DataSet singlePerson = Loader.loadOnePerson("faces", 25);

        //checks if user is in database, this fucntion will make the comparison to the codebook
        System.out.println(template.testIfIncluded(singlePerson.getTestFaces()));

        //get an array list of people
//        ArrayList<ArrayList<Mat>> trainingPeople2 = Loader.getAllImageByPerson("faces", true);
//        extraction.FisherfaceTemplate template2 = new extraction.FisherfaceTemplate(trainingPeople2);
//        DataSet singlePerson2 = Loader.loadOnePerson("faces", 25);
//        System.out.println(template2.testIfIncluded(singlePerson2.getTestFaces()));
    }
}
