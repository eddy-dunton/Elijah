package eddydunton.elijah;

import org.jxmapviewer.viewer.GeoPosition;

import java.time.LocalTime;

public class Point extends GeoPosition{
	private static final long serialVersionUID = 1L;

	public final LocalTime time;
	public final double lat;
	public final double lng;
	public final double mph;
	public final double accel;
	public final double latAccel;

	public Point(LocalTime time, double lat, double lng, double mph, double accel, double latAccel) {
		super(lat, lng);
		this.time = time;
		this.lat = lat;
		this.lng = lng;
		this.mph = mph;
		this.accel = accel;
		this.latAccel = latAccel;
	}
}