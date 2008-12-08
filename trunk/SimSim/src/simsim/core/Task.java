package simsim.core;

import java.awt.*;

import static simsim.core.Scheduler.*;

/**
 * 
 * This is the base class for creating tasks in the simulator.
 * 
 * Typically this class will be used to create anonymous classes by overriding run() to
 * place the code to be executed at given time in the future.
 *
 * By default, tasks execute only once. They can be re-scheduled (within run()) to execute
 * again at a later time.
 * 
 * Tasks can also be canceled to prevent them from executing.
 * 
 * Tasks are executed according to <b>simulation time</b>.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class Task implements Comparable<Task>, Displayable {
    
    double due ;
    public Color color ;
    public Object owner ;
    protected boolean isQueued = false ;
    protected boolean isCancelled = false ; 
    protected boolean reScheduled = false ;

    private int seqN = g_seqN++;
    private static int g_seqN = 0;

    /**
     * Creates an anonymous Task to execute "immediately".
     * The task will execute once, but 
     * can be re-scheduled to execute again at a given later time.
     */
    public Task() {
    	this(0 ) ;
    }
    
    /**
     * Creates an anonymous Task.
     * By default it executes once, when it is due.
     * Can be re-scheduled to execute again at a given later time.
     * 
     * @param due Number of seconds to wait until the task executes. 
     */
    public Task( double due ) {
    	this( null, due ) ;
    }

    /**
     * Creates an new Task.
     * By default it executes once, when it is due.
     * Can be re-scheduled to execute again at a given later time.
     * 
     * @param owner - Owner of the task, a node for certain in this case.
     * @param due Number of seconds to wait until the task executes. 
     */
    public Task( AbstractNode owner, double due ) {
    	this( owner, due, Color.gray ) ;
    }

    /**
     * Creates an new Task.
     * By default it executes once, when it is due.
     * Can be re-scheduled to execute again at a given later time.
     * 
     * @param owner - Owner of the task, a node for certain in this case.
     * @param due Number of seconds to wait until the task executes. 
     * @param color The color used to display the task in the Scheduler 
     */
    public Task( AbstractNode owner, double due, Color color ) {
    	this.color = color ;
    	this.owner = owner ;    	
    	if( owner != null ) owner.registerNodeTask(this ) ;
    	Scheduler.schedule( this, due) ;
    }

    /* (non-Javadoc)
     * 
     * This method should overriden in all concrete subtypes of this base class.
     * @see java.lang.Runnable#run()
     */
    public void run() {
    	System.err.println("Unexpected execution of Task.run() method.") ;
    	System.err.println("Override public void run() in parent class...") ;
    }
    
    /**
     * Tells the time when the task is to due to execute.
     * @return The time when the task is due to execute.
     */
    public double due() {
    	return due ;
    }
    
    /**
     * Cancels the tasks, preventing it from being executed again.
     */
    public void cancel() {
    	isCancelled = true ;
    	reScheduled = false ;
    }
    
    /**
     * Asks the task to be scheduled again to execute after the given delay. The period is maintained.
     * @param t The new deadline for next execution of this task.
     */
    public void reSchedule( double t ) {
    	Scheduler.reSchedule( this, t ) ;
    	reScheduled = true ;
    }

    /**
     * Tells if the task is scheduled for execution.
     * @return true if the task is scheduled for execution of false otherwise.
     */
    public boolean isScheduled() {
    	return this.isQueued ;
    }
    
    void reset() {
    	isQueued = false ;
    	reScheduled = false ;
    }
    
    void reSchedule() {
    }
    
    public int hashCode() {
    	return seqN ;
    }
    
    public boolean equals( Object other ) {
    	return other != null && equals( (Task) other ) ;
    }
    
    public boolean equals( Task other ) {
    	return other != null && this.seqN == other.seqN ;
    }

    public int compareTo( Task other) {
    	assert other != null ;
    	if( this.due == other.due ) return (this.seqN - other.seqN) ;
    	else return this.due < other.due ? -1 : 1 ;
    }
  
    public String toString() {
        return String.format("%d / %s / %s [%s, %s]", seqN, (owner == null ? "" : "" + owner.toString()), getClass(), isQueued, reScheduled) ;
    }
          
    public void display( Graphics2D gu, Graphics2D gs) {}
}

