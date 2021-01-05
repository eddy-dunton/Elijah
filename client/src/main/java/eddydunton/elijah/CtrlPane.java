package eddydunton.elijah;

import eddydunton.elijah.representation.Representation;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.*;

//Controls to change the view
public class CtrlPane extends JPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;

	private static final short border = 1;

	private static final String divTFFormat = "%.8f";

	// Preset Lap divide lines
	public static HashMap<String, Line2D> presets = new LinkedHashMap<String, Line2D>();
	// Preset colours for combo boxes
	public static HashMap<String, Color> colours = new LinkedHashMap<String, Color>();

	static {
		presets.put("Custom", null);
		presets.put("Test", new Line2D.Double(0.0, 0.0, 30.0, 0.0));

		colours.put("Black", Color.BLACK);
		colours.put("Blue", Color.BLUE);
		colours.put("Cyan", Color.CYAN);
		colours.put("Dark Gray", Color.DARK_GRAY);
		colours.put("Gray", Color.GRAY);
		colours.put("Green", Color.GREEN);
		colours.put("Light Gray", Color.LIGHT_GRAY);
		colours.put("Magenta", Color.MAGENTA);
		colours.put("Orange", Color.ORANGE);
		colours.put("Pink", Color.PINK);
		colours.put("Red", Color.RED);
		colours.put("White", Color.WHITE);
		colours.put("Yellow", Color.YELLOW);
	}

	// Sets the border of a panel
	public static void setBorder(JPanel panel, String title, int padding) {
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title),
				new EmptyBorder(padding, padding, padding, padding)));
	}

	// File Components
	private JPanel paFile, paFileInner;
	private LinkedList<Component> trFile;

	// Session Components
	private JPanel paSession;
	private LinkedList<Component> trSession;
	private JPanel paDivide, paDivideEntry, paDivide1, paDivide2, paDivideCtrl;
	private JTextField tfDivide1Lat, tfDivide1Long, tfDivide2Lat, tfDivide2Long;
	private DivPresetSelector divPresetSelecter;

	// View Components
	private JPanel paView, paLineLap, paLineLapOptionsGlobal, paLineDiv, paTileSet,	paTags, paGraph;
	private LinkedList<Component> trView;
	private JComboBox<String> cbLineDiv, cbTileSet, cbTagColour;
	private JComboBox<Representation> cbLineLapType, cbTagsType, cbGraphLeft, cbGraphRight;
	private JSpinner spLineDiv, spLineLapWidth, spTagsInt;

	public CtrlPane() {
		super();

		// Spreads the boxes out for better use of space
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));


		this.trFile = new LinkedList<Component>();
		this.trSession = new LinkedList<Component>();
		this.trView = new LinkedList<Component>();

		// Main File pane
		this.paFile = new JPanel();
			setBorder(this.paFile, "File", border);
			this.add(paFile);

			// Inner file pane, necessary to trick the layout into making
			// The file pane take up the right amount of space so it fits with
			// the other panes
			this.paFileInner = new JPanel();
			this.paFileInner.setLayout(new BoxLayout(this.paFileInner, BoxLayout.Y_AXIS));
			this.paFile.add(this.paFileInner);

			// Open session button
			this.addButton(this.paFileInner, this.trFile, "Open", "open");

			// Space between buttons
			this.paFileInner.add(Box.createRigidArea(new Dimension(0, 5)));

			// Close program button
			this.addButton(this.paFileInner, this.trFile, "Close", "close");

			// Space between buttons
			this.paFileInner.add(Box.createRigidArea(new Dimension(0, 5)));

			//Add dump button
			this.addButton(this.paFileInner, this.trSession, "Dump", "dump");

		// Main Session pane
		this.paSession = new JPanel();
			setBorder(this.paSession, "Session", border);
			this.paSession.setLayout(new BoxLayout(this.paSession, BoxLayout.Y_AXIS));
			this.add(paSession);

			// Divide panel, used for dividing up the lap
			this.paDivide = new JPanel();
				setBorder(this.paDivide, "Divide", border);
				// this.paDivide.setLayout(new BoxLayout(this.paDivide, BoxLayout.Y_AXIS));
				this.trSession.add(this.paDivide);
				this.paSession.add(this.paDivide);

				// Minimum size of the text fields
				Dimension tfMinSize = new Dimension(90, 20);

				// Pane for divide data entry fields
				this.paDivideEntry = new JPanel();
					this.paDivide.add(this.paDivideEntry);
					this.paDivideEntry.setLayout(new BoxLayout(this.paDivideEntry, BoxLayout.Y_AXIS));

					// Pane for divide point 1
					this.paDivide1 = new JPanel();
						setBorder(this.paDivide1, "Point 1", border);
						this.paDivideEntry.add(this.paDivide1);
						this.trSession.add(this.paDivide1);

						this.tfDivide1Lat = new JTextField();
						this.tfDivide1Lat.setPreferredSize(tfMinSize);
						this.trSession.add(this.tfDivide1Lat);
						this.paDivide1.add(this.tfDivide1Lat);

						this.tfDivide1Long = new JTextField();
						this.tfDivide1Long.setPreferredSize(tfMinSize);
						this.trSession.add(this.tfDivide1Long);
						this.paDivide1.add(this.tfDivide1Long);

					// Pane for divide point 2
					this.paDivide2 = new JPanel();
						setBorder(this.paDivide2, "Point 2", border);
						this.paDivideEntry.add(this.paDivide2);
						this.trSession.add(this.paDivide2);

						this.tfDivide2Lat = new JTextField();
						this.tfDivide2Lat.setPreferredSize(tfMinSize);
						this.trSession.add(this.tfDivide2Lat);
						this.paDivide2.add(this.tfDivide2Lat);

						this.tfDivide2Long = new JTextField();
						this.tfDivide2Long.setPreferredSize(tfMinSize);
						this.trSession.add(this.tfDivide2Long);
						this.paDivide2.add(this.tfDivide2Long);



			// Pane for division controls
			this.paDivideCtrl = new JPanel();
				this.paDivideCtrl.setLayout(new BoxLayout(this.paDivideCtrl, BoxLayout.Y_AXIS));
				this.paDivide.add(this.paDivideCtrl);

				// Custom divide button
				this.addButton(this.paDivideCtrl, this.trSession, "Divide", "divide");

				this.paDivideCtrl.add(Box.createRigidArea(new Dimension(0, 5)));

				// Used to pick lap dividers from presets
				this.divPresetSelecter = new DivPresetSelector(presets);
				this.paDivideCtrl.add(divPresetSelecter);
				this.trSession.add(divPresetSelecter);

		// Pane for view options
		this.paView = new JPanel();
			setBorder(this.paView, "View", border);
			// this.paView.setLayout(new BoxLayout(this.paView, BoxLayout.Y_AXIS));
			this.add(paView);

			// Panel for controlling lap line
			this.paLineLap = new JPanel();
				setBorder(this.paLineLap, "Lap Line", border);
				this.trView.add(this.paLineLap);
				this.paView.add(this.paLineLap);

				// Panel for the lap line's width spinner
				this.paLineLapOptionsGlobal = new JPanel();
					this.paLineLapOptionsGlobal.setLayout(new BoxLayout(this.paLineLapOptionsGlobal, BoxLayout.Y_AXIS));
					this.paLineLap.add(this.paLineLapOptionsGlobal);
					this.trView.add(this.paLineLapOptionsGlobal);

					Representation[] lineLapTypes = //Filter out reps which are not valid for lines
							Arrays.stream(Client.datareps).filter(x -> x.isValidForLine()).toArray(Representation[]::new);

					// Combo box for choosing the lap line's representation
					this.cbLineLapType = new JComboBox<Representation>(lineLapTypes);
					this.cbLineLapType.setActionCommand("line_type_change");
					this.cbLineLapType.addActionListener(this);
					this.trView.add(this.cbLineLapType);
					this.paLineLapOptionsGlobal.add(this.cbLineLapType);

					this.paLineLapOptionsGlobal.add(Box.createRigidArea(new Dimension(0, 5)));

					this.paLineLapOptionsGlobal.add(new JLabel("Width"));

					// Lap width spinner
					this.spLineLapWidth = new JSpinner(new SpinnerNumberModel(4, 0, 10, 1));
					this.spLineLapWidth.addChangeListener(this);
					this.paLineLapOptionsGlobal.add(this.spLineLapWidth);
					this.trView.add(this.spLineLapWidth);

					// Adds data reps
					for (Representation rep : lineLapTypes) {
						rep.addToPane(this.paLineLap, this);
					}
					//Set init selected
					this.cbLineLapType.setSelectedIndex(1);

			// Panel for lap divider line display settings
			this.paLineDiv = new JPanel();
				this.paLineDiv.setLayout(new BoxLayout(this.paLineDiv, BoxLayout.Y_AXIS));
				setBorder(this.paLineDiv, "Division Line", border);
				this.trView.add(this.paLineLap);
				this.paView.add(this.paLineDiv);

				this.paLineDiv.add(Box.createRigidArea(new Dimension(0, 5)));

				// Colour of divider line, defaults to blue
				this.cbLineDiv = new JComboBox<String>(colours.keySet().toArray(new String[0]));
				this.cbLineDiv.setActionCommand("line_change");
				this.cbLineDiv.addActionListener(this);
				this.cbLineDiv.setSelectedItem("Black");
				this.paLineDiv.add(this.cbLineDiv);
				this.trView.add(this.cbLineDiv);

				this.paLineDiv.add(Box.createRigidArea(new Dimension(0, 5)));

				this.paLineDiv.add(new JLabel("Width"));

				// Width of divider line
				this.spLineDiv = new JSpinner(new SpinnerNumberModel(4, 0, 10, 1));
				this.spLineDiv.addChangeListener(this);
				this.paLineDiv.add(this.spLineDiv);
				this.trView.add(this.spLineDiv);

			// Panel for changing tileset provider
			this.paTileSet = new JPanel();
				this.paTileSet.setLayout(new BoxLayout(this.paTileSet, BoxLayout.Y_AXIS));
				setBorder(this.paTileSet, "Tile set", border);
				this.trView.add(this.paTileSet);
				this.paView.add(this.paTileSet);

				this.paTileSet.add(Box.createRigidArea(new Dimension(0, 5)));

				this.paTileSet.add(new JLabel("Provider"));

				// Tileset selector
				this.cbTileSet = new JComboBox<String>(ViewPane.tileSets.keySet().toArray(new String[0]));
				this.cbTileSet.setActionCommand("tileset_change");
				this.cbTileSet.addActionListener(this);
				this.cbTileSet.setSelectedItem("OpenStreetMap");
				this.paTileSet.add(this.cbTileSet);
				this.trView.add(this.cbTileSet);

			// Panel for adjusting tag options
			this.paTags = new JPanel();
				setBorder(this.paTags, "Tags", border);
				this.paTags.setLayout(new BoxLayout(this.paTags, BoxLayout.Y_AXIS));
				this.trView.add(this.paTags);
				this.paView.add(this.paTags);

				// Combo box for choosing the tag's representation
				this.cbTagsType = new JComboBox<Representation>(
						//Filter out reps which are not valid for tags
						Arrays.stream(Client.datareps).filter(x -> x.isValidForTags()).toArray(Representation[]::new));
				this.cbTagsType.setActionCommand("line_change");
				this.cbTagsType.addActionListener(this);
				this.cbTagsType.setSelectedIndex(4);
				this.trView.add(this.cbTagsType);
				this.paTags.add(this.cbTagsType);

				this.paTags.add(Box.createRigidArea(new Dimension(0, 5)));

				this.paTags.add(new JLabel("Interval"));

				// Interval between tags
				this.spTagsInt = new JSpinner(new SpinnerNumberModel(9, 0, 100, 1));
				this.spTagsInt.addChangeListener(this);
				this.paTags.add(spTagsInt);
				this.trView.add(this.spTagsInt);

				this.paTags.add(Box.createRigidArea(new Dimension(0, 5)));

				this.paTags.add(new JLabel("Colour"));

				// Colour of tag text, defaults to blue
				this.cbTagColour = new JComboBox<String>(colours.keySet().toArray(new String[0]));
				this.cbTagColour.setActionCommand("line_change");
				this.cbTagColour.addActionListener(this);
				this.cbTagColour.setSelectedItem("Blue");
				this.paTags.add(this.cbTagColour);
				this.trView.add(this.cbTagColour);

			this.paGraph = new JPanel();
				setBorder(this.paGraph, "Graph", border);
				this.paGraph.setLayout(new BoxLayout(this.paGraph, BoxLayout.Y_AXIS));
				this.trView.add(this.paGraph);
				this.paView.add(this.paGraph);

				this.paGraph.add(new JLabel("Left Axis"));

				// Combo box for choosing the left axis's representation
				this.cbGraphLeft = new JComboBox<Representation>(
						//Filter out reps which are not valid for tags
						Arrays.stream(Client.datareps).filter(x -> x.isValidForGraph()).toArray(Representation[]::new));
				this.cbGraphLeft.setActionCommand("left_graph_axis_changed");
				this.cbGraphLeft.addActionListener(this);
				this.cbGraphLeft.setSelectedIndex(1);
				this.trView.add(this.cbGraphLeft);
				this.paGraph.add(this.cbGraphLeft);

				this.paGraph.add(Box.createRigidArea(new Dimension(0, 5)));

				this.paGraph.add(new JLabel("Right Axis"));


				// Combo box for choosing the left axis's representation
				this.cbGraphRight = new JComboBox<Representation>(
						//Filter out reps which are not valid for tags
						Arrays.stream(Client.datareps).filter(x -> x.isValidForGraph()).toArray(Representation[]::new));
				this.cbGraphRight.setActionCommand("right_graph_axis_changed");
				this.cbGraphRight.addActionListener(this);
				this.cbGraphRight.setSelectedIndex(2);
				this.trView.add(this.cbGraphRight);
				this.paGraph.add(this.cbGraphRight);


		// Set starting states
		this.state(this.paSession, this.trSession, false);
	}

	// Called when a button is pressed
	@Override
	public void actionPerformed(ActionEvent action) {
		switch (action.getActionCommand()) {
			case "open":
				this.chooseSession();
				break;

			case "close":
				System.exit(0);

			case "dump":
				this.dumpSession();
				break;

			case "divide":
				this.changeDivide();
				break;

			case "line_change": // causes the viewpane to repaint when owt is changed
				Client.viewPane.repaint();
				break;

			case "line_type_change":
				//Hide all line options
				for (Representation rep : Client.datareps)
					rep.setPanelVisible(false);
				//Set newly selected panel to visible
				((Representation) this.cbLineLapType.getSelectedItem()).setPanelVisible(true);
				Client.viewPane.repaint();
				break;

			case "tileset_change":
				Client.viewPane.changeTileset((String) this.cbTileSet.getSelectedItem());
				break;

			case "left_graph_axis_changed":
				//Check that there is a lap loaded first
				if (Client.lap == null) break;
				Client.viewPane.updateGraph(0, (Representation) this.cbGraphLeft.getSelectedItem());
				break;

			case "right_graph_axis_changed":
				//Check that there is a lap loaded first
				if (Client.lap == null) break;
				Client.viewPane.updateGraph(1, (Representation) this.cbGraphRight.getSelectedItem());
				break;

			default:
				System.out.println("Error: incorrect action command issued: " + action.getActionCommand());
		}
	}

	// Only called when spinner value is changed
	@Override
	public void stateChanged(ChangeEvent change) {
		Client.viewPane.repaint();
	}

	// Attempts to change the divide points according to values in tfDividexLxx
	private void changeDivide() {
		try { // Attempts to convert values into lat / long doubles
			Client.session.divide(Double.parseDouble(this.tfDivide1Lat.getText()),
					Double.parseDouble(this.tfDivide1Long.getText()), Double.parseDouble(this.tfDivide2Lat.getText()),
					Double.parseDouble(this.tfDivide2Long.getText()));

			this.formatDivisionFields();

			this.divPresetSelecter.reset();
			Client.viewPane.repaint();
		} catch (Exception e) {
			System.out.println("Error: Incorrect Lat / Long values entered");
		}
	}

	// Opens a new session
	private void chooseSession() {
		//Opens the a file chooser at the cwd
		JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));

		// Opens file chooser, with client frame as parent component
		int res = chooser.showOpenDialog(Client.frame);

		if (res == JFileChooser.APPROVE_OPTION) {
			// Starts a new session with the selected file
			Client.openSession(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	//Dumps the current session into a file in text format
	private void dumpSession() {
		//Opens the a file chooser at the cwd
		JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));

		//Ensure type is txt
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
		chooser.setAcceptAllFileFilterUsed(false);

		// Gets a save location
		int selection = chooser.showSaveDialog(Client.frame);

		//Stop if approve is not selected
		if (selection != JFileChooser.APPROVE_OPTION) return;

		String path = chooser.getSelectedFile().getAbsolutePath();
		if (!path.toLowerCase().endsWith(".txt")) { //Adds .txt extension if necessary
			path += ".txt";
		}

		//Gets dump
		String[] dump = eddydunton.elijah.Client.session.getDump();

		File file = new File(path);

		try {
			//Create file if it does not exist
			if (! file.exists()) file.createNewFile();

			FileWriter fw = new FileWriter(file);

			//Write all lines in dump to file
			for (String s : dump) fw.append(s + '\n');

			fw.close();


		} catch (IOException e) {
			System.out.println("ERROR: Occurred whilst dumping, perhaps the file is open elsewhere?");
		}
	}

	// Sets the correct states for when a session is opened
	public void openSession() {
		this.state(this.paSession, this.trSession, true);

		this.divPresetSelecter.reset();
	}

	// Sets the correct states for when a lap is opened
	public void openLap() {
		// Currently does nowt
	}

	// Returns the type of lap line to be drawn
	public Representation getLineLapRep() {
		return (Representation) this.cbLineLapType.getSelectedItem();
	}

	//Returns what type of tags should be drawn (or none)
	public Representation getTagsRep() {
		return (Representation) this.cbTagsType.getSelectedItem();
	}

	//Returns the representation for the left graph axis
	public Representation getGraphLeftRep() {
		return (Representation) this.cbGraphLeft.getSelectedItem();
	}

	//Returns the representation for the left graph axis
	public Representation getGraphRightRep() {
		return (Representation) this.cbGraphRight.getSelectedItem();
	}

	// Format division text fields
	public void formatDivisionFields() {
		this.tfDivide1Lat.setText(String.format(divTFFormat, Double.parseDouble(this.tfDivide1Lat.getText())));
		this.tfDivide1Long.setText(String.format(divTFFormat, Double.parseDouble(this.tfDivide1Long.getText())));
		this.tfDivide2Lat.setText(String.format(divTFFormat, Double.parseDouble(this.tfDivide2Lat.getText())));
		this.tfDivide2Long.setText(String.format(divTFFormat, Double.parseDouble(this.tfDivide2Long.getText())));
	}

	// Changes the values of the division text fields and format them
	public void formatDivisionFields(double newX1, double newY1, double newX2, double newY2) {
		this.tfDivide1Lat.setText(String.format(divTFFormat, newX1));
		this.tfDivide1Long.setText(String.format(divTFFormat, newY1));
		this.tfDivide2Lat.setText(String.format(divTFFormat, newX2));
		this.tfDivide2Long.setText(String.format(divTFFormat, newY2));
	}

	// Returns colour of lap divider line
	public Color getLineDivColour() {
		return colours.get(this.cbLineDiv.getSelectedItem());
	}

	// Returns width of lap line
	public int getLineLapSize() {
		return (int) this.spLineLapWidth.getValue();
	}

	// Returns width of lap divider line
	public int getLineDivSize() {
		return (int) this.spLineDiv.getValue();
	}

	//Returns number of points per tag
	public int getTagsInterval() {
		return (int) this.spTagsInt.getValue();
	}

	public Color getTagsColour() {
		return colours.get(this.cbTagColour.getSelectedItem());
	}

	public void setDividerCoords(int changed, GeoPosition pos) {
		if (changed == 1) {
			//Update divide point 1
			this.tfDivide1Lat.setText(String.format(divTFFormat, pos.getLatitude()));
			this.tfDivide1Long.setText(String.format(divTFFormat, pos.getLongitude()));

			//Update division line if divide point 2 is also set
			if (this.tfDivide2Lat.getText() != "" && this.tfDivide2Long.getText() != "") this.changeDivide();
		} else if (changed == 2) {
			//Update divide point 2
			this.tfDivide2Lat.setText(String.format(divTFFormat, pos.getLatitude()));
			this.tfDivide2Long.setText(String.format(divTFFormat, pos.getLongitude()));

			//Update division line if divide point 2 is also set
			if (this.tfDivide1Lat.getText() != "" && this.tfDivide1Long.getText() != "") this.changeDivide();
		}
	}

	// changes a panel and all of its components' state
	private void state(JPanel panel, List<Component> tracker, boolean state) {
		panel.setEnabled(state);
		for (Component element : tracker) {
			element.setEnabled(state);
		}
	}

	/**
	 * Adds a button to a given panel with display text name Command is the
	 * action command string passed to actionPerformed() when the button is
	 * pressed
	 */
	private void addButton(JPanel panel, List<Component> tracker, String name, String command) {
		JButton button = new JButton(name);
		button.setActionCommand(command);
		button.addActionListener(this);
		panel.add(button);
		tracker.add(button);
	}

	// Lap division preset selector
	class DivPresetSelector extends JComboBox<String> implements ActionListener {
		private static final long serialVersionUID = 1L;

		// Preset lap division lines
		private HashMap<String, Line2D> presets;

		public DivPresetSelector(HashMap<String, Line2D> presets) {
			super(presets.keySet().toArray(new String[0]));

			this.presets = presets;
			this.addActionListener(this);
		}

		// Resets box to custom
		public void reset() {
			this.setSelectedIndex(0);
		}

		// When a lap division is chosen
		@Override
		public void actionPerformed(ActionEvent e) {
			// Checks that selection was not custom (which is index 0)
			if (this.getSelectedIndex() != 0) {
				// Selected divider
				Line2D sel = this.presets.get(this.getSelectedItem());

				// Sets text in text fields
				Client.ctrlPane.formatDivisionFields(sel.getX1(), sel.getY1(), sel.getX2(), sel.getY2());

				Client.session.divide(sel);
			}
		}
	}
}