package eddydunton.elijah.representation;

import eddydunton.elijah.Client;
import eddydunton.elijah.Lap;
import eddydunton.elijah.Point;
import eddydunton.elijah.Session;
import org.jxmapviewer.JXMapViewer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class Time extends Representation {
	@Override
	public boolean isValidForLine() {return false;}
	@Override
	public boolean isValidForGraph() {return false;}
	
	public static String format(LocalTime time) {
		return Session.formatTime(time);
	}
	
	public static float secondsFromStart(Lap l, Point p) {
		Duration diff = Duration.between(l.startTime(), p.time);
		return diff.getSeconds() + (diff.getNano() / 1000000000);
	}
	
	@Override
	public void openSession() {}

	@Override
	public String toString() {
		return "Time";
	}

	@Override
	public String formatPoint(Point pos) {
		return Session.formatDuration(Duration.between(eddydunton.elijah.Client.lap.startTime(), pos.time));
	}
	
	@Override
	public void drawLine(Graphics2D g, JXMapViewer map, List<Point> positions) {
		// Time should not have a line
	}
	
	@Override
	public void drawTags(Graphics2D g, JXMapViewer map, List<Point> positions, int interval) {
		Point2D point;
		
		g.setColor(eddydunton.elijah.Client.ctrlPane.getTagsColour());
		
		// Ensures that the first position will always have the first tag
		int pointsUntilTag = 1;
		
		int pointsUntilText = 1;
		
		for (Point pos : positions) {
			point = map.getTileFactory().geoToPixel(pos, map.getZoom());
			
			pointsUntilText--;
			
			if (pos.mph > eddydunton.elijah.Client.TAG_SPEED_CUTOFF && (--pointsUntilTag) == 0) {
				// Draw label
				g.fillRect((int) point.getX() - 1, (int) point.getY() - 1, 3, 3);
				
				if (pointsUntilText <= 0) {
					// Perhaps replace with cumulative
					g.drawString(this.formatPoint(pos), (int) point.getX() + 5, (int) point.getY() + 5);
					pointsUntilText = Client.MINIMUM_TIME_TEXT_GAP;
				}
				
				// Reset points until tag and cumulative values
				pointsUntilTag = (interval + 1);
			}
		}
	}
}
