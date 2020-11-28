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

public class Acceleration extends Representation {

	public static String format(double a) {
		return a > 0.0 ? String.format("+%.3f", a) : String.format("%.3f", a);
	}
	
	protected JSpinner spMax;
	
	public Acceleration() {
		super();
		// Label
		this.panel.add(new JLabel("Max"));

		// Max speed shown in this line
		this.spMax = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 20.0, 0.1));
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
		return "m/S^2";
	}

	@Override
	public String formatPoint(Point p) {
		//Add + to front of acceleration in case it is positive
		return format(p.accel);
	}

	@Override
	public void drawLine(Graphics2D g, JXMapViewer map, List<Point> positions) {
		Color colour;
		double red, green;
		
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
	
			//Acceleration in green, deceleration in red
			if (pos.accel > 0.0) {
				red = 1.0;
				green = 1.0 - Double.min(pos.accel / max, 1.0);
			} else {
				green = 1.0;
				red = 1.0 - Double.min(-pos.accel / max, 1.0);
			}
			colour = new Color((int) (255 * green), (int) (255 * red), (int) (255 * red * green));
			
			x = (int) point.getX();
			y = (int) point.getY();
			
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
		g.setPaint(new GradientPaint(25 + vp.x, 25 + vp.y, Color.GREEN, 25 + vp.x, 152 + vp.y, Color.WHITE));
		g.drawLine(25 + vp.x, 25 + vp.y, 25 + vp.x, 152 + vp.y);
		g.setPaint(new GradientPaint(25 + vp.x, 152 + vp.y, Color.WHITE, 25 + vp.x, 280 + vp.y, Color.RED));
		g.drawLine(25 + vp.x, 152 + vp.y, 25 + vp.x, 280 + vp.y);
		
		//Adds text, at 8 equal points across the bar
		g.setColor(eddydunton.elijah.Client.ctrlPane.getTagsColour());
		for (int o = 0; o <= 256; o += 32)
			g.drawString(Double.toString(max * ((128 - o) / 128.0)), 30 + vp.x, 25 + vp.y + o);
	}

	@Override
	public void openSession() {
		this.spMax.setValue(Math.ceil(Math.abs(eddydunton.elijah.Client.session.session.topAccel) * 10) / 10);
	}
	
	@Override
	public XYDataset generateDataset(Lap lap) {
		XYSeriesCollection collection = new XYSeriesCollection();
		XYSeries series = new XYSeries(this.toString());
		
		for (Point p : lap) {
			series.add(Time.secondsFromStart(lap, p), p.accel);
		}
		
		collection.addSeries(series);
		return collection;
	}
}
