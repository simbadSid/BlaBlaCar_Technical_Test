package main.java;

import main.java.tools.Debug;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * TODO
 * @warning this class allows to have a position out of the field
 */
public class Position
{
    // ====================================
    // Attributes
    // ====================================
    public static final char INSTRUCTION_LEFT       = 'L';
    public static final char INSTRUCTION_RIGHT      = 'R';
    public static final char INSTRUCTION_FORWARD    = 'F';
    public static final char INSTRUCTION_NULL       = 'X';

    private static Point    upperRightCorner        = null;

    private Point           localisation            = null;
    private Orientation     orientation             = null;
    private char            previousInstruction     = INSTRUCTION_NULL; // Instruction that brought to this position
    private int             instructionIndex        = -1;


    // ====================================
    // Constructors
    // ====================================

    /**
     * Instantiate the current position to match the input string
     * @param upperRightCorner string representing the dimension of the lawn following the user-input file-format: "X Y"
     *                 where X and Y are the cartesian position
     * @param position string representing the position following the user-input file-format: "X Y O"
     *                 where X and Y are the cartesian position and O is the orientation (Orientation.NORTH,
     *                 Orientation.EAST, Orientation.SOUTH or Orientation.WEST)
     * @throws NumberFormatException or RuntimeException if the input string does not respect the format.
     */
    public Position(String upperRightCorner, String position)
    {
        Debug.INSTANCE.println(Debug.LOG_FLAG_POSITION, "Creating position: " + position);

        // Parse the upperRightCorner
        List<String> items = Arrays.asList(upperRightCorner.split("\\s* \\s*"));
        if (items.size() != 2)
            throw new RuntimeException("Error in the input upperRightCorner: " + upperRightCorner);
        Position.upperRightCorner = parseCoordinate(items.get(0), items.get(1));

        // Parse the position
        items = Arrays.asList(position.split("\\s* \\s*"));
        if (items.size() != 3)
            throw new RuntimeException("Error in the input position: " + position);
        this.localisation           = parseCoordinate(items.get(0), items.get(1));
        this.orientation            = new Orientation(items.get(2));
        this.previousInstruction    = INSTRUCTION_NULL;
        this.instructionIndex       = 0;
    }


    public Position(Position p)
    {
        this.set(p);
    }


    // ====================================
    // Public Methods
    // ====================================

    /**
     * Change the current internal position following the input instruction
     * @param instruction Instruction to follow.
     *                    Can be either one of Position.INSTRUCTION_LEFT,
     *                    Position.INSTRUCTION_RIGHT or Position.INSTRUCTION_FORWARD.
     * @throws RuntimeException if the input instruction is not followed
     */
    public void update(char instruction)
    {
        if      (instruction == INSTRUCTION_LEFT)     this.orientation.turnLeft();
        else if (instruction == INSTRUCTION_RIGHT)    this.orientation.turnRight();
        else if (instruction == INSTRUCTION_FORWARD)
        {
            this.orientation.forwardLocalisation(this.localisation);
            // Case wrong position: silently cancel it
            if (this.isWrongLocalisation())
                this.orientation.backwardLocalisation(this.localisation);
        }
        else
            throw new RuntimeException("Unhandled instruction: " + instruction);

        this.previousInstruction = instruction;
        this.instructionIndex++;
    }


    /**
     * Cancels the last forward instruction.
     * @warning If the last instruction was not "forward", the the behaviour is not ensured
     */
    public void backward()
    {
        Debug.INSTANCE.assertion(this.previousInstruction == INSTRUCTION_FORWARD);

        this.orientation.backwardLocalisation(localisation);
        this.previousInstruction = INSTRUCTION_NULL;    // Allows this instruction not to be successively canceled again
        this.instructionIndex--;

        Debug.INSTANCE.assertion(! isWrongLocalisation());
    }


    /**
     * @return  string representing the position following the user-input file-format: "X Y O"
     *                 where X and Y are the cartesian position and O is the orientation (Orientation.NORTH,
     *                 Orientation.EAST, Orientation.SOUTH or Orientation.WEST)
     */
    public String toString()
    {
        return "" + this.localisation.x + " " + this.localisation.y + " " + this.orientation;
    }


    public void set(Position p)
    {
        this.localisation           = new Point(p.localisation);
        this.orientation            = new Orientation(p.orientation);
        this.previousInstruction    = p.previousInstruction;
        this.instructionIndex       = p.instructionIndex;
    }


    public boolean isInCollision(Position position)
    {
        return this.localisation.equals(position.localisation);
    }


    public boolean previousInstructionIsForward()
    {
        return this.previousInstruction == INSTRUCTION_FORWARD;
    }


    public int getInstructionIndex()
    {
        return this.instructionIndex;
    }


    // ====================================
    // Private Methods
    // ====================================
    private static Point parseCoordinate(String coordinateX, String coordinateY)
    {
        try
        {
            return new Point (Integer.parseInt(coordinateX), Integer.parseInt(coordinateY));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Can't parse coordinate: (X=" + coordinateX + ", Y=" + coordinateY + ")");
        }

    }


    private boolean isWrongLocalisation()
    {
        return ((this.localisation.x < 0) ||
                (this.localisation.y < 0) ||
                (this.localisation.x > Position.upperRightCorner.x) ||
                (this.localisation.y > Position.upperRightCorner.y));
    }
}
