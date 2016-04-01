import extraction.PCATemplate;

/**
 * Created by trill on 2016-03-26.
 */
public class Runner {
    static public void main(String[] args) {
        System.out.println("We Running It Now!");
        DataSet dataSet = Loader.load("faces", true);
        DataSet singlePerson = Loader.loadOnePerson("faces", 0);
        PCATemplate template = new PCATemplate(singlePerson.getFaces());
        template.test(singlePerson.getTestFaces().get(0));
    }
}
