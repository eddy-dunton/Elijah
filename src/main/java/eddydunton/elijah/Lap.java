package eddydunton.elijah;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.OptionalDouble;

public class Lap extends ArrayList<Point> {
	private static final long serialVersionUID = 1L;

	// Name of the lap, shown when laps are listed
	public String name;

	// Duration of lap
	public final Duration duration;

	public final double averageMPH, topMPH, topAccel, topLatAccel;

	// Main lap constructor
	public Lap(Collection<? extends Point> c) {
		super(c);

		this.duration = Duration.between(this.get(0).time, this.get(this.size() - 1).time);

		this.name = "";

		// Gets max as a optional double
		OptionalDouble max = this.stream().mapToDouble(x -> x.mph).max();
		// gets top mph if max has a value
		this.topMPH = max.isPresent() ? max.getAsDouble() : 0.0;

		//Same for acceleration (although it finds the most extreme)
		max = this.stream().mapToDouble(x -> x.accel).max();
		OptionalDouble min = this.stream().mapToDouble(x -> x.accel).min();
		double realMax = max.isPresent() ? max.getAsDouble() : 0.0;
		double realMin = min.isPresent() ? min.getAsDouble() : 0.0;
		this.topAccel = realMax > Math.abs(realMin) ? realMax : realMin;

		//And for lateral acceleration
		max = this.stream().mapToDouble(x -> x.latAccel).max();
		this.topLatAccel = max.isPresent() ? max.getAsDouble() : 0.0;


		// if statement to avoid divide by 0
		if (this.size() > 0) {
			this.averageMPH = (float) this.stream().mapToDouble(x -> x.mph).sum() / this.size();
		} else {
			this.averageMPH = 0;
		}
	}

	public Lap(Collection<? extends Point> c, String name) {
		this(c);

		this.name = name;
	}

	// name as shown in the laps pane
	@Override
	public String toString() {

		// formats and returns
		return String.format("%s - %s", this.name, Session.formatDuration(this.duration));
	}

	//Time utils

	//Returns the time this lap started
	public LocalTime startTime() {
		return this.get(0).time;
	}

	//Returns the time this lap ended
	public LocalTime endTime() {
		return this.get(this.size() - 1).time;
	}
}
