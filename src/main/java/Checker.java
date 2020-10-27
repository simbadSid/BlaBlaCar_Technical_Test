package main.java;

import main.java.tools.Debug;

import java.util.LinkedList;

public class Checker implements Runnable
{
    // ====================================
    // Attributes
    // ====================================
    private LinkedList<Mower> mowerList;


    // ====================================
    // Constructors
    // ====================================
    public Checker(LinkedList<Mower> mowerList)
    {
        this.mowerList = mowerList;
    }


    // ====================================
    // Public Methods
    // ====================================
    @Override
    public void run()
    {
//TODO check if the collision is on the initial position (throw appropriate exception)

        int         nbMowerAlive                = mowerList.size();
        boolean[]   mowerList_toCorrect         = new boolean[mowerList.size()];
        boolean[]   mowerList_EntirelyTreated   = new boolean[mowerList.size()];
        Position[]  iterationPositionList       = new Position[mowerList.size()];

        for (int i=0; i<mowerList_toCorrect.length; i++)
        {
            mowerList_toCorrect[i]          = false;
            mowerList_EntirelyTreated[i]    = false;
            iterationPositionList[i]        = null;
        }

        // For each iteration (Assume that all the previous iterations are safe)
        while( nbMowerAlive > 1)
        {
            Debug.INSTANCE.println(Debug.LOG_FLAG_CHECKER, "CHECKER " + this);

            // For each running mower (not completely checked)
            for (int i0=0, n=mowerList.size(); i0<n-1; i0++)
            {
                Debug.INSTANCE.println(Debug.LOG_FLAG_CHECKER, "\tCheck Mower " + i0);

                if (mowerList_EntirelyTreated[i0] == true)                   // Case mower0 has already been entirely treated
                    continue;

                Mower mower0 = mowerList.get(i0);
                if (iterationPositionList[i0] == null)
                    iterationPositionList[i0] = mower0.retrieveLastUncheckedPosition();
                if (iterationPositionList[i0] == null)                       // Case mower0 has finished and been entirely checked
                {
                    mowerList_EntirelyTreated[i0] = true;
                    nbMowerAlive --;
                    continue;
                }

                if (mowerList_toCorrect[i0])                                // Case mower0 has already been spotted wrong
                    continue;

                for (int i1=i0+1; i1<n; i1++)
                {
                    Debug.INSTANCE.println(Debug.LOG_FLAG_CHECKER, "\t\tCheck Mower " + i1);

                    if ((mowerList_EntirelyTreated[i1] == true) ||          // Case mower1 has finished and been entirely checked
                        (mowerList_toCorrect[i1] == true))                  // Case mower1 has already been spotted wrong
                        continue;

                    Mower mower1 = mowerList.get(i1);
                    if (iterationPositionList[i1] == null)
                        iterationPositionList[i1] = mower1.retrieveLastUncheckedPosition();
                    if (iterationPositionList[i1] == null)
                    {
                        mowerList_EntirelyTreated[i1] = true;
                        nbMowerAlive --;
                        continue;
                    }
                                                                            // Case no collision
                    if (! iterationPositionList[i0].isInCollision(iterationPositionList[i1]))
                        continue;
                                                                            // Case two mowers in the same cell
                                                                            //      Case Mower 1 is responsible
                    Debug.INSTANCE.print(Debug.LOG_FLAG_CHECKER, "\t\tCHECKER collision at iteration " + iterationPositionList[i1].getInstructionIndex());
                    if (iterationPositionList[i1].previousInstructionIsForward())
                    {
                        Debug.INSTANCE.print(Debug.LOG_FLAG_CHECKER, "Rewind " + i1);
                        mowerList_toCorrect[i1] = true;
                    }
                    else                                                    //      Case Mower 0 is responsible
                    {
                        Debug.INSTANCE.println(Debug.LOG_FLAG_CHECKER, "Rewind " + i0);
                        mowerList_toCorrect[i0] = true;
                        break;
                    }
                }
            }

            for (int i=0, n=mowerList.size(); i<n; i++)                     // Update all the spotted mowers
            {
                if (mowerList_toCorrect[i] == true)
                {
                    mowerList_toCorrect[i] = false;
                    Mower mower = mowerList.get(i);
                    mower.rewindSinceCollision(iterationPositionList[i]);
                }
                iterationPositionList[i] = null;

            }
        }

        // Join the thread of all the mowers
        for (Mower mower : mowerList)
        {
            try
            {
                mower.getThread().join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}