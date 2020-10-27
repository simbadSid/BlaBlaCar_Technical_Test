package main.java.tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Custom and lightweight classes designed to parse user inputs
 * There are some more efficient and user friendly implementations.   However, they require non standard
 *      libraries (e.g Appache CL)
 */
public class UserInput
{
    // ====================================
    // Attributes
    // ====================================
    private static final String     USER_OPTION_FIELD_NAME  = "USER_OPTION_";
    private static final String     USER_HELP_FIELD_NAME    = "USER_HELP_";
    public  static final String     USER_OPTION             = "--";

    public static final String      USER_OPTION_INPUTFILE   = "inputFile";
    public static final String      USER_OPTION_NBTHREAD    = "nbThread";
    public static final String      USER_OPTION_DEBUG       = "debug";
    public static final String      USER_OPTION_HELP        = "help";

    public static final String      USER_DEFAULT_INPUTFILE  = "src/test/resources/input/NoCollision_NoOutField_Mower2_X5_Y5_1";
    public static final int         USER_DEFAULT_NBTHREAD   = 3;
    public static final String      USER_DEFAULT_DEBUG      = "";
    public static final String      USER_DEFAULT_HELP       = "";

    public static final String      USER_HELP_INPUTFILE     = "<arg1> Path to the user input file.   Default = " + USER_DEFAULT_INPUTFILE;
    public static final String      USER_HELP_NBTHREAD      = "<arg1> Maximum number of threads to be deployed excluding the main (>= 1)   Default = " + USER_DEFAULT_NBTHREAD;
    public static final String      USER_HELP_DEBUG         = "Indicates weather or not to use a logger to print operations.   Default = " + USER_DEFAULT_DEBUG;
    public static final String      USER_HELP_HELP          = "Prints this help";

    private String[]                args;

    public String                   inputFile               = USER_DEFAULT_INPUTFILE;
    public int                      nbThread                = USER_DEFAULT_NBTHREAD;
    public String                   debug                   = USER_DEFAULT_DEBUG;
    public String                   help                    = USER_DEFAULT_HELP;


    // ====================================
    // Local Methods
    // ====================================
    public void parseUserInput(String[] args)
    {
        this.args = args;
//        Class cls = this.getClass();
        Method method;
        String parameter;
        int parameterPrefix = USER_OPTION.length();

        for (int parameterIndex=0; parameterIndex<args.length; parameterIndex++)
        {
            // Parse the input to find the corresponding function
            try
            {
                parameter = args[parameterIndex].substring(parameterPrefix);
                method = UserInput.class.getDeclaredMethod(parameter, Integer.class);
                parameterIndex = (int) method.invoke(this, parameterIndex);
            }
            catch (NoSuchMethodException | IllegalAccessException  e )
            {
                throw new RuntimeException("Unhandled parameter: " + args[parameterIndex] + "\n" + e);
            }
            catch (InvocationTargetException e)
            {
//                e.printStackTrace();
                throw new RuntimeException("Missing or wrong parameters for the option: " + args[parameterIndex] + "\n" + e);
            }
        }
    }


    public String toString()
    {
        String result = "";
        Field[] fieldList = this.getClass().getFields();

        for (Field field : fieldList)
        {
            if (!field.getName().startsWith(USER_OPTION_FIELD_NAME))
                continue;
            try
            {
                String attributeName    = (String) field.get(this);
                Object attributeValue   = this.getClass().getDeclaredField(attributeName).get(this);

                result += "\t" + attributeName + " : " + attributeValue + "\n";
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new RuntimeException("Internal error: " + e);
            }
        }

        return result;
    }


    // ====================================
    // Private Methods to answer to each user-parameter
    // ====================================
    /**
     * Method used to process the user option "--inputFile"
     * @param parameterIndex current index of the user option within args
     * @return The new index of the user option within args
     */
    private int inputFile(Integer parameterIndex) throws ExceptionMalformedInput
    {
        this.inputFile = parseParameter(args, parameterIndex+1);
//TODO check that the file exists and is readable
        return parameterIndex + 1;
    }


    /**
     * Method used to process the user option "--nbThread"
     * @param parameterIndex current index of the user option within args
     * @return The new index of the user option within args
     */
    private int nbThread(Integer parameterIndex) throws ExceptionMalformedInput
    {
        this.nbThread = parseParameterInt(args, parameterIndex+1);
//TODO check that the number is >=1
        return parameterIndex + 1;
    }


    /**
     * Method used to process the user option "--nbThread"
     * @param parameterIndex current index of the user option within args
     * @return The new index of the user option within args
     */
    private int debug(Integer parameterIndex) throws ExceptionMalformedInput
    {
        this.debug = parseParameter(args, parameterIndex+1);
        Debug.INSTANCE = new DebugLoud();
        Debug.INSTANCE.initLogFlag(this.debug);
        return parameterIndex + 1;
    }


    /**
     * Method used to process the user option "--help"
     * @param parameterIndex current index of the user option within args
     * @return The new index of the user option within args
     * @warning This functions call System.exit() after printing the user help
     */
    private int help(Integer parameterIndex) throws ExceptionMalformedInput
    {
        System.out.println("usage: ");

        Field[] fieldList = this.getClass().getFields();

        for (Field field : fieldList)
        {
            if (!field.getName().startsWith(USER_OPTION_FIELD_NAME))
                continue;

            try
            {
                String attributeName    = (String) field.get(this);
                String fieldName        = USER_HELP_FIELD_NAME + field.getName().substring(USER_OPTION_FIELD_NAME.length());      //USER_HELP_FIELD_NAME+attributeName;
                String help             = (String) this.getClass().getDeclaredField(fieldName).get(this);
                System.out.println("\t" + USER_OPTION + attributeName + ": \t" + help);
            }
            catch(Exception e)
            {
                throw new RuntimeException("Internal error." +
                        "  We forgot to declare a help for the user option " + field);
            }

        }
        System.exit(0);

        return parameterIndex + 1;
    }


    // ====================================
    // Private Methods
    // ====================================
    private String parseParameter(String[] args, int parameterIndex) throws ExceptionMalformedInput
    {
        if (parameterIndex >= args.length)
            throw new ExceptionMalformedInput("Missing value for the parameter: " + args[parameterIndex]);
        return args[parameterIndex];
    }


    private int parseParameterInt(String[] args, int parameterIndex) throws ExceptionMalformedInput
    {
        String result = parseParameter(args, parameterIndex);
        try
        {
            return Integer.parseInt(result);
        }
        catch (Exception e)
        {
            throw new ExceptionMalformedInput("Malformed value for the integer parameter " + args[parameterIndex] + ": " + result);
        }
    }


    private class ExceptionMalformedInput extends Exception
    {

        public ExceptionMalformedInput(String s)
        {
            super(s);
        }
    }
}
