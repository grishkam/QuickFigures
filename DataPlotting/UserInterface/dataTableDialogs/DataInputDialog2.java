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
 * Version: 2022.2
 */
package dataTableDialogs;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableCellEditor;

import dataSeries.BasicDataPoint;
import dataSeries.KaplenMeierDataPoint;
import standardDialog.StandardDialog;

/**A very generalized dialog for data input. See also 
 * @see DataTable*/
public class DataInputDialog2 extends StandardDialog {
	
	/**
	 
	 */
	private static final long serialVersionUID = 1L;
	DataTable area;
	
	protected HashMap<Double, String> map;
	protected String title;
	protected boolean editName;
	
	
	/**Creates a data input dialog for editing data*/
	public DataInputDialog2(DataTable area) { 
		this.area=area;
		
		prepareArea() ;
	}
	
	protected void prepareArea() {
		this.setTabName("Data");

		JScrollPane pane = new JScrollPane(area);
	    pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	    super.getCurrentUsePanel().add(pane, super.getCurrentConstraints());
	}

	
	
	@Override
	public void showDialog() {
		
		super.showDialog();
	}
	
	protected void stopEditing() {
		TableCellEditor editor = area.getCellEditor();
		if (editor != null) {
		  editor.stopCellEditing();
		}
	}
	
	protected  DataInputDialog2(String st, boolean b) {
		super(st, b);
	}
	
	/**Returns the numbers from the column. Pass a RowFilter object to only 
	 * check some rows.*/
	ArrayList<BasicDataPoint> getNumbers(int column, RowFilter filter) {
		ArrayList<BasicDataPoint> output=new ArrayList<BasicDataPoint>();

		DataTable area2 = area;
		for(int i=0; i<area2.getRowCount(); i++) try {
			
			/**makes sure not to waste time on irrelevent rows*/
			if (filter!=null &&!filter.isChosenRow(area2, i)) continue;
			
			Object value = area2.getValueAt(i,column);
			BasicDataPoint p=null;
			if (value instanceof Double)p=(new BasicDataPoint(0, (Double) value));
			if (value instanceof String)p=(new BasicDataPoint(0,doubleFrom(value.toString())));
			if (p!=null &&this.isExclusionString(value)) p.setExcluded(true);
			if (p!=null) output.add(p);
			
		} catch (Throwable ex) {}
		
		return output;
	}
	
	/**Returns the numbers from the column. Pass a RowFilter object to only 
	 * check some rows.*/
	ArrayList<Double> getNumbers2(int column, RowFilter filter) {
		ArrayList<Double> output=new ArrayList<Double>();

		for(int i=0; i<area.getRowCount(); i++) try {
			
			/**makes sure not to waste time on irrelevent rows*/
			if (filter!=null &&!filter.isChosenRow(area, i)) continue;
			
			Object value = area.getValueAt(i,column);
			if (value instanceof Double)output.add((Double) value);
			if (value instanceof String)output.add(doubleFrom(value.toString()));
		} catch (Throwable ex) {}
		
		return output;
	}
	
	/**Returns the values in the column as Strings. Pass a RowFilter object to only 
	 * check some rows.*/
	static ArrayList<String> getStrings(int column, RowFilter filter, TableReader area2) {
		ArrayList<String> output=new ArrayList<String>();

		for(int i=0; i<area2.getRowCount(); i++) try {
			
			/**makes sure not to waste time on irrelevent rows*/
			if (filter!=null &&!filter.isChosenRow(area2, i)) continue;
			
			Object value = area2.getValueAt(i,column);
			if (value instanceof String)output.add((String) value);
			else output.add(""+value);
		} catch (Throwable ex) {}
		
		return output;
	}
	
	/**Returns the numeric points in the columns. Pass a RowFilter object to only 
	 * check some rows. Skips points if one of the two columns if missing a number.
	 * one column holds positions and another holds values*/
	ArrayList<BasicDataPoint> getPoints(int col1, int col2,  RowFilter filter) {
		ArrayList<BasicDataPoint> output=new ArrayList<BasicDataPoint>();

		//String t = area.getValueAt(row, column)
		//String[] lines = t.split(""+'\n');
		for(int i=0; i<area.getRowCount(); i++) try {
			
			/**makes sure not to waste time on irrelevent rows*/
			if (filter!=null &&!filter.isChosenRow(area, i)) continue;
			
			Object val1 = area.getValueAt(i,col1);
			Object val2 = area.getValueAt(i,col2);
			if (val1==null||val2==null) continue;
			BasicDataPoint pt = new BasicDataPoint( 
									doubleFrom(val1),
				                    doubleFrom( val2));
			if (this.isExclusionString(val2)) pt.setExcluded(true);
			output.add(pt);
			} catch (NumberFormatException ex) {
			//	ex.printStackTrace();
			//I expect number format exceptions to be commonplace. why print them?
			//In the case of exceptions, I just want the loop to continue after catch.
			}
		
		return output;
	}
	
	/**Returns the numeric points in the columns. Pass a RowFilter object to only 
	 * check some rows. Skips points if one of the two columns if missing a number*/
	 static ArrayList<KaplenMeierDataPoint> getPointsKaplan(int col1, int col2,  RowFilter filter, TableReader area2) {
		ArrayList<KaplenMeierDataPoint> output=new ArrayList<KaplenMeierDataPoint>();

		 
		int rowCount = area2.getRowCount();
		for(int i=0; i<rowCount; i++) try {
			/**makes sure not to waste time on irrelevent rows*/
			if (filter!=null &&!filter.isChosenRow(area2, i)) continue;
			
			Object val1 = area2.getValueAt(i,col1);
			Object val2 = area2.getValueAt(i,col2);
			
			
			boolean censor = false;
			if (isCensor(val1)) censor=true;
			
		
			//if (val1==null||val2==null) continue;//null values mean a mouse was still alive
			
			Double doubleFrom = doubleFrom(val2);
			
			KaplenMeierDataPoint pt = new KaplenMeierDataPoint( 
									doubleFrom,
				                    censor);
			output.add(pt);
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			//I expect number format exceptions to be commonplace. why print them?
			//In the case of exceptions, I just want the loop to continue after catch.
			}
	
		return output;
	}

	private static boolean isCensor(Object val1) {
		if (val1==null)return false;
		//if (val1==new Double(1)) return true;//TODO: determine if commenting this out affect anything
		if ("1.0".equals((val1+"").toString().toLowerCase())) return true;
		if ("1".equals((val1+"").toString().toLowerCase())) return true;
		return "censored".equals(val1.toString().toLowerCase());
	}
	
	boolean isExclusionString(Object value) {
		if (value instanceof String && ((String) value).endsWith("*"))
			{return true;}	
			return false;
		
	}
	
	/**returns a double value*/
	protected static Double doubleFrom(Object value) throws NumberFormatException {
		if (value instanceof Double) return (Double) value;
		if (value instanceof String)
		{
			String val=(String) value;
			if (val.endsWith("*")) {
				val= val.substring(0, val.length()-1);
			}
			return Double.parseDouble(val);
		}
		
		throw new NumberFormatException();
	}
	

	public DataTable getDataTable() {
		return area;
	}

	public void setDataTable(DataTable area) {
		this.area = area;
	}


	/**what action to take when the ok button is pressed*/
	protected void onOK() {
		this.stopEditing();//must commit any edits to the model before use
		super.onOK();
	}
	
}
