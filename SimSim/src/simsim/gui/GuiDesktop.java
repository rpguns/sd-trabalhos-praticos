package simsim.gui;

import java.awt.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.image.*;


import javax.swing.*;

import simsim.core.*;
import simsim.utils.*;
import static simsim.core.Simulation.* ;

/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 */
@SuppressWarnings("serial")
public class GuiDesktop extends javax.swing.JFrame implements Runnable, Gui {

	static GuiDesktop gd ;
	private XY mouse = new XY(-1, -1);
	private javax.swing.JDesktopPane desktop;
	private volatile boolean needsRedraw = false;
	private GraphicsConfiguration graphicsConf ;

	
	private Map<String, IFrame> frames = new HashMap<String, IFrame>();
	
	public GuiDesktop() {
		gd = this ;
		setDefaultCloseOperation( javax.swing.WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));
		desktop = new javax.swing.JDesktopPane();
		getContentPane().add(desktop);
		
		desktop.setDesktopManager(new DefaultDesktopManager() {
			public void activateFrame(JInternalFrame f) {
				if (f != null)
					super.activateFrame(f);
			}

			// Control drag to force internal frames to appear inside the
			// desktop
			public void dragFrame(JComponent f, int x, int y) {
				// Only internal frames
				if (f instanceof JInternalFrame) {
					Dimension d = desktop.getSize();
					Dimension r = f.getSize();
					Insets s = f.getInsets();
					x = Math.max(0 - s.left, x);
					x = Math.min(x, d.width - r.width + s.right);
					y = Math.max(0, y);
					y = Math.min(y, d.height - r.height + s.bottom);
				}
				super.dragFrame(f, x, y - 3);
			}
		});		
		setTitle("SimSimulator") ;
		this.setPreferredSize( new Dimension( 1024, 768));
	}

	public void start() {
		
		setVisible(true);
		desktop.doLayout();
		pack();

		Threading.newThread( this, true).start();
		graphicsConf = this.getGraphicsConfiguration() ;
				
		while( ! this.isShowing() )
			Threading.sleep(500) ;
	}

	public XY getMouseXY() {
		return mouse;
	}

	public XY getMouseXY_Scaled( Graphics2D gs) {
		try {
			XY res = new XY();
			gs.getTransform().inverseTransform(mouse, res);
			return res;
		} catch (NoninvertibleTransformException e) {
		}
		return mouse;
	}

	public void addDisplayable(String frame, Displayable d, double fps ) {
		if (d != null) {
			IFrame f = frames.get(frame);
			if (f == null) {
				f = new IFrame(this, frame, fps);
				frames.put(frame, f);
				desktop.add(f, javax.swing.JLayeredPane.DEFAULT_LAYER);
			}
			f.addDisplayable(d);
		}
	}

	public void setFrameRectangle(String frame, int x, int y, int w, int h ) {
		IFrame f = frames.get(frame);
		if (f != null) {
			Insets is = f.getInsets() ;
			f.setBounds(new Rectangle( x - is.left, y - 4, w + is.left + is.right, h + is.top + is.bottom ));
		}
	}

	public void setFrameTransform(String frame, double virtualWidth, double virtualHeight, double offset, boolean keepRatios ) {
		IFrame f = frames.get(frame);
		if (f != null) {
			f.setFrameTransform( virtualWidth, virtualHeight, offset, keepRatios );
		}
	}
		
	void setMousePosition(int x, int y) {
		mouse = new XY(x, y);
	}

	

	public void redraw() {
		if (needsRedraw) {
			synchronized( this ) {
				double now = System.nanoTime() * 1e-9;
				for (IFrame i : frames.values())
					if (i.nextRedraw() < now)
						i.reDraw();
	
				needsRedraw = false;
				Threading.notifyOn(this) ;
			}
		}
	}

	public void run() {
		Threading.sleep(50);
		for (;;) {
			synchronized( this ) {
				while( needsRedraw )
					Threading.waitOn(this) ;
			}
			
			double now = System.nanoTime() * 1e-9;
			double nextDeadline = now + 0.1;
			for (IFrame i : frames.values())
				nextDeadline = Math.min(nextDeadline, i.nextRedraw());
			
			int delay = (int) (1000 * (nextDeadline - now));
			if (delay > 0)
				Threading.sleep(delay);
			else
				needsRedraw = true;
			
			if( Scheduler.isStopped() )
				redraw() ;
		}
	}	
	
	VolatileImage createVImage( int w, int h ) {		
		return graphicsConf == null ? null : graphicsConf.createCompatibleVolatileImage( w, h) ;
	}

}
