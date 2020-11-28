package eddydunton.elijah.representation;

import eddydunton.elijah.CtrlPane;
import eddydunton.elijah.Point;
import org.jxmapviewer.JXMapViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Data Representation for when no data is being represented
 * Does not show tags, shows plain line
 * 
 * @author Eddy
 */
public class Blank extends Representation {
	
	private JComboBox<String> cbColour;
	
	public Blank() {
		super();

		this.panel.add(new JLabel("Colour"));
		this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
		
		// Colour choice box
		this.cbColour = new JComboBox<String>(CtrlPane.colours.keySet().toArray(new String[0]));
		this.cbColour.setActionCommand("line_change");
		this.panel.add(this.cbColour);
	}

	@Override
	public void addToPane(JPanel pane, CtrlPane parent) {
		super.addToPane(pane, parent);
		
		//Hooks up cbColour to the control panes listener
		this.cbColour.addActionListener(parent);
	}

	@Override
	public String toString() {
		return "Blank";
	}

	@Override
	public String formatPoint(Point pos) {
		return "ERROR: Format called on none";
	}

	@Override
	public void drawLine(Graphics2D g, JXMapViewer map, List<Point> positions) {
		// Gets colour from combo box
		g.setColor(CtrlPane.colours.get(this.cbColour.getSelectedItem()));
		
		// Set correct values for first point
		Point2D point = map.getTileFactory().geoToPixel(positions.get(0), map.getZoom());
		int lastX = (int) point.getX();
		int lastY = (int) point.getY();
		
		int x, y;
		
		// Iterates through points in the current lap
		for (Point pos : positions) {
			// Coords to pixel
			point = map.getTileFactory().geoToPixel(pos, map.getZoom());
		
			x = (int) point.getX();
			y = (int) point.getY();
			
			// Draws line
			g.drawLine(lastX, lastY, x, y);
			
			lastX = x;
			lastY = y;
		}
	}
	
	@Override
	//Does not draw tags
	public void drawTags(Graphics2D g, JXMapViewer map, List<Point> positions, int interval) {}

	@Override
	public void openSession() {}
}
