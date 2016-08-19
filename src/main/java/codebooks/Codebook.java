package codebooks;

import java.util.ArrayList;

/**
 * This interface implements the needed methods for a codebook in our aplication.
 * Created by Renaud on 2016-08-12.
 */
public interface Codebook {

    // void setNumberOfComponents( int numberOfComponents );
    int getNumberOfComponents ();

    ArrayList<double[]> getGlobalCodebook ();

    /**
     * Generates a user specific codebook from the user's data of deltas, mins and maxs. Different implementation
     * may or may not use all the data in their specific implementations.
     * @param deltas
     * @param mins
     * @param maxs
     * @return
     */
    ArrayList<double[]> generateUserCodebook (double[] deltas, double[] mins, double[] maxs);

    ArrayList<double[]> generateUserCodebook2( double[] deltas, double[] midPointVector );
}
