package main.java;

import java.awt.*;

public class Orientation
{
    // ====================================
    // Attributes
    // ====================================
    public static final char    NORTH               = 'N';
    public static final char    EAST                = 'E';
    public static final char    SOUTH               = 'S';
    public static final char    WEST                = 'W';

    /**
     * List of potential orientations sorted clockwise.
     * @warning Do not change this order.   It is a lightweight computation for turn left/right
     */
    public static final char[]  ORIENTATION_LIST    = {NORTH, EAST, SOUTH, WEST};
    public static final int     NB_ORIENTATION      = ORIENTATION_LIST.length;

    private int                 currentOrientation  = -1;   // Index within the array orientationList


    // ====================================
    // Constructors
    // ====================================

    /**
     * Parse the input orientation following the user-input file-format
     * @param orientation orientation following the user-input file-format: either Orientation.NORTH,
     *                    Orientation.EAST,
     *                    Orientation.SOUTH or
     *                    Orientation.WEST
     * @return An object with the found orientation
     * @throws RuntimeException if the input corresponds to no known orientation
     */
    public Orientation(String orientation)
    {
        for (int i=0; i<NB_ORIENTATION; i++)
        {
            if (orientation.equals(""+ORIENTATION_LIST[i]))
            {
                this.currentOrientation = i;
                return;
            }
        }

        throw new RuntimeException("Unhandled orientation: " + orientation);
    }


    public Orientation(Orientation orientation)
    {
        this.currentOrientation = orientation.currentOrientation;
    }


    // ====================================
    // Public Methods
    // ====================================

    /**
     * Change the current internal orientation to the right
     */
    public void turnRight()
    {
        this.currentOrientation = (this.currentOrientation == NB_ORIENTATION-1) ? 0 : this.currentOrientation +1;
    }


    /**
     * Change the current internal orientation to the left
     */
    public void turnLeft()
    {
        this.currentOrientation = (this.currentOrientation == 0) ? NB_ORIENTATION-1 : this.currentOrientation -1;
    }


    /**
     * Forward the input position depending on the current orientation.
     * Update the value of the input parameter position depending on the current orientation
     * @param localisation Input/output position to move forward.   This object is modified by this method.
     */
    public void forwardLocalisation(Point localisation)
    {
        if      (ORIENTATION_LIST[currentOrientation] == NORTH)  localisation.y ++;
        else if (ORIENTATION_LIST[currentOrientation] == EAST)   localisation.x ++;
        else if (ORIENTATION_LIST[currentOrientation] == SOUTH)  localisation.y --;
        else if (ORIENTATION_LIST[currentOrientation] == WEST)   localisation.x --;
        else
            throw new RuntimeException("Internal error: Corrupted current orientation: " + this.currentOrientation);
    }


    /**
     * Undi the previous forward that has led to the input localisation depending on the current orientation.
     * Update the value of the input parameter position depending on the current orientation
     * @param localisation Input/output position to move forward.   This object is modified by this method.
     */
    public void backwardLocalisation(Point localisation)
    {
        if      (ORIENTATION_LIST[currentOrientation] == NORTH)  localisation.y --;
        else if (ORIENTATION_LIST[currentOrientation] == EAST)   localisation.x --;
        else if (ORIENTATION_LIST[currentOrientation] == SOUTH)  localisation.y ++;
        else if (ORIENTATION_LIST[currentOrientation] == WEST)   localisation.x ++;
        else
            throw new RuntimeException("Internal error: Corrupted current orientation: " + this.currentOrientation);
    }


    /**
     * @return  string representing the orientation following the user-input file-format: either Orientation.NORTH,
     *          Orientation.EAST,
     *          Orientation.SOUTH or
     *          Orientation.WEST
     */
    public String toString()
    {
        return "" + ORIENTATION_LIST[currentOrientation];
    }
}
