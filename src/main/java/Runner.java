import org.opencv.core.Mat;
import org.opencv.core.Core;
import java.util.ArrayList;


/**
 * Created by trill on 2016-03-26.
 */



public class Runner {
    static public void main(String[] args) {

        String libopencv_java = "/Users/Erica/Desktop/opencv-2.4.13/build/lib/libopencv_java2413.dylib";
        System.load(libopencv_java);
        System.out.println("We're Running It Now!");
        DataSet dataSet = Loader.load("faces", true);

        DataSet singlePerson = Loader.loadOnePerson("faces", 38);

        DataSet secondPerson = Loader.loadOnePerson("faces", 1);

//        PCATemplate template = new PCATemplate(dataSet.getFaces());
//        template.test(singlePerson.getFaces().get(0));



        ArrayList<ArrayList<Mat>> trainingPeople = Loader.getAllImageByPerson("faces", true);
        extraction.PCATemplate template = new extraction.PCATemplate(trainingPeople);
        System.out.println(template.testIfIncluded(singlePerson.getTestFaces()));

//        template.
//        opencv_core.Mat largeFeatureVector[] = new opencv_core.Mat[15];
//        //now lets add to template feature vectors
//        for(int i = 0; i<15; i++){
//            largeFeatureVector = template.addToFeatures(Loader.loadOnePerson("faces", i).getFaces(), i);
//        }
        //template.enroll(singlePerson.getFaces());

        // PCATemplate Enroll1 = new PCATemplate(singlePerson.getFaces(), true);



        // template.test(singlePerson.getFaces().get(0));
        // template.test(secondPerson.getFaces().get(0));
        //Sketch erica = new Sketch(template);


        //System.out.println("Load Third person");
        DataSet thirdPerson = Loader.loadOnePerson("faces", 2);
        // System.out.println("Load Fourth person" + '\n');
        DataSet fourthPerson = Loader.loadOnePerson("faces", 3);

        //  template.test(thirdPerson.getFaces().get(0));
        // template.test(fourthPerson.getFaces().get(0));


      /*  PCATemplate templateOne = new PCATemplate(singlePerson.getFaces());
        templateOne.test(singlePerson.getTestFaces().get(0));


        System.out.println("---------------");

        templateOne.test(secondPerson.getTestFaces().get(0));


        System.out.println("---------------");



        */
/*
        Sketch temp = template.getErica();

        float[] tester = template.doStuff(temp);

        System.out.println(tester);



    /*   PCATemplate templateAll= new PCATemplate(dataSet.getFaces());
       templateAll.test(singlePerson.getTestFaces().get(0));
    /*    templateAll.test(secondPerson.getTestFaces().get(0));
        templateAll.test(thirdPerson.getTestFaces().get(0));
        templateAll.test(fourthPerson.getTestFaces().get(0));
       // System.out.println("The number of template images of first person" + singlePerson.getFaces().size());

        System.out.println("Next Picture" + '\n');


        templateAll.test(singlePerson.getTestFaces().get(1));
        templateAll.test(secondPerson.getTestFaces().get(1));
        templateAll.test(thirdPerson.getTestFaces().get(1));
        templateAll.test(fourthPerson.getTestFaces().get(1));
        //templateAll.test(templateAll.get(1));

       /*
        System.out.println("Load First person");
        //DataSet singlePerson = Loader.loadOnePerson("faces", 1);
        System.out.println("Load Second person");
        DataSet secondPerson = Loader.loadOnePerson("faces", 2);
        System.out.println("Load Third person");
        DataSet thirdPerson = Loader.loadOnePerson("faces", 3);
        System.out.println("Load Fourth person" + '\n');
        DataSet fourthPerson = Loader.loadOnePerson("faces", 4);

        System.out.println("Create Template of First Person");
        PCATemplate template = new PCATemplate(singlePerson.getFaces());

        System.out.println("Test First Person's Template against picture of First Person : " );
        template.test(singlePerson.getTestFaces().get(0));
        System.out.println("Test First Person's Template against picture of Second Person : ");
        template.test(secondPerson.getTestFaces().get(0));
        System.out.println("Test First Person's Template against picture of Third Person : ");
        template.test(thirdPerson.getTestFaces().get(0));
        System.out.println("Test First Person's Template against picture of Fourth Person : " );
        template.test(fourthPerson.getTestFaces().get(0));

        System.out.println("Create Template of Second Person");
        PCATemplate template2 = new PCATemplate(secondPerson.getFaces());
        System.out.println("Test Second Person's Template against picture of First Person : ");
        template2.test(singlePerson.getTestFaces().get(0));
        System.out.println("Test Second Person's Template against picture of Second Person : ");
        template2.test(secondPerson.getTestFaces().get(0));
        System.out.println("Test Second Person's Template against picture of Third Person : ");
        template2.test(thirdPerson.getTestFaces().get(0));
        System.out.println("Test Second Person's Template against picture of Fourth Person : ");
        template2.test(fourthPerson.getTestFaces().get(0));

        System.out.println("Create Template of Third Person");
        PCATemplate template3 = new PCATemplate(thirdPerson.getFaces());
        System.out.println("Test Third Person's Template against picture of First Person : ");
        template3.test(singlePerson.getTestFaces().get(0));
        System.out.println("Test Third Person's Template against picture of Second Person : " );
        template3.test(secondPerson.getTestFaces().get(0));
        System.out.println("Test Third Person's Template against picture of Third Person : " );
        template3.test(thirdPerson.getTestFaces().get(0));
        System.out.println("Test Third Person's Template against picture of Fourth Person : ");
        template3.test(fourthPerson.getTestFaces().get(0));

        System.out.println("Create Template of Fourth Person");
        PCATemplate template4 = new PCATemplate(fourthPerson.getFaces());
        System.out.println("Test Fourth Person's Template against picture of First Person : ");
        template4.test(singlePerson.getTestFaces().get(0));
        System.out.println("Test Fourth Person's Template against picture of Second Person : ");
        template4.test(secondPerson.getTestFaces().get(0));
        System.out.println("Test Fourth Person's Template against picture of Third Person : ");
        template4.test(thirdPerson.getTestFaces().get(0));
        System.out.println("Test Fourth Person's Template against picture of Fourth Person : ");
        template4.test(fourthPerson.getTestFaces().get(0));
*/

    }
}
