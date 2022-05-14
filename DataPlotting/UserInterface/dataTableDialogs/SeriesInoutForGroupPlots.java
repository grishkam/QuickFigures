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
 * Date Modified: Jan 7, 2021
 * Version: 2022.1
 */
package dataTableDialogs;

import java.awt.GridBagConstraints;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JScrollPane;

import dataSeries.Basic1DDataSeries;
import dataSeries.BasicDataPoint;
import dataSeries.ColumnDataSeries;
import dataSeries.DataSeries;
import dataSeries.GroupedDataSeries;
import dataSeries.XYDataSeries;
import fileread.ExcelFileToComplexCategoryPlot;
import fileread.ExcelRowSubset;
import fileread.ReadExcelData;
import logging.IssueLog;
import standardDialog.GriddedPanel;
import standardDialog.strings.StringInputPanel;

/**A special data input dialog for grouped data*/
public class SeriesInoutForGroupPlots extends DataInputDialog2 {
	int checkedColumn=0;
	int checkedColumn2=1;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] vals;
	

	/**Creates a data input dialog for editing a category in a series.
	  Does not need a starting category*/
	public SeriesInoutForGroupPlots(String groupname, String[] names) {
		super("Input Data ", true);
		this.editName=true;;
		GriddedPanel panel = this.getMainPanel();
		title=groupname;  setTabName("");
		vals=names;
		int nrow=500;
			
		createAreaForValues(nrow);
		//prepareArea();
		
  	JScrollPane pane = new JScrollPane(area);
  	addNameField(editName);
  	
  	addScrollPaneToPanel(panel, pane);
	}
	
	public void debugReport() {
		IssueLog.log("values "+vals.length+" for "+ vals.toString());
		IssueLog.log(area.getColumnCount()+" cols and  "+area.getRowCount()+ "rows ");
	}

	public void createAreaForValues(int nrow) {
		area=new DataTable(new Object[nrow][vals.length], vals);
		
		int j=0;
		for(String s: vals) {
			area.setValueAt(s, 0, j);
			j++;
			}
	}
	
	/**Creates a data input dialog for editing a category in a series.
	  
	  Precondition: arguments must be a special type of multicategory series in which the series
	  and category roles have been flipped*/
	private SeriesInoutForGroupPlots(GroupedDataSeries starting, boolean editName) {
		super("Input Data "+(!editName? starting.getName(): ""), true);
		this.editName=editName;
		GriddedPanel panel = this.getMainPanel();
		title=starting.getName();
		if (!editName)	this.setTabName(starting.getName()); else setTabName("");
		
			map = starting.getCategoryToLocationMap();
			
			vals=map.values().toArray(new String[map.values().size()]);
			int nrow=starting.getIncludedValues().getRawValues().length+200;
			
			
			
			createAreaForValues(nrow);
			/**flawed code*/
		double[] positions = starting.getAllPositions();
		for(double position: positions)  {
			String name=map.get(position);
			int indexCol = findIndexOf(name);
			Basic1DDataSeries data = starting.getValuesForPosition(position);
			
			for(int i=0; i<data.length(); i++) {
				if (0> indexCol||indexCol>data.length()) continue;
				area.setValueAt(data.getValue(i), i+1, indexCol);
			}
		}
		

		//prepareArea() ;
  	JScrollPane pane = new JScrollPane(area);
  	addNameField(editName);
  	
  	addScrollPaneToPanel(panel, pane);
  	
  
	}

	private int findIndexOf(String name) {
		for(int i=0; i<vals.length; i++) {
			
			String theVal = vals[i];
			if (name.equals(theVal)) return i;
		}
		return Arrays.binarySearch(vals, name);
	}
	
	/**Returns a multicategory data series.
	 Precondition: must have been created with the appropriate constructor*/
	public GroupedDataSeries getInputSeries() {
		if (editName) {title=this.getString("name");}
		ArrayList<ColumnDataSeries> subparts=new ArrayList<ColumnDataSeries>();
		for(String v: vals) {
			int indexCol = findIndexOf(v);
			ArrayList<BasicDataPoint> nums = this.getNumbers(indexCol, new RowFilter.HeaderExcludingFilter());
			subparts.add(new ColumnDataSeries(v, nums));
		};
		return new GroupedDataSeries(title, map, subparts.toArray(new DataSeries[subparts.size()]));
		//this.getNumbers(column)
	}
	
	public static GroupedDataSeries getUserInputSeries(GroupedDataSeries input, boolean name) {
		SeriesInoutForGroupPlots d = new SeriesInoutForGroupPlots(input, name);
		d.showDialog();
		if (d.wasOKed()) {
			return d.getInputSeries();
		}
		return null;
	}
	
	public static GroupedDataSeries getUserInputSeries(String title, String[] colNames) {
		SeriesInoutForGroupPlots d = new SeriesInoutForGroupPlots(title, colNames);
		d.showDialog();
		if (d.wasOKed()) {
			return d.getInputSeries();
		}
		return null;
	}
	
	@Override
	public void showDialog() {
		debugReport();
		super.showDialog();
	}
	
	/**Creates a Field for inputting of a title*/
	private void addNameField(boolean editName) {
		if (editName) {
    		StringInputPanel stringpanel = new StringInputPanel("name", title);
    		this.add("name" , stringpanel);
    		this.moveGrid(-1, 2);
    	}
	}

	private void addScrollPaneToPanel(GriddedPanel panel, JScrollPane pane) {
		GridBagConstraints c = new GridBagConstraints();
    	c.gridx=0;
    	c.gridwidth=3;
    	c.gridy=3;
    	panel.add(pane, c);
	}
	
	
	public static Basic1DDataSeries getUserData(Basic1DDataSeries starting) {
		SeriesInoutForGroupPlots d = new SeriesInoutForGroupPlots(starting);
		d.showDialog();
		
		if (d.wasOKed()) {
			ArrayList<Double> nums = d.getNumbers();
			//ArrayList<String> text = d.getNonNumbers();
			String dataName="data";
			//if (text.size()>0) dataName=text.get(0);
			return new Basic1DDataSeries(dataName, nums);
		} 
		return null;
	}
	
	public static void main(String[] args) {
		/**	
			ArrayList<java.awt.geom.Point2D.Double> sampleData = new ArrayList<Point2D.Double>();
			sampleData.add(new Point2D.Double(5, 6));
			sampleData.add(new Point2D.Double(7, 9));
			
			XYDataSeries testData = new XYDataSeries("Data 1", sampleData);
			XYDataSeries data2 = getUserPointDataData(testData);
			System.out.println(data2.toString());*/
			File f=new ExcelFileToComplexCategoryPlot(0).getFileAndaddExtension();
			ExcelRowSubset subset;
			try {
				new ReadExcelData();
				subset = new ExcelRowSubset(ReadExcelData.fileToWorkBook(f.getAbsolutePath()));
				ArrayList<GroupedDataSeries> items = subset.createCategoryDataSeries(0, 1, 2);//ReadExcelData.readXY(f.getAbsolutePath());
				SeriesInoutForGroupPlots dialog = new SeriesInoutForGroupPlots(items.get(0), true);
						dialog.showDialog();
						IssueLog.log(dialog.getInputSeries());
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}

	private ArrayList<Double> getNumbers() {
		return getNumbers2(checkedColumn, new RowFilter.HeaderExcludingFilter());
	}
	
	
	/**Creates a data input dialog for editing the series*/
	public SeriesInoutForGroupPlots(Basic1DDataSeries starting) {
		super("Input Data", true);
		area=new DataTable(new Object[starting.getIncludedValues().getRawValues().length+200][1], new Object[] {starting.getName()});
		
		
		double[] values2 = starting.getIncludedValues().getRawValues();
		for(int i=0; i<values2.length; i++) {
			area.setValueAt(values2[i], i+1, checkedColumn);
		}
		prepareArea();
    	
    
	}
	
	/**Creates a data input dialog for editing a single series*/
	private SeriesInoutForGroupPlots(XYDataSeries starting) {
		super("Input Data", true);
		GriddedPanel panel = this.getMainPanel();
		
			this.setTabName("Data");
		
		area=new DataTable(new Object[starting.getIncludedValues().getRawValues().length+200][2], new Object[] {starting.getxName(), starting.getDependantVariableName()});
		prepareArea();
		
		double[] values2 = starting.getIncludedValues().getRawValues();
		for(int i=0; i<values2.length; i++) {
			area.setValueAt(starting.getPosition(i), i+1, checkedColumn);
			area.setValueAt(starting.getValue(i), i+1, checkedColumn2);
		}
		
    	JScrollPane pane = new JScrollPane(area);
    
    	panel.add(pane, super.getCurrentConstraints());
    
	}

	public static XYDataSeries getUserPointDataData(XYDataSeries starting) {
		SeriesInoutForGroupPlots d = new SeriesInoutForGroupPlots(starting);
		d.showDialog();
		String dataName=starting.getName();
		
		if (d.wasOKed()) {
			ArrayList<BasicDataPoint> nums = d.getPoints();
			//ArrayList<String> text = d.getNonNumbers();
			
			//if (text.size()>0) dataName=text.get(0);
			return new XYDataSeries(dataName, nums);
		} 
		return null;
	}
	
	ArrayList<BasicDataPoint> getPoints() {
		return getPoints(checkedColumn, checkedColumn2, new RowFilter.HeaderExcludingFilter());
	}
	
}
