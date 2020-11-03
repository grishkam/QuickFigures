package plotParts.Core;

import java.io.Serializable;

public class Gap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double start;
	double size;
	
	public double markerTicWidth=14;
	public double gapMarkerHeight=4;
	
	public Gap copy() {
		Gap g = new Gap(start, size);
		g.copyFrom(this);
		return g;
	}
	
	public void copyFrom(Gap gap) {
		start=gap.start;
		size=gap.size;
		markerTicWidth=gap.markerTicWidth;
		gapMarkerHeight=gap.gapMarkerHeight;
	}

	public Gap(double start, double size) {
		this.start=start;
		this.size=size;
	}
	
	public boolean isInside(double value) {
		if (value>start&&value<start+size) return true;
		return false;
	} 
	
	public boolean isAfter(double value) {
		if (value>=start+size) return true;
		return false;
	}

	public double location() {
		return start;
	} 
	public void setLocation(double s) {
		start=s;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}
	
	
	
}
