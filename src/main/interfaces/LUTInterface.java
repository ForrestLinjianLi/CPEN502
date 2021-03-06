package main.interfaces;

import main.interfaces.CommonInterface;

public interface LUTInterface extends CommonInterface {

    /**
    * Initialise the look up table to all zeros.
    */
    public void initialiseLUT();

    /**
    * A helper method that translates a vector being used to index the look up table
    * into an ordinal that can then be used to access the associated look up table element.
    * @param X The state action vector used to index the LUT
    * @return The index where this vector maps to
    */
    public int indexFor(double [] X);
}
