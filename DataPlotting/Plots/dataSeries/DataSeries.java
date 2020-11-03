package dataSeries;

import java.io.Serializable;
import java.util.HashMap;

public interface DataSeries extends Serializable {

	public Basic1DDataSeries getIncludedValues();
	
	/**If multiple data points are associated with a given position, 
	  returns a basic data series*/
	public DataSeries getValuesForPosition(double position);
	
	/**how many points are included in the data series*/
	public int length();
	
	/**returns the position or value for point i.
	  Note: the points will be in no particular order*/
	public double getValue(int i);
	public double getPosition(int i);
	public DataPoint getDataPoint(int i);
	
	/**returns an array with each position appearing only once*/
	public double[] getAllPositions() ;
	public double[] getAllPositionsInOrder() ;
	public String getName();
	public void setName(String name);
	
	
	/**Slight shifts in position are needed in a few kinds of plots*/
	public double getPositionOffset();
	public void setPositionOffset(double o);
	
	public HashMap<Double, Double> getValueOffsetMap();

	//public ArrayList<DataPoint> getIncludedPoints();
	 
}
