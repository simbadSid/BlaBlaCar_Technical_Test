package test.java;

import junit.framework.TestCase;
import main.java.Simulation;
import main.java.tools.UserInput;

import java.io.*;

public class MainTest extends TestCase
{
    // ====================================
    // Attributes
    // ====================================
    public static final String  PATH_INPUT_FILE                     = "src/test/resources/input/";
    public static final String  PATH_OUTPUT_FILE                    = "src/test/resources/output/";


    // ====================================
    // Public tests
    // ====================================
    public void testMain_NoCollision_NoOutField_Mower2_X5_Y5_0() throws InterruptedException, IOException
    {
        assertTrue(compareInput_expectedOutput("NoCollision_NoOutField_Mower2_X5_Y5_0"));
    }

    public void testMain_NoCollision_NoOutField_Mower2_X5_Y5_1() throws InterruptedException, IOException
    {
        assertTrue(compareInput_expectedOutput("NoCollision_NoOutField_Mower2_X5_Y5_1"));
    }
/*
    public void testMain_Collision_OutField_Mower5_X5_Y5_0() throws InterruptedException, IOException
    {
        assertTrue(compareInput_expectedOutput("Collision_OutField_Mower5_X5_Y5_0"));
    }
*/
    public void testMain_Collision_OutField_Mower5_X5_Y5_1() throws InterruptedException, IOException
    {
        // Lunched without comparing it to a known solution
        runSimulation("Collision_OutField_Mower5_X5_Y5_1");
    }

    public void testMain_NoCollision_NoOutField_Mower3_X5_Y5_0() throws InterruptedException, IOException
    {
        assertTrue(compareInput_expectedOutput("NoCollision_NoOutField_Mower3_X5_Y5_0"));
    }

    public void testMain_Collision_NoOutField_Mower2_X5_Y5_0() throws InterruptedException, IOException
    {
        assertTrue(compareInput_expectedOutput("Collision_NoOutField_Mower2_X5_Y5_0"));
    }

    public void testMain_Collision_NoOutField_Mower2_X5_Y5_1() throws InterruptedException, IOException
    {
        assertTrue(compareInput_expectedOutput("Collision_NoOutField_Mower2_X5_Y5_1"));
    }


    // ====================================
    // Private methods
    // ====================================
    private String[] runSimulation(String inputFile) throws InterruptedException
    {
        String[] args = new String[2];
        File dir = new File(PATH_INPUT_FILE);
        assert(dir.isDirectory());

        args[0] = UserInput.USER_OPTION + UserInput.USER_OPTION_INPUTFILE;
        args[1] = PATH_INPUT_FILE + inputFile;

        // Run the simulation
        return Simulation.runSimulation(args);
    }
    private boolean compareInput_expectedOutput(String inputFile) throws InterruptedException, IOException
    {
        // Run the simulation
        String[] simulationResult = runSimulation(inputFile);

        //Compare the result with the expected result
        File            outputFile  = new File(PATH_OUTPUT_FILE + inputFile);
        FileReader      fileReader  = new FileReader(outputFile);
        BufferedReader  bufferReader= new BufferedReader(fileReader);
        for (String simulation : simulationResult)
        {
            if (! simulation.equals(bufferReader.readLine()))
                return false;
        }
        if (bufferReader.readLine() != null)
            return false;

        return true;
    }

}
