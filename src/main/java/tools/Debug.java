package main.java.tools;

/**
 * Custom class to manage conditional (user-set) debugging utilities.
 * If user chooses not to log (e.g. production-version of the code), these functions are swapped out by the compiler
 *      and have no impact on the performance.
 * Their are much better alternative.   However, this lightweight class allows to only use standard libraries.
 */
public abstract class Debug
{
    // ====================================
    // Attributes
    // ====================================
    public static final char    LOG_FLAG_ALL        = '+';
    public static final char    LOG_FLAG_MAIN       = 'i';
    public static final char    LOG_FLAG_MOWER      = 'm';
    public static final char    LOG_FLAG_CHECKER    = 'c';
    public static final char    LOG_FLAG_POSITION   = 'p' ;

    public static Debug         INSTANCE            = new DebugSilent() ;
    protected static String     LOG_FLAG            = "" ;


    // ====================================
    // Local methods
    // ====================================
    public static void initLogFlag(String logFlag)
    {
        Debug.LOG_FLAG = new String(logFlag);
    }


    public abstract void print(char flag, String s);

    public abstract void println(char flag, String s);

    public abstract void assertion(boolean b);
}
