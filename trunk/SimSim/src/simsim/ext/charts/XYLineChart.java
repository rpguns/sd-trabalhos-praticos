package simsim.ext.charts;

import java.awt.*;
import java.util.*;
import java.awt.geom.*;

import org.jfree.chart.*;
import org.jfree.data.xy.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;

import simsim.core.*;
import static simsim.core.Simulation.* ;

/**
 * A convenience wrapper class for using simple XYLineChart charts of the JFreeChart package. 
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class XYLineChart implements Displayable {

	protected String name ;
	protected JFreeChart chart;
	protected XYSeriesCollection data;
	protected Map<String, XYSeries> series ;
	protected String xAxisLabel, yAxisLabel ;
	protected XYLineAndShapeRenderer renderer ;
	
	public XYLineChart( String frame, double fps, String xAxisLabel, String yAxisLabel ) {
		this.name = frame ;
		this.xAxisLabel = xAxisLabel ;
		this.yAxisLabel = yAxisLabel ;
		init();		
		Gui.addDisplayable(frame, this, fps) ;
		Gui.setFrameTransform(frame, 500, 500, 0, false) ;
	}

	public void setXRange( boolean auto, double min, double max ) {
		chart.getXYPlot().getDomainAxis().setRange( min, max);
		chart.getXYPlot().getDomainAxis().setAutoRange(auto);
	}

	public void setYRange( boolean auto, double min, double max ) {
		chart.getXYPlot().getRangeAxis().setRange( min, max);
		chart.getXYPlot().getRangeAxis().setAutoRange(auto);
	}

	public void setSeriesLinesAndShapes( String name, boolean visibleLines, boolean shapesVisible ) {
		int i = getSeriesIndex( name ) ;
		renderer.setSeriesLinesVisible(i, visibleLines);
		renderer.setSeriesShapesVisible(i, shapesVisible);
	}
	
	public XYSeries getSeries( String name ) {
		XYSeries s = series.get( name) ;
		if( s == null ) {			
			s = new XYSeries( name ) ;
			series.put( name, s) ;
			data.addSeries(s) ;
		}
		return s ;
	}
	
	private int getSeriesIndex( String name ) {
		XYSeries s = getSeries( name ) ;
		for( int i = 0 ; i < data.getSeriesCount() ; i++ )
			if( s == data.getSeries(i) ) return i ;
		return -1 ;
	}
	/**
	 * Initializes all the FreeChart stuff..
	 */
	public void init() {

		series = new HashMap<String, XYSeries>() ;
		
		data = new XYSeriesCollection();
		renderer = new XYLineAndShapeRenderer();		
		chart = ChartFactory.createXYLineChart( name, yAxisLabel, xAxisLabel, data, PlotOrientation.VERTICAL, true, false, false);
		chart.getXYPlot().setRenderer( renderer);
		chart.removeLegend() ;
	}

	public void display(Graphics2D gu, Graphics2D gs) {
		final Rectangle2D chartArea = new Rectangle2D.Double(0, 0, 500, 500);
		try {
			chart.draw(gs, chartArea, null, null);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

}