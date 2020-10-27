package main.java;

import main.java.Checker;
import main.java.tools.Debug;
import main.java.tools.UserInput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        String[] simulationResult = runSimulation(args);

        Debug.INSTANCE.println(Debug.LOG_FLAG_MAIN, "===============\nResult:");

        for (int i=0,n=simulationResult.length; i<n; i++)
        {
            System.out.println(simulationResult[i]);
        }
    }


    /**
     * Run the simulation following the instructions within the input args
     * @param args User input argument list
     * @return The list of final position of each mower
     * @throws InterruptedException
     */
    public static String[] runSimulation(String[] args) throws InterruptedException
    {
        // Parse the user input
        UserInput userInput = new UserInput();
        userInput.parseUserInput(args);
        Debug.INSTANCE.println(Debug.LOG_FLAG_MAIN, "===============\n" + userInput.toString());

        File                file;
        FileReader          fileReader;
        BufferedReader      bufferReader;
        String              startingPosition, instructionList, upperRightCorner;
        LinkedList<Mower>   mowerList = new LinkedList<Mower>();

        // Parse the user file
        try
        {
            file        = new File(userInput.inputFile);
            fileReader  = new FileReader(file);
            bufferReader= new BufferedReader(fileReader);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error while opening the user input file: " + userInput.inputFile);
        }

        // Read Upper right corner
        try
        {
            upperRightCorner = bufferReader.readLine();
            if (upperRightCorner == null) throw new Exception();
            Debug.INSTANCE.println(Debug.LOG_FLAG_MAIN, "Parsing the upper right corner: " + upperRightCorner);

        }
        catch(Exception e)
        {
            throw new RuntimeException("Error while reading the user input file: " + userInput.inputFile);
        }


        // For each mower in the file
        int mowerId = 0;
        while(true)
        {
            try
            {
                startingPosition = bufferReader.readLine();
                if (startingPosition == null)   break;

                instructionList = bufferReader.readLine();
                if (instructionList == null)    throw new Exception();

                Debug.INSTANCE.println(Debug.LOG_FLAG_MAIN, "Parsing the mower at position " + startingPosition + ": " + instructionList);
            }
            catch(Exception e)
            {
                throw new RuntimeException("Error while reading the user input file: " + userInput.inputFile);
            }

            // Create and launch the mower
            Mower   mower   = new Mower(upperRightCorner, startingPosition, instructionList, ""+mowerId);
            Thread  t       = new Thread(mower);
            t.start();
            mower.setThread(t);
            mowerList.add(mower);
            mowerId++;
        }

        // Launch the assynchronous checker
        Checker checker = new Checker(mowerList);
        Thread  tChecker= new Thread(checker);
        tChecker.start();

        try
        {
            fileReader.close();
        }
        catch(Exception e)
        {
            throw new RuntimeException("Error while closing the user input file: " + userInput.inputFile);
        }

        // Wait for all the mowers to finish and be checked
        tChecker.join();

        //Retrieve the results
        String[] result = new String[mowerList.size()];
        for (int i=0,n=mowerList.size(); i<n; i++)
        {
            result[i] = mowerList.get(i).lastPosition();
        }

        return result;
    }
}
