package eddydunton.elijah.representation;

import eddydunton.elijah.CtrlPane;
import eddydunton.elijah.Lap;
import eddydunton.elijah.Point;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jxmapviewer.JXMapViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public abstract class Representation {
	protected JPanel panel;
	
	public boolean isValidForTags() {return true;}
	public boolean isValidForLine() {return true;}
	public boolean isValidForGraph() {return true;}
	
	public Representation() {
		this.panel = new JPanel();
		this.panel.setVisible(false);
		CtrlPane.setBorder(panel, "Options - " + this.toString(), 5);
	}
	
	/**
	 * Called when a session is opened, normally used to set max values
	 */
	abstract public void openSession();
	
	/**
	 * Enables or disables this representations line control panel
	 */
	public void setPanelVisible(boolean v) {
		this.panel.setVisible(v);
	}
	
	// Adds this representation to given pane, and adds given action listener to elements
	public void addToPane(JPanel pane, CtrlPane parent) {
		pane.add(panel);
	}
	
	public void drawTags(Graphics2D g, JXMapViewer map, List<Point> positions, int interval) {
		Point2D point;
		
		g.setColor(eddydunton.elijah.Client.ctrlPane.getTagsColour());
		
		// Ensures that the first position will always have the first tag
		int pointsUntilTag = 1;
		
		for (Point pos : positions) {
			point = map.getTileFactory().geoToPixel(pos, map.getZoom());
			
			if (pos.mph > eddydunton.elijah.Client.TAG_SPEED_CUTOFF && (--pointsUntilTag) == 0) {
				// Draw label
				g.fillRect((int) point.getX() - 1, (int) point.getY() - 1, 3, 3);
				// Perhaps replace with cumulative
				g.drawString(this.formatPoint(pos), (int) point.getX() + 5, (int) point.getY() + 5);
				
				// Reset points until tag and cumulative values
				pointsUntilTag = (interval + 1);
			}
		}
	}


	/**
	 * Generates an XY dataset for the given lap where the X is normally time and Y is this representation
	 * 
	 * Should only be called on dataset where isValidForGraph() is true
	 * 
	 * Returns null by default so that Representations which are not valid for graphs do not have to reimplement
	 * 
	 * @param lap The lap to generate the dataset for
	 * @return The complete XY series
	 */
	public XYDataset generateDataset(Lap lap) {return new XYSeriesCollection();}
	
	/**
	 * Returns the name of this representation
	 */
	@Override
	abstract public String toString();
	
	/**
	 * Gets this data representations data point from a point and formats into a string
	 * 
	 * @param p Point to extract data from
	 * @return Value as a string
	 */
	abstract public String formatPoint(Point pos); 
	
	/**
	 * Draws this data representations as a line on the map for given points
	 * 
	 * @param g Graphics interface to draw on
	 * @param map Map which this will be drawn on 
	 * @param positions List of points to draw the line between
	 */
	abstract public void drawLine(Graphics2D g, JXMapViewer map, List<Point> positions);
}
