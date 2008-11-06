package simsim.gui;

import java.awt.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.image.*;

import javax.swing.*;

import simsim.core.*;

@SuppressWarnings("serial")
class ImgPanel extends JPanel {

	private boolean keepRatios = true;
	private double v_width = 1000, v_height = 1000, offset = 0.2;

	ImgPanel() {
		super();
		this.buffers = new VImage[2];
		for (int i = 0; i < buffers.length; i++)
			buffers[i] = new VImage(this);

		this.displayables = new HashSet<Displayable>();

	}

	public void reDraw() {
		Graphics2D[] gcs = gCS();
		this.renderDisplayables(gcs[0], gcs[1]);
		this.swapBuffers();
	}

	public void addDisplayable(Displayable d) {
		if (d != null)
			displayables.add(d);
	}

	public void removeDisplayable(Displayable d) {
		if (d != null)
			displayables.remove(d);
	}

	public void renderDisplayables(Graphics2D gu, Graphics2D gs) {
		for (Displayable i : displayables)
			i.display(gu, gs);
	}

	public void setTransform(double vWidth, double vHeight, double offset, boolean keepRatios) {
		this.v_width = vWidth;
		this.v_height = vHeight;
		this.offset = offset ;
		this.keepRatios = keepRatios ;
		
		for( VImage i : buffers )
			i.setTransform() ;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		backImage().drawTo((Graphics2D) g);
	}

	private void swapBuffers() {
		cb = (cb + 1) % 2;
		repaint();
	}

	private Graphics2D[] gCS() {
		buffers[cb].validate().clear();
		return buffers[cb].gcs;
	}

	private VImage backImage() {
		return buffers[(cb + 1) % 2].validate();
	}

	int cb = 0;
	VImage[] buffers;

	class VImage {

		ImgPanel panel;
		Graphics2D[] gcs;
		VolatileImage img;

		VImage(ImgPanel panel) {
			this.panel = panel;
			gcs = new Graphics2D[2];
		}

		VImage validate() {
			if (invalid()) {
				if (img != null)
					img.flush();

				img = GuiDesktop.gd.createVImage(panel.getWidth(), panel.getHeight());
				if (img != null)
					setTransform();
			} 
			return this ;
		}

		void drawTo(Graphics2D g) {
			g.drawImage(img, 0, 0, null);
		}

		void clear() {
			gcs[0].clearRect(0, 0, img.getWidth(), img.getHeight());
		}

		private boolean invalid() {
			return img == null || img.getWidth() != panel.getWidth() || img.getHeight() != panel.getHeight();
		}

		void setTransform() {
			if ( invalid() )
				validate();
			
			if (img != null) {
				double p_width = img.getWidth();
				double p_height = img.getHeight();

				double tx, ty, sx, sy, ww, hh;
				double p_aspectRatio = p_width / p_height;
				double v_aspectRatio = v_width / v_height;

				if (p_aspectRatio > v_aspectRatio) {
					hh = p_height * (1 - offset);
					ww = hh * (keepRatios ? v_aspectRatio : p_aspectRatio);
				} else {
					ww = p_width * (1 - offset);
					hh = ww / (keepRatios ? v_aspectRatio : p_aspectRatio);
				}

				sx = ww / v_width;
				sy = hh / v_height;
				tx = (p_width - ww) / 2;
				ty = (p_height - hh) / 2;

				gcs[0] = img.createGraphics();
				gcs[0].setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				gcs[1] = (Graphics2D) gcs[0].create();

				gcs[0].setTransform(I);
				gcs[1].setTransform(I);
				gcs[1].translate(tx, ty);
				gcs[1].scale(sx, sy);
			}
		}
	}

	protected Collection<Displayable> displayables;
	private static final AffineTransform I = new AffineTransform();
}
