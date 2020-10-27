package main.java.tools;


/**
 * Custom class to manage conditional (user-set) logging.
 * These functions are the version of the custom Debugger allowing to always print and run the tests.
 * Their are much better alternative.   However, this lightweight class allows to only use standard libraries.
 */
public class DebugLoud extends Debug
{
    // ====================================
    // Local methods
    // ====================================
    @Override
    public void print(char flag, String s)
    {
        if ((LOG_FLAG.contains(""+LOG_FLAG_ALL)) || (LOG_FLAG.contains(""+flag)))
            System.out.print(s);
    }


    @Override
    public void println(char flag, String s)
    {
        this.print(flag, "--" + s + "\n");
    }


    @Override
    public void assertion(boolean b)
    {
        assert(b);
    }
}
