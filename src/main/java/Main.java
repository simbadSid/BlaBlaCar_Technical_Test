package main.java;

import main.java.tools.Debug;


public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        String[] simulationResult = Simulation.runSimulation(args);

        Debug.INSTANCE.println(Debug.LOG_FLAG_MAIN, "===============\nResult:");

        for (int i=0,n=simulationResult.length; i<n; i++)
        {
            System.out.println(simulationResult[i]);
        }
    }
}
