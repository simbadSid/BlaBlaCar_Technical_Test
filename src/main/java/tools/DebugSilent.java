package main.java.tools;

/**
 * Custom class to manage conditional (user-set) logging.
 * These functions are swapped out by the compiler (e.g. production-version of the code)
 *       and have no impact on the performance.
 * Their are much better alternative.   However, this lightweight class allows to only use standard libraries.
 */
public class DebugSilent extends Debug
{
    // ====================================
    // Local methods
    // ====================================
    @Override
    public void print(char flag, String s)
    {

    }


    @Override
    public void println(char flag, String s)
    {

    }


    @Override
    public void assertion(boolean b)
    {

    }
}
