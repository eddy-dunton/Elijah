package eddydunton.elijah;

import eddydunton.elijah.representation.Acceleration;
import eddydunton.elijah.representation.LateralAcceleration;
import eddydunton.elijah.representation.Speed;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedList;

public class Session {
	// Loads a level from a given path
	// Returns null if path not valid or an error occurred during reading the
	// file
	public static Session load(String path) {
		LinkedList<Point> points = new LinkedList<Point>();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmmssSS");

		// Lazy I know
		try {
			if (path.toLowerCase().endsWith(".ltel")) { // legacy support
				// Reads lines from file
				LinkedList<String> lines = new LinkedList<String>();
				BufferedReader reader = new BufferedReader(new FileReader(path));
				String line;
				while (true) {
					line = reader.readLine();
					if (line == null)
						break;
					lines.add(line);
				}
				// last line will probably be cut off, thus remove it
				lines.pollLast();

				LocalTime time_;
				double lat, lng, mph, accel, angle, b, o, j, l, latAccel;
				Point newPoint = null; //Will actually be the last point for most of the function
				Point lastPoint = null; //Will actually be the last after newPoint for most of the function

				// process lines
				for (String data : lines) {
					// Splits data into {time, long, lat}
					String[] split = data.split(":");

					String time = split[0];

					// Add extra zeros to start in case time isn't long enough
					// (this causes the date / time formatter to throw a fit)
					if (time.length() < 8) {
						// Gets original time
						// Creates char array for 0s
						char[] zeros = new char[8 - time.length()];
						// adds zeros to array
						Arrays.fill(zeros, '0');
						time = new String(zeros) + time;
					}

					time_ = LocalTime.parse(time, dtf);
					lat = Double.parseDouble(split[1]);
					lng = Double.parseDouble(split[2]);
					mph = Double.parseDouble(split[3]);

					// Calculates acceleration if there was a last point
					if (newPoint != null) {
						// Calculate acceleration in mph / s, then times by
						// 0.44704 to move to M/S^2
						accel = ((newPoint.mph - mph) / (ChronoUnit.MILLIS.between(time_, newPoint.time) / 1000))
								* 0.44704;
					} else {
						accel = 0.0;
					}

					// Calculates lateral acceleration
					/*
					 * Lateral acceleration = A Sin b A = Magnitude of
					 * acceleration b = Angle of acceleration (when compared to
					 * the direction of the last recorded acceleration) Angle of
					 * acceleration can be calculated using the cosine rule: Cos
					 * B = (o^2 + j^2 - l^2) / 2ij Here o, j, l are sides, which
					 * can be calculated using the vector positions for the last
					 * 2 points and b = Cos B = Cos(PI - angle) Note that
					 * lastPoint is 2 points behind and newPoint is 1 point
					 * behind Bit confusing I know, sorry xx
					 */
					if (lastPoint != null) {
						o = Session.calcDistance(lastPoint, newPoint);
						j = Session.calcDistance(newPoint, new GeoPosition(lat, lng));
						l = Session.calcDistance(new GeoPosition(lat, lng), lastPoint);

						b = ((o * o) + (j * j) - (l * l)) / (2 * o * j);
						angle = Math.PI - (Math.acos(b));
						latAccel = Math.abs(accel * Math.sin(angle));
					} else {
						latAccel = 0.0;
					}

					if (Double.isNaN(latAccel))
						latAccel = 0.0;

					lastPoint = newPoint;
					newPoint = new Point(time_, lat, lng, mph, accel, latAccel);

					points.add(newPoint);
				}
				reader.close();

			} else {
				// All bytes
				byte[] bytes = Files.readAllBytes(Paths.get(path));

				// Usable length
				// Each point is 16 bytes long
				// It is possible that there will be an incomplete point on the
				// end
				// Thus ensure that it is only traversed in blocks of 16 bytes
				int usableLength = (bytes.length / 16) * 16;

				ByteBuffer bb = ByteBuffer.wrap(bytes, 0, usableLength);
				bb.order(ByteOrder.LITTLE_ENDIAN);

				// Converts time as string to LocalTime object

				Point newPoint = null;
				Point lastPoint = null;

				double lat, lng, mph, accel, latAccel;
				// See Lateral acceleration comment
				double o, j, l, b, angle;

				// Each 16 byte block will contain the following values at said
				// positions
				// 0 - 3: Unsigned 32 bit Integer: Time
				// 4 - 7: 32 bit float: Lat
				// 8 - 11: 32 bit float: Long
				// 12 - 15: 32 bit float: MPH
				for (int i = 0; i < usableLength; i += 16) {
					// Gets time
					LocalTime time = LocalTime.parse(Integer.toString(bb.getInt(i)), dtf);
					// Checks for duplication
					if (points.size() > 0 && time.equals(points.peekLast().time))
						continue;
					// Checks that lat and long aren't within error region
					lat = bb.getFloat(i + 4);
					lng = bb.getFloat(i + 8);
					if (Math.abs(lat) < Client.GPS_ERROR_REGION && Math.abs(lng) < Client.GPS_ERROR_REGION)
						continue;

					mph = bb.getFloat(i + 12);

					// Calculates acceleration if there was a last point
					if (newPoint != null) {
						// Calculate acceleration in mph / s, then times by
						// 0.44704 to move to M/S^2
						accel = ((mph - newPoint.mph) / (ChronoUnit.MILLIS.between(newPoint.time, time) / 1000.0))
								* 0.44704;
					} else {
						accel = 0.0;
					}

					// Calculates lateral acceleration
					/*
					 * Lateral acceleration = A Sin b A = Magnitude of
					 * acceleration b = Angle of acceleration (when compared to
					 * the direction of the last recorded acceleration) Angle of
					 * acceleration can be calculated using the cosine rule: Cos
					 * B = (o^2 + j^2 - l^2) / 2ij Here o, j, l are sides, which
					 * can be calculated using the vector positions for the last
					 * 2 points and b = Cos B = Cos(PI - angle) Note that
					 * lastPoint is 2 points behind and newPoint is 1 point
					 * behind Bit confusing I know, sorry xx
					 */
					if (lastPoint != null) {
						o = Session.calcDistance(lastPoint, newPoint);
						j = Session.calcDistance(newPoint, new GeoPosition(lat, lng));
						l = Session.calcDistance(new GeoPosition(lat, lng), lastPoint);

						b = ((o * o) + (j * j) - (l * l)) / (2 * o * j);
						angle = Math.PI - (Math.acos(b));
						latAccel = Math.abs(accel * Math.sin(angle));
					} else {
						latAccel = 0.0;
					}

					if (Double.isNaN(latAccel))
						latAccel = 0.0;

					lastPoint = newPoint;
					newPoint = new Point(time, lat, lng, mph, accel, latAccel);

					// Adds new point
					points.add(newPoint);
				}
			}

			String name = Paths.get(path).getFileName().toString();

			return new Session(name, new Lap(points, "Session"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// UTILs
	public static String formatDuration(Duration dur) {
		return String.format("%02d:%02d.%02d", dur.getSeconds() / 60, dur.getSeconds() % 60, dur.getNano() / 10000000);
	}

	private static DateTimeFormatter outdtf = DateTimeFormatter.ofPattern("HH:mm:ss.SS");

	public static String formatTime(LocalTime time) {
		return time.format(outdtf);
	}

	/*
	 * Calculates the distance between 2 points Note that this function is
	 * actually mathematically incorrect (it treats lat / long as x / y values
	 * on a flat plane) However it should provide a close enough estimate for
	 * nearby positions
	 */
	public static double calcDistance(GeoPosition a, GeoPosition b) {
		double[] v = { a.getLatitude() - b.getLatitude(), a.getLongitude() - b.getLongitude() };
		return Math.sqrt(v[0] * v[0] + v[1] * v[1]);
	}

	public final String NAME;

	public Lap session;

	public Line2D divisor;

	private LinkedList<Lap> laps;

	private Session(String name, Lap session) {
		this.NAME = name;
		this.session = session;
		this.laps = new LinkedList<Lap>(); // Temp until laps are correctly
											// divided
		this.divisor = null;
	}

	// Divides entire session into laps
	// Will divide when Lap passes through line
	public void divide(double lat1, double lng1, double lat2, double lng2) {
		this.divide(new Line2D.Double(lat1, lng1, lat2, lng2));
	}

	// Divides session up into laps
	// Division is made when the path from one point to the next crosses
	// The divider line
	public void divide(Line2D divisor) {
		// Sets divider and deletes old lap
		this.divisor = divisor;
		this.laps.clear();

		double lastLat = 0F, lastLng = 0F;
		boolean first = true;
		LinkedList<Point> currentLap = new LinkedList<Point>();

		// Lap number, used for names
		int l = 1;

		// Iterates through all points in session
		for (Point pos : this.session) {
			if (first)
				first = false;
			// Checking for division cannot be done first lap as there is no
			// last lap
			else {
				Line2D move = new Line2D.Double(pos.lat, pos.lng, lastLat, lastLng);

				// Check if they cross divide line
				if (divisor.intersectsLine(move)) {
					this.laps.add(new Lap(currentLap, "Lap " + l));
					l++;
					currentLap = new LinkedList<Point>();
				}
			}
			// Adds position to lap
			currentLap.add(pos);

			lastLat = pos.lat;
			lastLng = pos.lng;
		}

		// Add on final lap as long as it is not empty
		if (!currentLap.isEmpty())
			this.laps.add(new Lap(currentLap, "Lap " + l));

		// Updates visual side
		Client.updateLaps();
	}

	// Will return a list of all laps in a format understandable by JList
	// So they are capable of understanding the laps
	public Lap[] getLaps() {
		// Creates new array
		LinkedList<Lap> out = new LinkedList<Lap>(this.laps);
		out.add(0, this.session);
		// Copies old array into position 1 onwards
		return out.toArray(new Lap[0]);
	}

	//Returns a dump of all the points in this session as an array
	public String[] getDump() {
		String[] dump = new String[this.session.size() + 2]; //Space for every point + 2 extra lines
		dump[0] = "GPS Dump generated by lap tracker, see: github.com/edward-dunton/lap-tracker";
		dump[1] = "";

		//Iterate through every point in session
		int i = 2;
		for (Point p : this.session) {
			//I know this isn't considered the most efficient way to create a string, but I'll leave that to the compiler
			String s = Session.formatTime(p.time) + ">\t";
			s += "LAT:" + String.format("%.8f\t\t", p.lat);
			s += "LONG:" + String.format("%.8f\t", p.lng);
			s += "MPH:" + Speed.format(p.mph) + "\t";
			s += "M/S^2:" + Acceleration.format(p.accel) + "\t";
			s += "M/S^2 Lat:" + LateralAcceleration.format(p.latAccel);

			dump[i] = s;

			i ++;
		}

		return dump;
	}
}
