package plotParts.Core;

import java.io.Serializable;


public class PlotAxes implements Serializable {
	
	/**
	 * 
	 */
	static int LOG_SCALE=1, NORMAL_SCALE=0;
	private static final long serialVersionUID = 1L;
	
	private int majorTic=25;
	private int minorTic=5;
	int scaleType=0;
	private Gap gap=new Gap(0,0);
	
	private double minValue=0;
	private double maxValue=100;
	private boolean vertical=true;
	
	PlotAxes copy(){
		PlotAxes pa = new PlotAxes();
		pa.copyFieldsFrom(this);
		return pa;
	}
	
	public void copyFieldsFrom(PlotAxes pa) {
		this.maxValue=pa.maxValue;
		this.minValue=pa.minValue;
		this.vertical=pa.vertical;
		this.scaleType=pa.scaleType;
		this.majorTic=pa.majorTic;
		this.minorTic=pa.minorTic;
		gap.copyFrom(pa.getGap());
	}
	
	public String toString() {
		return "From "+minValue+" to "+maxValue+";  Major tics at "+
					majorTic+ "minor tics at "+minorTic;}
	
	public double getRange() {
		return getMaxValue()- getMinValue();
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public int getMajorTic() {
	
		return majorTic;
	}

	public void setMajorTic(int majorTic) {
		this.majorTic = majorTic;
	}

	/**returns the minor tic spacing, cannot be 0 (infinite loop issue), returns 1 if set to 0*/
	public int getMinorTic() {
		if (minorTic==0) return 1;
		
		return minorTic;
	}

	public void setMinorTic(int minorTic) {
		this.minorTic = minorTic;
	}

	public boolean isVertical() {
		return vertical;
	}

	public void setVertical(boolean vertical) {
		this.vertical = vertical;
	}

	public Gap getGap() {
		return gap;
	}

	public void setGap(Gap gap) {
		this.gap = gap;
	}
	
	
	public boolean usesLogScale() {
		return this.scaleType==LOG_SCALE;
	}


}
