package simsim.core;

import java.awt.*;
import static simsim.core.Scheduler.Scheduler;

/**
 * 
 * This is the base class for creating asynchronous periodic tasks in the simulator.
 * 
 * Typically this class will be used to create anonymous classes by overriding run() to
 * place the code to be executed at given times, in the future.
 *
 * Periodic tasks repeat execution with a given period (frequency) until canceled. 
 * 
 * They can be re-scheduled (within run()) to execute again at a later time.
 * 
 * Periodic tasks can be canceled to prevent them from executing any further.
 * 
 * Periodic tasks are executed according to <b>simulation time</b>.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class PeriodicTask extends Task {
    
    protected final double period ;
    
    /**
     * Creates an anonymous task that is automatically scheduled to run with a given frequency/period.
     * @param period The period of time between successive executions.
     */
    public PeriodicTask( double period) {
        this(0, period) ;
    }
    
    /**
     * Creates an anonymous task that is automatically scheduled to run with a given frequency/period.
     * @param due The number of seconds before this task executes for the first time.
     * @param period The period of time between successive executions.
     */
    public PeriodicTask( double due, double period) {
    	this( null, due, period) ;
    }
    
    /**
     * Creates a "named" task that is automatically scheduled to run with a given frequency/period. 
     * When a node is disposed all of its "named" tasks are canceled as well. 
     * @param owner The node that issued this task
     * @param period The period of this task.
     */
    public PeriodicTask( AbstractNode owner, double period) {
        this(owner, 0, period) ;
    }
    
    /**
     * Creates a "named" task that is automatically scheduled to run with a given frequency/period. 
     * When a node is disposed all of its "named" tasks are canceled as well. 
     * @param owner The node that issued this task
     * @param due The number of seconds before this task executes for the first time.
     * @param period The period of this task.
     */
    public PeriodicTask( AbstractNode owner, double due, double period) {
        super(owner, due, Color.blue ) ;
        this.period = period ;
    }
    
    void reSchedule() {
       if( ! reScheduled && ! isCancelled )
    	   super.reSchedule( period ) ;
    }
    
    /* (non-Javadoc)
     * 
     * Asks the task to be scheduled again to execute after the given delay. The period is maintained.
     * 
     * @see simsim.core.Task#reSchedule(double)
     */
    public void reSchedule( double t ) {
    	Scheduler.reSchedule( this, t ) ;
    	reScheduled = true ;
    }
    
}
