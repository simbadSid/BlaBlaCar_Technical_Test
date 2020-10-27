package main.java;

import main.java.tools.Debug;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantLock;

public class Mower implements Runnable
{
    // ====================================
    // Attributes
    // ====================================
    private String                          instructionList             = null;
    /**
     * Thread-safe queue of successive positions.
     * @warning This queue is filled with successive positions regardless of the other concurrent mowers.
     * Thus it may contain wrong or conflicting positions (that need to be addressed by the adequate threads).
     */
//    private ConcurrentLinkedQueue<Position> positionQueue               = null;
    private volatile BlockingQueue<Position>positionQueue               = null;

    /**
     * Thread-safe variable indicating the current position of the simulation
     * This variable is atomically updated by the method run and by the method rewindSinceCollision (from an other thread).
     */
    private volatile Position               lastPosition                = null;
    /**
     * Thread-safe variable indicating the current index within the instruction list
     * This variable is atomically updated by the method run and by the method rewindSinceCollision (from an other thread).
     */
    private volatile int                    currentInstructionIndex     = -1;

    /**
     * Lock used to always access in a critical section the attributes positionQueue, lastPosition and currentInstructionIndex
     */
    private final ReentrantLock             lock                        = new ReentrantLock();

    private String                          id                          = null;
    private Thread                          thread                      = null;

    // ====================================
    // Constructors
    // ====================================

    /**
     * Instantiate the mower object with initial position and instruction list
     * @param upperRightCorner string representing the dimension of the lawn following the user-input file-format: "X Y"
     *      where X and Y are the cartesian position
     * @param startingPosition string representing the starting position following the user input file format: "X Y O"
     *      where X and Y are the cartesian position and O is the orientation (Orientation.NORTH,
     *      Orientation.EAST, Orientation.SOUTH or Orientation.WEST).
     *      This input format is checked in this constructor.
     * @param instructionList List of instructions to be eventually received by the mower.
     *      The instructions of this list is not checked by this constructor.
     *      However, exceptions may be raised eventually by the run method
     * @throws NumberFormatException or RuntimeException if the input startingPosition does not respect the
     * user-file format.
     */
    public Mower(String upperRightCorner, String startingPosition, String instructionList, String id)
    {
        Debug.INSTANCE.println(Debug.LOG_FLAG_MOWER, "Creating the mower at the position " + startingPosition + " and with the instruction list: " + instructionList);

        this.instructionList            = new String(instructionList);
//        this.positionQueue              = new ConcurrentLinkedQueue<Position>();
        this.positionQueue              = new LinkedBlockingDeque<Position>();
        this.lastPosition               = new Position(upperRightCorner, startingPosition);
        this.currentInstructionIndex    = 0;
        this.id                         = new String(id);

        this.positionQueue.add(new Position(this.lastPosition));
    }


    // ====================================
    // Public Methods
    // ====================================
    @Override
    public void run()
    {
        int n=this.instructionList.length();

        while (this.currentInstructionIndex < n)
        {
            // Enter critical section
            this.lock.lock();
            char command = this.instructionList.charAt(this.currentInstructionIndex);

            Debug.INSTANCE.println(Debug.LOG_FLAG_MOWER, "MOWER " + this.id + ": Command " + command + "(" + this.currentInstructionIndex + ")" + " on position " + this.lastPosition);

            this.lastPosition.update(command);
            this.positionQueue.add(new Position(this.lastPosition));
            this.currentInstructionIndex ++;

            Debug.INSTANCE.println(Debug.LOG_FLAG_MOWER, "\tMOWER " + this.id + " +  -> " + this.lastPosition + "(" + this.currentInstructionIndex + " )");

            // Leave critical section
            this.lock.unlock();
        }
    }


    /**
     * THread-safe method designed
     * @param positionCollision Position that has been spoted as causing a collision
     */
    public void rewindSinceCollision(Position positionCollision)
    {
        Debug.INSTANCE.println(Debug.LOG_FLAG_MOWER, "Mower " + this.id + ": Collision detected by checker thread.");

        // Enter critical section
        this.lock.lock();

        boolean mowerHadFinished = (this.currentInstructionIndex == this.instructionList.length());
        this.positionQueue.clear();
        this.lastPosition.set(positionCollision);
        this.lastPosition.backward();
        this.currentInstructionIndex = positionCollision.getInstructionIndex() + 1;   //TODO check the +1
        this.positionQueue.add(this.lastPosition);

        // Check if the current thread had finished -> re create it
        if (mowerHadFinished)
        {
            Debug.INSTANCE.println(Debug.LOG_FLAG_MOWER, "Mower " + this.id + ": Relaunch thread.");
            this.thread = new Thread(this);
            this.thread.start();
        }

        // Leave critical section
        this.lock.unlock();

        Debug.INSTANCE.println(Debug.LOG_FLAG_MOWER, "\tMower " + this.id + ": Return to last position: " + currentInstructionIndex);

    }


    /**
     * Retrieve the last unchecked position from the thread-safe positionQueue.
     * @return null if the queue is empty and the mower has computed all the instruction.
     * If the queue is empty but the mower has not finished, then this method blocks until a new position is computed
     */
    public Position retrieveLastUncheckedPosition()
    {
        //TODO try to remove this syncrhonization (by reordering the accesses to the queue and this.currentInstructionIndex in this function and in run)

        // Enter critical section
        this.lock.lock();

        if ((this.positionQueue.size() == 0) &&                                 // Case mower has finished and
            (this.currentInstructionIndex == this.instructionList.length()))    //      all positions have been treated
        {
            this.lock.unlock();
            return null;
        }

        // Leave critical section
        this.lock.unlock();

        try
        {
            return this.positionQueue.take();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Non thread safe function designed to return the last computed position
     * This function should not be called before the mower thread is not over or
     *      before the checker thread has not checked all the computed positions.
     * @return Return a string representing the last computed result.
     */
    public String lastPosition()
    {
        return "" + this.lastPosition;
    }

    public void setThread(Thread t)
    {
        this.thread = t;
    }


    public Thread getThread()
    {
        return this.thread;
    }
}