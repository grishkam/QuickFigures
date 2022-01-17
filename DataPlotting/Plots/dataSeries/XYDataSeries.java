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
 * Version: 2022.0
 */
package dataSeries;

import java.util.ArrayList;
import java.util.HashMap;

/**a data seris for an x,y plot*/
public class XYDataSeries extends AbstractDataSeries{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String xName="x values";
	private String yName="y values";
	private ArrayList<BasicDataPoint> numbers;

	
	public XYDataSeries(ArrayList<BasicDataPoint> number) {
		this.numbers=number;
	}
	
	public XYDataSeries(String name2, ArrayList<BasicDataPoint> nums) {
		this(nums);
		this.setName(name2);
	}

	public String toString() {
		String s="";
		
		for(BasicDataPoint p: numbers)s+=""+'\n'+p.toString();
		return s;
	}
	
	
	
	/**returns all the positions, even for excluded points*/
	private Basic1DDataSeries getPositions() {
		double[] values=new double[numbers.size()];
		for(int i=0; i<values.length;i++) values[i]=numbers.get(i).getPosition();
		return new Basic1DDataSeries(xName, values);
	}
	
	/**unfinished formula for regressio line {slope, intercept, r, r2}*/
	public double[] getLeastSquareLine() {
		
		Basic1DDataSeries y = getIncludedValues();
		Basic1DDataSeries x = getPositions();
		
		double ym = y.getMean();
		double xm = x.getMean();
		
		double slope=covi(x, y)/ covi(x,x);
		
		double b = ym-slope*xm;
		
		double r =(1/(y.getSDDev()*x.getSDDev())) * covi(x, y)/(y.length()-1);
		
		return new double[] {slope, b, r, r*r};
		
	}
	
	static double covi(Basic1DDataSeries x, Basic1DDataSeries y) {
		double ym = y.getMean();
		double xm = x.getMean();
		double covariance=0;
		double n=y.length();
		for(int i=0; i<n; i++) {
			double xi = x.getValue(i);
			double yi = y.getValue(i);
			covariance+=(xi-xm)*(yi-ym);
		}
		//covariance=covariance;
		return covariance;
	}

	

	


	public String getxName() {
		return xName;
	}




	public void setxName(String xName) {
		this.xName = xName;
	}




	public String getDependantVariableName() {
		return yName;
	}




	public void setyName(String yName) {
		this.yName = yName;
	}






	@Override
	public HashMap<Double, Double> getValueOffsetMap() {
		
		return null;
	}

	public void replaceData(XYDataSeries datanew) {
		
		numbers=datanew.numbers;
		refreshPositionList();
	}



	/**returns the arraylist that represents this data internally, any changes 
	  here, change this data series*/
public ArrayList<BasicDataPoint> getDataPointList() {
	return numbers;
}





	
	

	
}
