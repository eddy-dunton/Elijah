package eddydunton.elijah.representation;

import eddydunton.elijah.CtrlPane;
import eddydunton.elijah.Lap;
import eddydunton.elijah.Point;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jxmapviewer.JXMapViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class Speed extends Representation {

	public static String format(double d) {
		return String.format("%.2f", d);
	}
	
	private JSpinner spMax;
	
	public Speed() {
		super();
		// Label
		this.panel.add(new JLabel("Max"));

		// Max speed shown in this line
		this.spMax = new JSpinner(new SpinnerNumberModel(0, 0, 200, 5));
		this.panel.add(this.spMax);
	}
	
	@Override
	public void addToPane(JPanel pane, CtrlPane parent) {
		super.addToPane(pane, parent);
		
		//Hooks up cbColour to the control panes listener
		this.spMax.addChangeListener(parent);
	}
	
	@Override
	public String toString() {
		return "MPH";
	}

	@Override
	public String formatPoint(Point p) {
		return  format(p.mph);
	}

	@Override
	public void drawLine(Graphics2D g, JXMapViewer map, List<Point> positions) {
		Color colour;
		double red;
		
		// Set up values for first point
		Point2D point = map.getTileFactory().geoToPixel(positions.get(0), map.getZoom());
		int lastX = (int) point.getX();
		int lastY = (int) point.getY();
		Color lastColour = Color.WHITE;
		double max = (double) this.spMax.getValue();
		
		int x, y;
		
		// Iterates through points in the current lap
		for (Point pos : positions) {
			// Coords to pixel
			point = map.getTileFactory().geoToPixel(pos, map.getZoom());
	
			// If not first (first will no last point and therefore cannot be drawn
			// draws line between this point and the last
			red = Double.min(1.0, pos.mph / max);
			colour = new Color((int) (255 * red), (int) (255 * (1.0 - red)), 0);
				
			x = (int) point.getX();
			y = (int) point.getY();
			
			// Sets the gradient
			g.setPaint(new GradientPaint(lastX, lastY, lastColour, x, y, colour));

			// Draws line
			g.drawLine(lastX, lastY, x, y);
		
	
			// Sets last line values to this line values
			lastX = x;
			lastY = y;
			lastColour = colour;
		}	
		
		//Draw gradient
		//Viewport data, needed for correct offset
		Rectangle vp = map.getViewportBounds();
		
		//Draws bar
		g.setStroke(new BasicStroke(5));
		g.setPaint(new GradientPaint(25 + vp.x, 25 + vp.y, Color.RED, 25 + vp.x, 280 + vp.y, Color.GREEN));
		g.drawLine(25 + vp.x, 25 + vp.y, 25 + vp.x, 280 + vp.y);
		
		//Adds text, at 8 equal points across the bar
		g.setColor(eddydunton.elijah.Client.ctrlPane.getTagsColour());
		for (int o = 0; o <= 256; o += 32)
			g.drawString(Double.toString(max * ((256 - o) / 256.0)), 30 + vp.x, 25 + vp.y + o);
	}

	@Override
	public void openSession() {
		this.spMax.setValue(Math.ceil(eddydunton.elijah.Client.session.session.topMPH));
	}
	
	@Override
	public XYDataset generateDataset(Lap lap) {
		XYSeriesCollection collection = new XYSeriesCollection();
		XYSeries series = new XYSeries(this.toString());
		
		for (Point p : lap) {
			series.add(Time.secondsFromStart(lap, p), p.mph);
		}
		
		collection.addSeries(series);
		return collection;
	}
}
