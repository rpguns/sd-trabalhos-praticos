package simsim.gui;

import java.awt.*;

import simsim.core.*;
import simsim.utils.*;

/**
 * This interface contains the operations supported by the GUI of the simulator.
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public interface Gui {

	/**
	 * Gets the coordinates of the mouse pointer.
	 * @return the (x,y) coordinates of the mouse pointer.
	 */
	public XY getMouseXY() ;

	/**
	 * 
	 * Gets the coordinates of the mouse pointer transformed to a given graphics context object.
	 * @param gs A graphics context that contains the transformation to apply. 
	 * @return the (x,y) coordinates of the mouse pointer.
	 */
	public XY getMouseXY_Scaled( Graphics2D gs) ;
	
	/**
	 * Adds a displayable item to the named internal frame. The frame is created as needed.
	 * @param frame The name of the frame.
	 * @param d The new displayable item.
	 * @param fps The desired framerate.
	 */
	public void addDisplayable(String frame, Displayable d, double fps) ;

	/**
	 * Adds a displayable item to the named internal frame and sets its position and size.
	 * The frame is created as needed.
	 * @param frame The name of the internal frame.
	 * @param x The x coordinate of the frame window in the GUI desktop.
	 * @param y The y coordinate of the frame window in the GUI desktop.
	 * @param w The width of the frame window.
	 * @param h The height of the frame window.
	 */
	public void setFrameRectangle(String frame, int x, int y, int w, int h ) ;

	/**
	 * Set the scaling transform use to scale (and translate) the graphics context of the frame.
	 * @param frame The name of the frame.
	 * @param virtualWidth The virtual width of the frame.
	 * @param virtualHeight The virtual height of the frame.
	 * @param offset A diagonal offset (translation) measure as a percentage.
	 * @param keepRatios A flag indicating if the scaled display are must keep the aspect ratio.
	 */
	public void setFrameTransform(String frame, double virtualWidth, double virtualHeight, double offset, boolean keepRatios ) ;
}
