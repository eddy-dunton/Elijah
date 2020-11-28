import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

class LayoutTest {
	static JFrame frame = new JFrame();

	//Data
	static JPanel dataPanel = new JPanel();
	
	//View
	static JPanel viewPanel = new JPanel();
	
	//Info
	static JPanel infoPanel = new JPanel();

	//Laps
	static JList<String> lapList;
	static JScrollPane lapsPanel;
	
	static JSplitPane leftPane;
	static JSplitPane rightPane;
	static JSplitPane mainPane;

	static JButton bd, bv, bi, bl;

	public static void main(String[] args) {
		//Initalise panels
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);

		lapList = new JList<String>(new String[] {"Hello", "World", "!"});
		lapsPanel = new JScrollPane(lapList);

		//Set views
		leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dataPanel, viewPanel);
		leftPane.setResizeWeight(1);
		rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, lapsPanel, infoPanel);
		rightPane.setResizeWeight(1);
		mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		mainPane.setResizeWeight(1);

		frame.add(mainPane);

		viewPanel.setBorder(BorderFactory.createTitledBorder("View"));

		//Create components
		bd = new JButton("Data");
		bv = new JButton("View");
		bi = new JButton("Info");	
		bl = new JButton("Laps");

		//Add componets
		dataPanel.add(bd);
		viewPanel.add(bv);
		infoPanel.add(bi);
		lapsPanel.add(bl);

		//draw
		frame.setVisible(true);
		leftPane.setDividerLocation(0.75);
		rightPane.setDividerLocation(0.75);
		mainPane.setDividerLocation(0.75);
	}
}