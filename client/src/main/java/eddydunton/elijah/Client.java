package eddydunton.elijah;

import eddydunton.elijah.representation.*;

import javax.swing.*;

public class Client {
	// Main Frame
	static JFrame frame;

	public static final int WIDTH = 1600;
	public static final int HEIGHT = 1000;
	public static final int CTRL_HEIGHT = 225;
	// Init width of lap control pane
	public static final int LAP_WIDTH = 250;
	// Size of around point 0,0 which all GPS positions within will be thrown out
	public static final double GPS_ERROR_REGION = 5.0;
	// Minimum speed for a point to be considered for a tag
	public static final double TAG_SPEED_CUTOFF = 2.5;
	// Time will be displayed at least every time number of points
	public static final int MINIMUM_TIME_TEXT_GAP = 5;

	static final Blank repBlank = new Blank();
	static final Speed repSpeed = new Speed();
	static final Acceleration repAccel = new Acceleration();
	static final LateralAcceleration repLatAccel = new LateralAcceleration();
	static final Time repTime = new Time();

	// All used data representations
	static final Representation[] datareps = {repBlank, repSpeed, repAccel, repLatAccel, repTime};

	// Panes, each contains a different element of the viewer
	public static ViewPane viewPane;
	public static CtrlPane ctrlPane;
	public static InfoPane infoPane;
	public static LapsPane lapsPane;

	// Split panes used to manage the panes
	static JSplitPane leftPane;
	static JSplitPane rightPane;
	static JSplitPane mainPane;

	// Current Session
	public static Session session;
	public static Lap lap;

	public static void main(String[] args) {
		// Init frame
		frame = new JFrame("Elijah");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH, HEIGHT);

		// Init panes
		viewPane = new ViewPane();
		ctrlPane = new CtrlPane();
		infoPane = new InfoPane();
		lapsPane = new LapsPane(new JList<Lap>());

		// Sets up split panes
		leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, viewPane, ctrlPane);
		leftPane.setResizeWeight(1); // Gives top extra space when resized
		leftPane.setOneTouchExpandable(true); // Add show / hide buttons
		rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, lapsPane, infoPane);
		rightPane.setResizeWeight(1); // Gives top extra space when resized
		mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		mainPane.setResizeWeight(1); // Gives left extra space when resized

		frame.add(mainPane);

		// Sets divider location based on space needed to display ctrlPane
		frame.setVisible(true);
		leftPane.setDividerLocation(HEIGHT - CTRL_HEIGHT);
		rightPane.setDividerLocation(HEIGHT - CTRL_HEIGHT);
		mainPane.setDividerLocation(WIDTH - LAP_WIDTH);

		// Opens lap if put in args
		if (args.length > 0) openSession(args[0]);
	}

	// Opens up a session
	public static void openSession(String path) {
		session = Session.load(path);

		if (session == null) { // Checks session was loaded correctly
			System.out.println("Error loading session: invalid");
			return;
		}

		// Updates panes
		lapsPane.update(session.getLaps());
		viewPane.reset();
		infoPane.reset();
		ctrlPane.openSession();

		// Allow data reps to reset max values (or whatever)
		for (Representation rep : datareps) rep.openSession();

		// Resets laps so session is shown
		lapsPane.resetSelection();

		// Goto start of session
		viewPane.map.getMainMap().setCenterPosition(session.session.get(0));
	}

	// Called when the lap list is changed
	public static void updateLaps() {
		lapsPane.update(session.getLaps());
		lapsPane.resetSelection();
	}

	// Opens up a lap
	public static void openLap(Lap lap) {
		// Changes Panes to reflect new lap
		Client.lap = lap;
		viewPane.changeLap(lap);
		infoPane.changeLap(lap);
		ctrlPane.openLap();
	}
}