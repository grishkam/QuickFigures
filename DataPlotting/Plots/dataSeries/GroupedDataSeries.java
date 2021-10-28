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
 * Version: 2021.2
 */
package dataSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**a Data series that is an amalgamation of a few categories*/
public class GroupedDataSeries implements DataSeries {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name="data";
	private String xName="Catergories";
	private String yName="values";
	HashMap<String, DataSeries> allDividedSeries=new HashMap<String, DataSeries> ();
	
	/**This map indicates the position of each data series on the plot. in one
	  plot, every Category data series object should share the same map (the same hashmap)*/
	private HashMap<Double, String> indicesForSeries=new HashMap<Double, String>();
	
	
	private double pOffset;
	private HashMap<Double, Double> valueOffsetMap;
	
	
	
	public GroupedDataSeries(String name2, HashMap<Double, String> indicesForSeries, DataSeries... number) {
		name=name2;
		this.indicesForSeries=indicesForSeries;
		for(DataSeries s: number) {
			 allDividedSeries.put(s.getName(), s);
		}
		
		/**if there is no input data, adds emplty data sries*/
		if (number.length==0) {
			for(Double k: indicesForSeries.keySet()) {
				String namenew = indicesForSeries.get(k);
				Basic1DDataSeries newsereies = new Basic1DDataSeries(namenew, new double[] {});
				allDividedSeries.put(namenew, newsereies );
			}
		}
		
	}
	
	/***
	public MultiCategorySeries(String name2, DataSeries... number) {
		name=name2;
		Double m=1.0;
		for(DataSeries s: number) {
			 allDividedSeries.put(s.getName(), s);
			 indicesForSeries.put(m, s.getName());
			 m+=1;
		}
		
	}
	*/
	
	public static  HashMap<Double, String> createLocationMapFor(ArrayList<String> s) {
		HashMap<Double, String> ser=new HashMap<Double, String>();
		Double m=1.0;
		for(String s1: s) {
			 ser.put(m, s1);
			 m+=1;
		}
		return ser;
	}
	
	/***/
	public void addNewCategory(String name, DataSeries numbers) {
		numbers.setName(name);
		allDividedSeries.put(name, numbers);
		double place=1;
		while(indicesForSeries.keySet().contains(place)&&!indicesForSeries.get(place).equals(name)) {
			place+=1;
		}
		indicesForSeries.put(place, name);
		
		
	}
	
	
	public HashMap<Double, String> getCategoryToLocationMap() {
		return indicesForSeries;
	}
	
	public double smallestPosition() {
		double smallest=Double.MAX_VALUE;
		for(Double d: indicesForSeries.keySet()) {
			if (d<smallest) smallest=d;
		}
		return smallest;
	}
	public double largestPosition() {
		double largest=Double.MIN_VALUE;
		for(Double d: indicesForSeries.keySet()) {
			if (d>largest) largest=d;
		}
		return largest;
	}
	


	public String toString() {
		String s="";
		
		for(DataSeries p: allDividedSeries.values())s+=""+'\n'+p.toString();
		return s;
	}
	
	

	@Override
	public Basic1DDataSeries getIncludedValues() {
		ArrayList<Double> numbers=new ArrayList<Double> ();
		for(DataSeries p: allDividedSeries.values())
			for(Double d: p.getIncludedValues().getRawValues()) numbers.add(d);
		
		double[] values=new double[numbers.size()];
		for(int i=0; i<values.length;i++) values[i]=numbers.get(i);
		return new Basic1DDataSeries(this.getName(), values);
	}

	@Override
	public int length() {
		int out=0;
		;
		for(DataSeries p: allDividedSeries.values()) {
			out+=p.length();;
		}
		
		return out;
	}

	
	public double getValue(int y) {
		for(DataSeries p: allDividedSeries.values())  {
			if (y>=p.length()) y-=p.length();
			else return p.getValue(y);
		}
		return 0;
	}
	
	
	public DataPoint getDataPoint(int y) {
		for(DataSeries p: allDividedSeries.values())  {
			if (y>=p.length()) y-=p.length();
			else return p.getDataPoint(y);
		}
		return null;
	}
	
	public String getCategoryOf(int y) {
		for(DataSeries p: allDividedSeries.values())  {
			if (y>=p.length()) y-=p.length();
			else return p.getName();
		}
		return null;
	}

	@Override
	public double getPosition(int x) {
		for(DataSeries p: allDividedSeries.values())  {
			if (x>=p.length()) x-=p.length();
			else return p.getPosition(x);
		}
		return 0;
	}




	public String getxName() {
		return xName;
	}




	public void setxName(String xName) {
		this.xName = xName;
	}




	public String getyName() {
		return yName;
	}




	public void setyName(String yName) {
		this.yName = yName;
	}



	/**returns a Data series with only the values at the given x position*/
	@Override
	public Basic1DDataSeries getValuesForPosition(double position) {
		//Intger i=(int) position;
		String ser = indicesForSeries.get(position);
		DataSeries divCategory = allDividedSeries.get(ser);
		if (divCategory ==null) return null;// if there are no data points for the mapped category
		Basic1DDataSeries output = divCategory.getIncludedValues();
		
		output.setPositionOnPlot(position);
		output.setPositionOffset(getPositionOffset());
		return output;
	}
	public DataSeries getValuesForPosition(String ser) {
		DataSeries output = allDividedSeries.get(ser);
		output.setPositionOffset(getPositionOffset());
		return output;
	}

	protected double[] getUniquePositions() {
		Set<Double> set = indicesForSeries.keySet();
		Double[] dar = set.toArray(new Double[set.size()]);
		double[] output=new double[dar.length];
		for(int i=0; i<output.length; i++) output[i]=dar[i];
		return output;
	}
/**
	public static void main(String[] args) {
		File f=new ExcelFileToBarPlot(0).getFileAndaddExtension();
		System.out.println(f.getAbsolutePath());
		InputStream inp;
		try {
			inp = new FileInputStream(f.getAbsolutePath());
			Workbook wb = WorkbookFactory.create(inp);
		ArrayList<MultiCategorySeries> xyData = ReadExcelData.extractXYDataSeriesF(wb);
		xyData.get(0).getAllPositions();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    InputStream inp = new FileInputStream("workbook.xlsx");

	    
	    
	}
*/

/**

	public HashMap<Integer, Basic1DDataSeries> getDividedSeries() {
		if (dividedSeries==null)dividedSeries=new HashMap<Integer, Basic1DDataSeries>();
		return dividedSeries;
	}

*/


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}

	@Override
	public double getPositionOffset() {
		return pOffset;
	}
	
	public void setPositionOffset(double off) {
		pOffset=off;
	}

	@Override
	public HashMap<Double, Double> getValueOffsetMap() {
		return valueOffsetMap;
	}

	public void setValueOffsetMap(HashMap<Double, Double> valueOffsetMap) {
		this.valueOffsetMap = valueOffsetMap;
	}

public void replaceData( GroupedDataSeries input , HashMap<Double, String> indicSeries) {
	this.allDividedSeries=input.allDividedSeries;
	indicesForSeries=indicSeries;
	
}

public void replaceData( GroupedDataSeries input ) {
	this.allDividedSeries=input.allDividedSeries;}

public Set<String> getAllSeriesNames() {return allDividedSeries.keySet();}

public ArrayList<String> getSeriesNamesInorder() {
	ArrayList<String> output = new ArrayList<String>();
	for(double d=0; d<200; d++) {
		String s = indicesForSeries.get(d);
		if (s!=null) output.add(s);
	}
	return output;
	}

@Override
public double[] getAllPositions() {
	return getUniquePositions();
}

@Override
public double[] getAllPositionsInOrder() {
	double[] in = getAllPositions().clone();
	Arrays.sort(in);
	return in;
}

	
}
