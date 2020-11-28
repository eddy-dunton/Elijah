package eddydunton.elijah.representation;

import eddydunton.elijah.Client;
import eddydunton.elijah.Lap;
import eddydunton.elijah.Point;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jxmapviewer.JXMapViewer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class LateralAcceleration extends Acceleration {
	
	public static String format(double a) {
		return String.format("%.3f", a);
	}
	
	@Override 
	public String toString() {
		return "Lat m/S^2";
	}
	
	@Override
	public String formatPoint(Point p) {
		return format(p.latAccel);
	}
	
	@Override
	public void drawLine(Graphics2D g, JXMapViewer map, List<Point> positions) {
		Color colour;
		double green;
		
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
	
			green = 1.0 - (pos.latAccel / max);
			colour = new Color((int) (green * 255), 255, (int) (green * 255));
			
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
		g.setPaint(new GradientPaint(25 + vp.x, 25 + vp.y, Color.GREEN, 25 + vp.x, 280 + vp.y, Color.WHITE));
		g.drawLine(25 + vp.x, 25 + vp.y, 25 + vp.x, 280 + vp.y);
		
		//Adds text, at 8 equal points across the bar
		g.setColor(eddydunton.elijah.Client.ctrlPane.getTagsColour());
		for (int o = 0; o <= 256; o += 32)
			g.drawString(Double.toString(max * ((256.0 - o) / 256.0)), 30 + vp.x, 25 + vp.y + o);
	}
	
	@Override
	public void openSession() {
		this.spMax.setValue(Math.ceil(Math.abs(Client.session.session.topLatAccel) * 10) / 10);
	}
	
	@Override
	public XYDataset generateDataset(Lap lap) {
		XYSeriesCollection collection = new XYSeriesCollection();
		XYSeries series = new XYSeries(this.toString());
		
		for (Point p : lap) {
			series.add(Time.secondsFromStart(lap, p), p.latAccel);
		}
		
		collection.addSeries(series);
		return collection;
	}
}
