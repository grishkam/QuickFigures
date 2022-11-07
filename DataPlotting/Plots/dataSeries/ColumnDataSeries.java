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
 * Date Modified: Jan 6, 2021
 * Version: 2022.2
 */
package dataSeries;

import java.util.ArrayList;

/**a data seris for a column plot*/
public class ColumnDataSeries extends AbstractDataSeries {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private double positionOnPlot;
	
	ArrayList<LocalDataPoint> data=new ArrayList<LocalDataPoint> ();

	public ColumnDataSeries(String string, ArrayList<? extends DataPoint> output) {
		this.setName(string);
		replaceData(output);
	}

	public ColumnDataSeries(String string, double... n) {
		this.setName(string);
		for(Double d: n) {
			data.add(new LocalDataPoint(d, false));
		}
	}
	public ColumnDataSeries(String string, Double... n) {
		this.setName(string);
		for(Double d: n) {
			data.add(new LocalDataPoint(d, false));
		}
	}

	public void replaceData(ArrayList<? extends DataPoint> output) {
		data=new ArrayList<LocalDataPoint> ();
		for(DataPoint d: output) {
			data.add(new LocalDataPoint(d.getValue(), d.isExcluded()));
		}
	}

	@Override
	public ArrayList<? extends DataPoint> getDataPointList() {
		return data;
	}
	
	public void setPositionOnPlot(double position) {
		this.positionOnPlot=position;
		
	}
	
	public double getPositionOnPlot() {
		return positionOnPlot;
	}
	
	
	public DataSeries getValuesForPosition(double position) {
		if(position!=this.positionOnPlot) return null;
		return this;
	}
	
	protected void determinePositionList() {
		ArrayList<Double> allPositions=new ArrayList<Double> ();
		allPositions.add(positionOnPlot);
		allUniquePositions=allPositions;
	}
	
	protected double[] getUniquePositions() { 
		return new double[] {positionOnPlot};
	}
	
	/**returns all the values, even for excluded points*/
	public Basic1DDataSeries getIncludedValues() {
		Basic1DDataSeries output = super.getIncludedValues();
		output.setPositionOnPlot(positionOnPlot);
		return output;
	}
	
	
	class LocalDataPoint extends BasicDataPoint {

		public LocalDataPoint(double value, boolean exclude) {
			super(positionOnPlot, value);
			super.setExcluded(exclude);
		}

		@Override
		public double getPosition() {
			return positionOnPlot;
		}
		
	
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;}

}
