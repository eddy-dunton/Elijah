package eddydunton.elijah;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

//Scroll pane used to navigate laps
class LapsPane extends JScrollPane implements ListSelectionListener{
	private static final long serialVersionUID = 1L;

	//List containing all laps for the current session
	private JList<Lap> list;

	public LapsPane(JList<Lap> list) {
		super(list);

		this.list = list;
		//Catchy function name I know
		this.list.addListSelectionListener(this);

		//Layout

		//Components
	}

	public void update(Lap[] laps) {
		this.list.setListData(laps);
	}

	//Selects the first value in the list
	public void resetSelection() {
		this.list.setSelectedIndex(0);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		//Gets currently selected lap
		Lap lap = this.list.getSelectedValue();
		//Checks that it is not already open
		if (lap != Client.lap && lap != null) Client.openLap(lap);
	}
}