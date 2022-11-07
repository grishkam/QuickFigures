/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Jan 30, 2022
 * Version: 2022.2
 */
package dataSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**An abstract superclass for storing data that is part of a plot*/
public abstract class AbstractDataSeries implements DataSeries {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String name="Data";
	protected double positionOffset=0;
	ArrayList<Double> allUniquePositions=null;
	protected HashMap<Double, DataSeries> dividedSeries=new HashMap<Double, DataSeries> ();
	private HashMap<String, Object> map;
	

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name=name;

	}

	@Override
	public double getPositionOffset() {
		return positionOffset;
	}

	@Override
	public void setPositionOffset(double o) {
		positionOffset=o;

	}
	
	@Override
	public double getValue(int y) {
		return getDataPoint(y).getValue();
	}
	@Override
	public double getPosition(int x) {
		return getDataPoint(x).getPosition();
	}
	
	@Override
	public int length() {
		return getDataPointList().size();
	}


	
	@Override
	public DataPoint getDataPoint(int i) {
		 return getDataPointList().get(i);
	}
	
	/**returns the array list that represents this data internally, any changes 
	here, change this data series*/
	public ArrayList<DataPoint> getIncludedNumbers() {
		ArrayList<DataPoint> output=new ArrayList<DataPoint>();
		for(DataPoint n: getDataPointList()) {
			if (n.isExcluded()) continue;
			output.add(n);
		}
		
		return output;
	}
	

	/**returns a Data series with only the values at the given x position*/
	@Override
	public DataSeries getValuesForPosition(double position) {
		if (getDividedSeries().get(position)!=null) {
			DataSeries o = getDividedSeries().get(position);
			o.setPositionOffset(getPositionOffset());
			return o;
			}
		
		ArrayList<DataPoint> output=new ArrayList<DataPoint>();
		for(int i=0; i<length(); i++) {
			if (this.getPosition(i)==position)
				output.add(getDataPoint(i));
		} 
		ColumnDataSeries newData = new ColumnDataSeries(getDependantVariableName()+" for position= "+position, output);
		newData.setPositionOnPlot(position);
		newData.setPositionOffset(this.getPositionOffset());
		getDividedSeries().put(position, newData);
		return newData;
	}


	private String getDependantVariableName() {
		return name;
	}

	@Override
	public double[] getAllPositions() {
		return getUniquePositions();
	}

	

	protected void determinePositionList() {
		ArrayList<Double> allPositions=new ArrayList<Double> ();
		ArrayList<DataPoint> sorted = new ArrayList<DataPoint> ();
		
		sorted.addAll(getDataPointList());
		Collections.sort(sorted, new Comparator<DataPoint>() {

			

			@Override
			public int compare(DataPoint o1, DataPoint o2) {
				return (int) (o1.getPosition()-o2.getPosition());
			}});
		
		Double lastPosition=sorted.get(0).getPosition();
		allPositions.add(lastPosition);
		
		/**goes through the presorted list, when it finds an item with a new 
		 x value, it adds the x value to the list. skips when it finds the
		 same x value again*/
		for(int i=1; i< length(); i++) {
			if (sorted.get(i).getPosition()==lastPosition) continue;
			lastPosition=sorted.get(i).getPosition();
			allPositions.add(lastPosition);
			
		}
		allUniquePositions=allPositions;
		
	}


	@Override
	public double[] getAllPositionsInOrder() {
			return this.getAllPositions();
	}


	protected double[] getUniquePositions() {
		if (allUniquePositions==null) this.determinePositionList();
		double[] output=new double[allUniquePositions.size()];
		for(int i=0; i<output.length; i++) output[i]=allUniquePositions.get(i);
		return output;
	}
	
	/**A map of every divided series*/
	public HashMap<Double,  DataSeries> getDividedSeries() {
		if (dividedSeries==null)dividedSeries=new HashMap<Double, DataSeries>();
		return dividedSeries;
	}
	protected void refreshPositionList() {
		dividedSeries=new HashMap<Double, DataSeries> ();
		allUniquePositions=null;
		determinePositionList();
	}
	@Override
	public HashMap<Double, Double> getValueOffsetMap() {
		
		return null;
	}
	
	/**returns all the values, even for excluded points*/
	public Basic1DDataSeries getIncludedValues() {
		ArrayList<DataPoint> included = getIncludedNumbers();
		double[] values=new double[included.size()];
		for(int i=0; i<values.length;i++) values[i]=included.get(i).getValue();
		return new Basic1DDataSeries(getDependantVariableName(), values);
	}
	

	
	/**returns the arraylist that represents this data internally, any changes 
	  here, change this data series*/
public abstract  ArrayList<? extends DataPoint> getDataPointList() ;


/**
 * @param key
 * @param value
 */
public void setTag(String key, Object value) {
	if(map==null)
		map=new HashMap<String, Object>();
	map.put(key, value);
	
}

/**returns a tag with the given key*/
public Object getTag(String Key) {
	if(map==null)
		return null;
	return map.get(Key);
}

}
