package simsim.core;

import java.awt.*;

/**
 * Interface used when the implementing object needs to be drawn in a simulator window.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public interface Displayable {

	/**
	 * Draws the implementing object in a simulator internal frame/window.
	 * @param gu Unscaled graphics context of the internal frame/window.
	 * @param gs Scaled graphics context of the internal frame/window. Using this context, shapes are scaled according to a transformation applied to the frame/window.
	 */
	public void display( Graphics2D gu, Graphics2D gs ) ;
	 
}
