package simsim.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import simsim.core.*;

@SuppressWarnings("serial")
class IFrame extends JInternalFrame {

	private ImgPanel panel;
	private double requestedFrameRate ;
		
	IFrame( final GuiDesktop g, String title, double fps ) {		
		this.panel = new ImgPanel() ;
		this.requestedFrameRate = Math.min(50.0,  fps) ;
		
		this.setTitle(title) ;
		this.setClosable(true);
		this.setResizable(true);
		this.setMaximizable(true);
		panel.setPreferredSize( new Dimension(640, 640)) ;
				
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(new javax.swing.BoxLayout( getContentPane(), javax.swing.BoxLayout.LINE_AXIS));
		getContentPane().add( panel ) ;
		
		panel.addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent evt) {
				g.setMousePosition(Integer.MIN_VALUE, Integer.MIN_VALUE);
			}
		});
		
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent evt) {
				g.setMousePosition(evt.getX(), evt.getY());
			}

			public void mouseMoved(MouseEvent evt) {
				g.setMousePosition(evt.getX(), evt.getY());
			}
		});

		panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
            	forceRedraw();
            }
        });	
		this.setVisible(true);
		this.pack();
	}
			
	void addDisplayable( Displayable d ) {
		panel.addDisplayable( d ) ;
	}
	
	public void setBounds( Rectangle r ) {
		super.setBounds(r) ;
		panel.setPreferredSize( r.getSize() ) ;
		this.invalidate() ;
	}
	
	void setFrameTransform( double virtualWidth, double virtualHeight, double offset, boolean keepRatios ) {
		panel.setTransform(virtualWidth, virtualHeight, offset, keepRatios) ;
	}
		
		
	private void forceRedraw() {
		nextRedraw = 0 ;
	}
	
	public void reDraw() {
		double t0 = System.nanoTime() * 1e-9;
		panel.reDraw() ;
		double t1 = System.nanoTime() * 1e-9;
		avgRedrawDuration = 0.5 * avgRedrawDuration + 0.5 * (t1 - t0) ;
		double targetFrameRate = Math.min( requestedFrameRate, 0.75 / avgRedrawDuration ) ;
		nextRedraw = t1 + 1.0 / targetFrameRate ;
	}
	

	double nextRedraw() {
		return nextRedraw ;
	}
	
	private double nextRedraw = -1 ;
	private double avgRedrawDuration = 1.0 ;
}
