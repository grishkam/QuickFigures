package dataSeries;

public class BasicDataPoint implements DataPoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double position;
	private double value;
	private boolean excluded=false;

	public BasicDataPoint(double position, double value) {
		this.position=position;
		this.value=value;
	}
	
	@Override
	public double getPosition() {
		return position;
	}

	@Override
	public double getValue() {
		return value;
	}
	
	@Override
	public String getValueString() {
		return value+(this.isExcluded()? "*": "");
	}

	@Override
	public boolean isExcluded() {
		return this.excluded;
	}

	public void setExcluded(boolean excluded) {
		this.excluded = excluded;
	}
	
	public String toString() {
		return "("+position+", "+value+")";
	}

}
