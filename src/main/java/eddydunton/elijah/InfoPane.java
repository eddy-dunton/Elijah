package eddydunton.elijah;

import eddydunton.elijah.representation.Acceleration;
import eddydunton.elijah.representation.Speed;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

//Shows info on the current selected lap
class InfoPane extends JPanel {
	private static final long serialVersionUID = 1L;

	JTextArea text;

	public InfoPane() {
		super();
		//Layout
		this.setLayout(new BorderLayout());

		//Components
		this.text = new JTextArea();
		this.text.setEditable(false);

		this.setBorder("No lap selected");

		this.add(this.text, BorderLayout.WEST);
	}

	//Changes lap data when a lap is changed
	public void changeLap(Lap lap) {
		this.setBorder(lap.name);

		this.text.setText(String.join("\n",
			"Name:\t" + eddydunton.elijah.Client.session.NAME,
			"Top MPH:\t" + Speed.format(lap.topMPH),
			"Average MPH:\t" + Speed.format(lap.averageMPH),
			"Top m/S^2:\t" + Acceleration.format(lap.topAccel),
			"Duration:\t" + Session.formatDuration(lap.duration),
			"Start:\t" + Session.formatTime(lap.get(0).time),
			"End:\t" + Session.formatTime(lap.get(lap.size() - 1).time)));
	}

	//Resets
	//Used at start and when session is changed
	public void reset() {
		this.text.setText("");
		this.setBorder("No lap selected");
	}

	//Sets the border to title
	private void setBorder(String title) {
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title),
				new EmptyBorder(5, 5, 5, 5)));
	}
}