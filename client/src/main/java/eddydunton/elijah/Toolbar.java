package eddydunton.elijah;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Top toolbar, used for session independant controls such as open
class Toolbar extends JToolBar implements ActionListener {
	private static final long serialVersionUID = 1L;

	public Toolbar() {
		super("Toolbar");

		this.addButton("Open", "open");
	}

	//Called when a button is pressed
	@Override
	public void actionPerformed(ActionEvent action) {
		switch (action.getActionCommand()) {
			case "open":
				this.open();
				break;
		}
	}

	//Opens a new session
	private void open() {
		JFileChooser chooser = new JFileChooser();

		//Opens file chooser, with client frame as parent component
		int res = chooser.showOpenDialog(Client.frame);

		if (res == JFileChooser.APPROVE_OPTION) {
			//Starts a new session with the selected file
			Client.openSession(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	/**
	 * Adds a button to the toolbar with display text name
	 * Command is the action command string passed to actionPerformed()
	 * when the button is pressed
	 */
	private void addButton(String name, String command) {
		JButton button = new JButton(name);
		button.setActionCommand(command);
		button.addActionListener(this);
		this.add(button);
	}
}