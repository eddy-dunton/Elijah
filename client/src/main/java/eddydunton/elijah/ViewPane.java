package eddydunton.elijah;

import eddydunton.elijah.representation.Representation;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

//Main view pain, shows movement round the track
class ViewPane extends JSplitPane implements Painter<JXMapViewer> {
	private static final long serialVersionUID = 1L;

	public static HashMap<String, TileFactory> tileSets = new HashMap<String, TileFactory>();
	static {
		// Default tileset has to be added by instance
		// Not particularly elegant I know, sorry
		tileSets.put("VirtualEarth Satellite",
				new DefaultTileFactory(new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE)));
		tileSets.put("VirtualEarth Hybrid",
				new DefaultTileFactory(new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.HYBRID)));

	}

	public static final TickUnits GRAPH_TICKS = new TickUnits();
	static {
		//Set possible gridline gaps
		GRAPH_TICKS.add(new NumberTickUnit(60f));
		GRAPH_TICKS.add(new NumberTickUnit(30f));
		GRAPH_TICKS.add(new NumberTickUnit(15f));
		GRAPH_TICKS.add(new NumberTickUnit(10f));
		GRAPH_TICKS.add(new NumberTickUnit(5f));
		GRAPH_TICKS.add(new NumberTickUnit(1f));
	}

	public JXMapKit map;
	private ChartPanel graph;
	private RightClickMenu rcmenu;

	List<Point> positions;

	public ViewPane() {
		super(JSplitPane.VERTICAL_SPLIT);

		this.positions = new LinkedList<Point>();

		this.map = new JXMapKit();
		this.graph = this.createGraph();

		// Setup split settings
		this.setTopComponent(this.map);
		this.setBottomComponent(this.graph);
		this.setOneTouchExpandable(true); // Add show / hide buttons
		this.setResizeWeight(1); // Give all resize space to the map
		this.setDividerLocation(500);

		// Add default map to tileSets
		tileSets.put("OpenStreetMap", this.map.getMainMap().getTileFactory());

		// Centres map on home(ish)
		this.map.setCenterPosition(new GeoPosition(52.772031, -1.206196));

		this.rcmenu = new RightClickMenu(this.map.getMainMap());
		this.map.getMainMap().setComponentPopupMenu(this.rcmenu);

		// Layout
		this.map.setMiniMapVisible(false);
		this.map.getMainMap().setOverlayPainter(this);
	}

	// Resets after a session is closed
	public void reset() {
		this.positions.clear();
		this.repaint();
	}

	// Called when a lap is changed
	public void changeLap(Lap lap) {
		//keeps divider position as the graphs have a tendency to change it when updated
		int div = this.getDividerLocation();

		// converts Lap into a list of GeoPositions
		this.positions = new LinkedList<Point>(lap);

		this.graph = this.createGraph();
		this.setBottomComponent(this.graph);

		this.updateGraph(0, eddydunton.elijah.Client.ctrlPane.getGraphLeftRep());
		this.updateGraph(1, eddydunton.elijah.Client.ctrlPane.getGraphRightRep());

		this.setDividerLocation(div);

		this.repaint();
	}

	// Changes tileset, keeps centre p	osition
	public void changeTileset(String key) {
		GeoPosition pos = this.map.getCenterPosition();
		this.map.setTileFactory(tileSets.get(key));
		this.map.setCenterPosition(pos);
	}

	@Override
	public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
		g = (Graphics2D) g.create();

		// Centres the graphics around the current viewport
		Rectangle rect = map.getViewportBounds();
		g.translate(-rect.x, -rect.y);

		// Enables AA
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setStroke(new BasicStroke(Client.ctrlPane.getLineLapSize()));

		// No point calling draw if there's nowt to draw
		if (this.positions.size() > 0) {
			Client.ctrlPane.getLineLapRep().drawLine(g, map, this.positions);
			Client.ctrlPane.getTagsRep().drawTags(g, map, positions, Client.ctrlPane.getTagsInterval());
		}

		// Draw Divisor line

		// Checks that there is a session & line to draw
		if (Client.session != null && Client.session.divisor != null) {
			// sets colour and stroke according to ctrlPanel
			g.setColor(Client.ctrlPane.getLineDivColour());
			g.setStroke(new BasicStroke(Client.ctrlPane.getLineDivSize()));

			GeoPosition pos1 = new GeoPosition(Client.session.divisor.getX1(), Client.session.divisor.getY1());
			// converts coordinates of the position to pixels on the map
			Point2D point1 = map.getTileFactory().geoToPixel(pos1, map.getZoom());

			GeoPosition pos2 = new GeoPosition(Client.session.divisor.getX2(), Client.session.divisor.getY2());
			// converts coordinates of the position to pixels on the map
			Point2D point2 = map.getTileFactory().geoToPixel(pos2, map.getZoom());

			// Actually draws the line
			g.drawLine((int) point1.getX(), (int) point1.getY(), (int) point2.getX(), (int) point2.getY());

			//Calculate vector div1 -> div2
			double xvec = (point1.getX() - point2.getX());
			double yvec = (point1.getY() - point2.getY());
			//Calculate vector length
			double length = Math.sqrt((xvec * xvec) + (yvec * yvec));
			//factor needed to normalise vector
			double norm = 15 / length;
			//Normal x and y
			xvec *= norm;
			yvec *= norm;

			//Add text to line
			g.drawString("1", (int) (point1.getX() + xvec), (int) (point1.getY() + (yvec)));
			g.drawString("2", (int) (point2.getX() - xvec), (int) (point2.getY() - (yvec)));
		}

		g.dispose();
	}

	// Updates the graph, either when a new representation is needed or the lap has changed
	public void updateGraph(int dataset, Representation rep) {
		if (! rep.isValidForGraph()) return;

		//Add the dataset
		XYPlot plot = this.graph.getChart().getXYPlot();
		plot.setRangeAxis(dataset, new NumberAxis(rep.toString()));
		plot.setDataset(dataset, rep.generateDataset(eddydunton.elijah.Client.lap));

		plot.mapDatasetToRangeAxis(dataset, dataset);
	}

	//Creates the graph
	private ChartPanel createGraph() {
        //construct the plot
        XYPlot plot = new XYPlot();

        //customize the plot with renderers
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
        renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
        plot.setRenderer(0, renderer);

        renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
        renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
        renderer.setSeriesPaint(0, Color.BLUE);
        plot.setRenderer(1, renderer);

        // Set up time axis
 		ValueAxis domainAxis = new NumberAxis("Time");
 		domainAxis.setStandardTickUnits(GRAPH_TICKS);
 		domainAxis.setMinorTickMarksVisible(true);
 		domainAxis.setMinorTickCount(10);
 		plot.setDomainAxis(domainAxis);

        //generate the chart
        JFreeChart chart = new JFreeChart("", getFont(), plot, true);
        chart.setBackgroundPaint(Color.WHITE);
        chart.getLegend().setPosition(RectangleEdge.RIGHT);

 		return new ChartPanel(chart);
	}

	/**
	 * Popup menu, seen when the user right clicks on the map
	 *
	 * Also functions as a mouse listener to it's own open commands
	 *
	 * @author Eddy Dunton
	 *
	 */
	private class RightClickMenu extends JPopupMenu implements ActionListener, PopupMenuListener {
		private static final long serialVersionUID = -515168356575017895L;

		private JXMapViewer parent;

		private JMenuItem divPoint1, divPoint2;

		private java.awt.Point position;

		public RightClickMenu(JXMapViewer parent) {
			super("Map Options");

			this.divPoint1 = new JMenuItem("Set as divider point 1");
			this.divPoint1.setActionCommand("div1");
			this.divPoint1.addActionListener(this);

			this.divPoint2 = new JMenuItem("Set as divider point 2");
			this.divPoint2.setActionCommand("div2");
			this.divPoint2.addActionListener(this);

			this.add(this.divPoint1);
			this.add(this.divPoint2);

			this.addPopupMenuListener(this);
			this.parent = parent;
		}

		//Send data to viewpa
		@Override
		public void actionPerformed(ActionEvent e) {
			//Convert x and y to a geoposition
			GeoPosition pos = this.parent.convertPointToGeoPosition(this.position);
			//Figure out which divider point is being changed
			int changed = e.getActionCommand() == "div1" ? 1 : 2;
			//Pass through to ctrl pane
			Client.ctrlPane.setDividerCoords(changed, pos);
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			this.position = this.parent.getMousePosition();
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {}
	}
}