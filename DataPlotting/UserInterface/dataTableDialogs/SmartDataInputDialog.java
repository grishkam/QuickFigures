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
package dataTableDialogs;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import dataSeries.BasicDataPoint;
import dataSeries.ColumnDataSeries;
import dataSeries.DataPoint;
import dataSeries.DataSeries;
import dataSeries.GroupedDataSeries;
import dataSeries.KaplenMeierDataPoint;
import dataSeries.KaplanMeierDataSeries;
import dataSeries.XYDataSeries;
import dataTableActions.DataTableMenu;
import figureFormat.DirectoryHandler;
import fileread.ExcelRowToJTable;
import fileread.PlotType;
import fileread.UtilForDataReading;
import graphicActionToolbar.CurrentFigureSet;
import groupedDataPlots.Grouped_Plot;
import logging.IssueLog;
import plotCreation.GroupedPlotCreator;
import plotCreation.KaplanMeierPlotCreator;
import plotCreation.PlotCreator;
import plotCreation.XYPlotCreator;
import plotCreation.XYPlotCreator.xyPlotForm;
import plotCreation.ColumnPlotCreator;
import plotCreation.ColumnPlotCreator.ColumnPlotStyle;
import ultilInputOutput.FileChoiceUtil;


/**A GUI window for opening data files and making plots out of them.
  Includes menus*/
public class SmartDataInputDialog extends DataInputDialog2 {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PlotType form=null;
	
	ArrayList<? extends PlotCreator<?>> listOfPlotCreators=new ArrayList<PlotCreator<?>>();
	
	public static SmartDataInputDialog createDialog(PlotType form) {
		return new SmartDataInputDialog(new DataTable(300, 6), form);
	}
	
	boolean wrapSearch=false;
	private JMenu plotMakerMenu;

	/**Creates a data input dialog for editing data*/
	public SmartDataInputDialog(DataTable area, PlotType form) { 
		super(area);
		updateColumnRenderers(area, form);
		this.setHideCancel(true);
		super.setTabName("");
		JMenuBar bar=new JMenuBar();
		new DataTableMenu().installDefaultActionsOnMenuBar(bar, this.getDataTable());
		
		JMenu dataMenu = new JMenu("Data");
		bar.add(dataMenu);
		dataMenu.add(clearMenuItem());
		dataMenu.add(clearPasteMenuItem());
		dataMenu.add(openTSV());
		dataMenu.add(saveTSV());
		dataMenu.add(excludeSelectedValues());
		dataMenu.add(includeSelectedValues());
		dataMenu.add( fillGapsInTableBut());
		
		JMenu bp = new JMenu("New Plot");
		bar.add(bp);
		
		this.form=form;
		if (form==PlotType.COLUMN_PLOT_TYPE||form==null||form==PlotType.DEFAULT_PLOT_TYPE_COLS) {
			setPlotMakerMenu(createColumnPlotMakerMenu());
			bp.add(getPlotMakerMenu());
		}
		if (form==PlotType.XY_PLOT_TYPE||form==null) {
			setPlotMakerMenu(createXYPlotMakerMenu());
			bp.add(getPlotMakerMenu());
		
		}
		if (form==PlotType.GROUP_PLOT_TYPE||form==null) {
			setPlotMakerMenu(createGroupedPlotMakerMenu());
			bp.add(createGroupedPlotMakerMenu());
		}
		
		if (form==PlotType.KAPLAN_MEIER_PLOT_TYPE||form==null) {
			setPlotMakerMenu(createKMPlotMakerMenu());
			bp.add(getPlotMakerMenu());
		}
		
		this.setJMenuBar(bar);
	}

	private JMenuItem excludeSelectedValues() {
		JMenuItem ji = new JMenuItem("Exclude Selected Values");
		ji.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				markSelectedAsExcluded(false);
				
			}

			});
		return ji;
	}
	
	private JMenuItem includeSelectedValues() {
		JMenuItem ji = new JMenuItem("Include Selected Values");
		ji.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				markSelectedAsExcluded(true);
				
			}

			});
		return ji;
	}
	
	private void markSelectedAsExcluded(boolean include) {
		int[] rows = area.getSelectedRows();
		int[] cols = area.getSelectedColumns();
		for(int r:rows) {
			for(int col:cols) {
			Object val = area.getValueAt(r,col);
			try{
				Double dub = super.doubleFrom(val);
				area.setValueAt(dub+(include?"":"*"), r, col);
			}
			catch (Throwable t) {
				
			}
			}
		}
	}

	public void updateColumnRenderers(DataTable area, PlotType form) {
		if (form!=PlotType.COLUMN_PLOT_TYPE) {
			area.getColumnModel().getColumn(0).setCellRenderer( new SmartRenderer(this, form, new Color(245, 200, 200)));;
			area.getColumnModel().getColumn(1).setCellRenderer( new SmartRenderer(this, form, new Color(205, 250, 200)));;
			area.getColumnModel().getColumn(2).setCellRenderer( new SmartRenderer(this, form, new Color(205, 200, 240)));;
			for(int i=3; i<area.getColumnCount(); i++)
				area.getColumnModel().getColumn(i).setCellRenderer( new SmartRenderer(this, form, new Color(255, 250, 250)));;
				
		}
	}
	
	private JMenuItem openTSV() {
		JMenuItem open = new JMenuItem("Open text file");
		open.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				File f = FileChoiceUtil.getUserOpenFile();
				if (f==null|| !f.exists()) return;
				try {
					getDataTable().putFileIntoTable(f);
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			

			});
		return open;
	}
	
	private JMenuItem saveTSV() {
		JMenuItem open = new JMenuItem("Save as text");
		open.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				area.writeTableToFile(FileChoiceUtil.getSaveFile());
			}

		});
		return open;
	}
	

	private JMenuItem fillGapsInTableBut() {
		JMenuItem jb = new JMenuItem("Fill Missing Series Names and Gaps");
		jb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				fillGapsInTable();
			}
			
		});
		return jb;
		
	}

	private void fillGapsInTable() {
		int refCol=2;
		int fillCol=0;
		
		area.fillMissingValuesFor(refCol, fillCol);
		if (form!=PlotType.DEFAULT_PLOT_TYPE_COLS&&form!=PlotType.XY_PLOT_TYPE) area.fillMissingValuesFor(refCol, fillCol+1);
	}

	
	private JMenuItem clearMenuItem() {
		JMenuItem mi = new JMenuItem("Clear all data");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				area.deleteAllCells();
			}});
		return mi;
	}
	
	private JMenuItem clearPasteMenuItem() {
		JMenuItem mi = new JMenuItem("Clear and Paste");
		mi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					area.deleteAllCells();
					getDataTable().pasteToLocation(
							Toolkit.getDefaultToolkit().getSystemClipboard(), 0,0);
					area.shiftToTopLeft();
				} catch (HeadlessException | UnsupportedFlavorException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}});
		return mi;
	}



	public static void main(String[] args) {
		String path=DirectoryHandler.getDefaultHandler().getFigureFolderPath()+"/example.xlsx";
		try {
			 InputStream inp = new FileInputStream(path);
			    Workbook wb = WorkbookFactory.create(inp);
			   DataTable area2 = ExcelRowToJTable.DataTableFromWorkBookSheet(wb.getSheetAt(0));
		new SmartDataInputDialog(area2, PlotType.DEFAULT_PLOT_TYPE_COLS).showDialog();
		}
		
		
		catch (Throwable t) {
			t.printStackTrace();
		}
		
	}
	
	public void addButton(int i) {
		super.addButton(createButton(i));
	}
	
	public JButton createButton(int bType) {
		JButton output=null;
		if (bType==0) {
			output=new JButton("Create Column Plot");
			output.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					//IssueLog.log(getAllColumns() );
					
				}});
		}
		
		if (bType==5) {
			output=new JButton("Test Category Read");
			output.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					//not implemented
					
				}});
		}
		
		return output;
	}
	
	
	/**Makes a separate data series from each column*/
	public ArrayList<ColumnDataSeries> getAllColumns() {
		ArrayList<ColumnDataSeries> subparts=new ArrayList<ColumnDataSeries>();
		int nCol = getDataTable().getColumnCount();
	
		for(int i=0; i<nCol; i++) {
			Object value0 = getDataTable().getValueAt(0, i);
			String name="Column "+i;
			RowFilter filter=null;
			
			if (value0 instanceof String) {
				 filter=new RowFilter.HeaderExcludingFilter();
				 name=(String) value0;
			}
			
			ArrayList<BasicDataPoint> nums = this.getNumbers(i, filter);
			
			if (nums.size()>0) {
				ColumnDataSeries s = new ColumnDataSeries(name, nums);
				subparts.add(s);
			}
			
		}
		
		return subparts;
	}
	
	
	/**Makes a separate data series from each column*/
	public ArrayList<ColumnDataSeries> getDataSeriesUsingClassificationFolumn(int classColumn, int dataColumn,  DataTable area2) {
		ArrayList<String> u = UtilForDataReading.getUniqueStrings(getStrings(classColumn, new RowFilter.HeaderExcludingFilter(), area2));;
		ArrayList<ColumnDataSeries> subparts=new ArrayList<ColumnDataSeries>();
		
		for(String name: u) {
			
			RowFilter filter=new RowFilter.ColEqualFilter(name, classColumn);
			ArrayList<BasicDataPoint> nums = this.getNumbers(dataColumn, filter);
			
			if (nums.size()>0) {
				ColumnDataSeries s = new ColumnDataSeries(name, nums);
				subparts.add(s);
			}
			
		}
		
		return subparts;
	}
	
	public ArrayList<KaplanMeierDataSeries> getKaplanDataSeriesUsingDefaultClassification() {
		return getKaplanDataSeriesUsingDefaultClassification( area);
	}
	
	public static ArrayList<KaplanMeierDataSeries> getKaplanDataSeriesUsingDefaultClassification(TableReader area2) {
		int seriesNameCol=0;
		int censorCol=1;
		int timeCol=2;
		return getKaplanDataSeries(seriesNameCol, censorCol, timeCol, area2);
	}
	
	/**Experimental Kaplan-Meier data retrieval*/
	private static ArrayList<KaplanMeierDataSeries> getKaplanDataSeries(int seriesNameCol, int censorCol, int timeCol, TableReader area2) {
		ArrayList<KaplanMeierDataSeries> subparts=new ArrayList<KaplanMeierDataSeries>();
		ArrayList<String> u = UtilForDataReading.getUniqueStrings(getStrings(seriesNameCol, new RowFilter.HeaderExcludingFilter(), area2));;
	
		for(String name: u) {
			RowFilter filter=new RowFilter.ColEqualFilter(name, seriesNameCol);
			ArrayList<KaplenMeierDataPoint> nums = getPointsKaplan(censorCol, timeCol, filter, area2);

			if (nums.size()>0) {
				KaplanMeierDataSeries s = new KaplanMeierDataSeries(name, nums);
				subparts.add(s);
			}
			
		}
		
		return subparts;
	}

	public ArrayList<XYDataSeries> getXYDataSeriesUsingDefaultClassification() {
		int seriesNameCol=0;
		int xCol=1;
		int yCol=2;
		ArrayList<XYDataSeries> in = getXYDataSeriesUsingClassificationColumn(seriesNameCol, xCol, yCol);
		
		if (this.wrapSearch) {
			int max = area.lastNonEmptyColumn();
			while(yCol<=max) {
				seriesNameCol+=3;;
				xCol+=3;
				yCol+=3;
				ArrayList<XYDataSeries> in2 = getXYDataSeriesUsingClassificationColumn(seriesNameCol, xCol, yCol);
				if (in2.size()>0) in.addAll(in2);
			}
		}
		return in;
	}
	
	public ArrayList<XYDataSeries> getXYDataSeriesUsingClassificationColumn(int classColumn, int dataColumn, int dataColumn2) {
		ArrayList<XYDataSeries> subparts=new ArrayList<XYDataSeries>();
		ArrayList<String> u = UtilForDataReading.getUniqueStrings(getStrings(classColumn, new RowFilter.HeaderExcludingFilter(), area));;
	
		for(String name: u) {
			RowFilter filter=new RowFilter.ColEqualFilter(name, classColumn);
			ArrayList<BasicDataPoint> nums = this.getPoints(dataColumn,  dataColumn2, filter);
			
			if (nums.size()>0) {
				XYDataSeries s = new XYDataSeries(name, nums);
				subparts.add(s);
			}
			
		}
		
		return subparts;
	}
	
	public ArrayList<GroupedDataSeries> getCategoryDataSeriesUsingClassificationFolumn(int classColumn, int categoryColumn, int dataColumn) {
		ArrayList<GroupedDataSeries> subparts=new ArrayList<GroupedDataSeries>();
		ArrayList<String> classNames = UtilForDataReading.getUniqueStrings(getStrings(classColumn, new RowFilter.HeaderExcludingFilter(), area));;
		ArrayList<String> categoryNames = UtilForDataReading.getUniqueStrings(getStrings(categoryColumn, new RowFilter.HeaderExcludingFilter(), area));;
		HashMap<Double, String> map1 = GroupedDataSeries.createLocationMapFor(categoryNames);
	
		addGroupedSeriesToList(classColumn, categoryColumn, dataColumn, subparts, classNames, categoryNames, map1);
		
		return subparts;
	}

	/**This method adds new data series to the grouped data series*/
	private void addGroupedSeriesToList(int classColumn, int categoryColumn, int dataColumn,
			ArrayList<GroupedDataSeries> subparts, ArrayList<String> classNames, ArrayList<String> categoryNames,
			HashMap<Double, String> map1) {
		for(String name: classNames) {
			RowFilter filter=new RowFilter.ColEqualFilter(name, classColumn);
			
			ArrayList<ColumnDataSeries> eachCategory=new ArrayList<ColumnDataSeries>();
			
			for(String categoryName: categoryNames ) {
				RowFilter filter2=new RowFilter.ColEqualFilter(categoryName, categoryColumn);
				ArrayList<BasicDataPoint> nums = this.getNumbers(dataColumn, new RowFilter.CombinedFilter(filter, filter2));
				
				if (nums.size()>0) {
					ColumnDataSeries s = new ColumnDataSeries(categoryName, nums);
					 eachCategory.add(s);
				}
				
			}
			
			if (eachCategory.size()>0) {
			GroupedDataSeries out = new GroupedDataSeries(name, map1, eachCategory.toArray(new DataSeries[eachCategory.size()]) );
			subparts.add(out);
			} else {IssueLog.log("Having trouble geting data for categories  in "+name);}
		}
	}
	
	
	
	
	
	public JMenu createColumnPlotMakerMenu() {
		JMenu output = new JMenu("Create Column Plot");
		ArrayList<ColumnPlotCreator> list=new ArrayList<ColumnPlotCreator>();
		for (ColumnPlotStyle p : ColumnPlotCreator.ColumnPlotStyle.values()) {
			ColumnPlotCreator creator = new ColumnPlotCreator(p);
			list.add(creator);
			output.add(new PlotMakerMenuItem2(creator, this));
		}
		this.listOfPlotCreators=list;
		
		
		return output;
	}
	
	public JMenu createXYPlotMakerMenu() {
		JMenu output = new JMenu("Create XY Plot");
		ArrayList<XYPlotCreator> list=new ArrayList<XYPlotCreator>();
		for(xyPlotForm form: XYPlotCreator.xyPlotForm.values())
			{
			XYPlotCreator creater = new XYPlotCreator(form);
			list.add(creater);
			output.add(new PlotMakerMenuItem2(creater, this));
			}
		this.listOfPlotCreators=list;
		
		return output;
	}
	
	public JMenu createGroupedPlotMakerMenu() {
		JMenu output = new JMenu("Create Grouped Plot");
		
		ArrayList<GroupedPlotCreator> list=new ArrayList<GroupedPlotCreator>();
		listOfPlotCreators=list;
		list.add(new GroupedPlotCreator(Grouped_Plot.STAGGERED_BARS));
		list.add(new GroupedPlotCreator(Grouped_Plot.STACKED_BARS));
		list.add(new GroupedPlotCreator(Grouped_Plot.SEQUENTIAL_BARS));
		//list.add(new GroupedPlotCreator(Grouped_Plot.JITTER_POINTS));
		
		for(GroupedPlotCreator l:list) {
			output.add(new PlotMakerMenuItem2(l, this));
		}
		
		
		return output;
	}
	
	
	/**creates a plot creator meny option for the km plot*/
	public JMenu createKMPlotMakerMenu() {
		JMenu output = new JMenu("Create Kaplan Meier Plot");
		KaplanMeierPlotCreator maker = new KaplanMeierPlotCreator();
		
		ArrayList<KaplanMeierPlotCreator> list=new ArrayList<KaplanMeierPlotCreator>();
		listOfPlotCreators=list;
		list.add(maker);
		
		output.add(new PlotMakerMenuItem2(maker, this));

		return output;
	}
	
	
	

	
	class PlotMakerMenuItem2 extends JMenuItem implements ActionListener {
		
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private PlotCreator<?> maker2;
		private SmartDataInputDialog dialog;

		
		public PlotMakerMenuItem2(PlotCreator<?> maker, SmartDataInputDialog d) {
			this.maker2=maker;
			this.dialog=d;
			this.setText(maker.getNameText());
			this.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		
			if (maker2!=null) {
				maker2.createPlot("New Plot", dialog, CurrentFigureSet.getCurrentActiveDisplayGroup());
				
			}
		}
	}
	
	
	
	



/**Creates a data input dialog for editing of multiple series*/
public static SmartDataInputDialog createFrom(ArrayList<ColumnDataSeries> cols) {
	
	
	int longest=0;
	ArrayList<String> names=new ArrayList<String>();
	for(ColumnDataSeries s: cols) 
		{
		if (s.length()>longest) longest=s.length();
		names.add(s.getName());
		}
	
	DataTable area2 = new DataTable(longest+200, cols.size()+5 );
	
	
	int j=0;
	for(ColumnDataSeries s: cols) {
		ArrayList<? extends DataPoint> values2 = s.getDataPointList();
		for(int i=0; i<values2.size(); i++) {
			area2.setValueAt(values2.get(i).getValueString(), i+1, j);
		}
		area2.setValueAt(names.get(j), 0, j);
		j++;
	}
	
	return new SmartDataInputDialog(area2, null);
	

}


/**
private ArrayList<String> getEachSeriesNames(ArrayList<? extends DataSeries> starting) {
	ArrayList<String> names=new ArrayList<String>();
	for(DataSeries s: starting) 
	{
	names.add(s.getName());
	}
	return names;
}*/


private static int getTotalLengths(ArrayList<? extends DataSeries> starting) {
	int total=0;
	for(DataSeries s: starting) 
	{
		total+=s.length();
	}
	return total;
}

/**creates a j table for input of data*/
private static DataTable createDataTableToHold(ArrayList<? extends DataSeries> hold) {
	int minRows = getTotalLengths(hold);
	return new DataTable(minRows+200,3 );
}

/**Creates a data input dialog for editing of multiple series*/
public static SmartDataInputDialog createXYDataDialogFrom(ArrayList<XYDataSeries> starting) {
	
	DataTable area2=createDataTableToHold(starting);
	
	int row=1;
	for(XYDataSeries s: starting) {
		String namenew = s.getName();
		for(int i=0; i<s.length(); i++) {
			area2.setValueAt(namenew, row, 0);
			DataPoint pt = s.getDataPoint(i);
			
			area2.setValueAt(pt.getPosition(), row, 1);
			area2.setValueAt(pt.getValue()+(pt.isExcluded()? "*":""), row, 2);
			row++;
		}
		
		area2.setValueAt("Series Name", 0, 0);
		area2.setValueAt(s.getxName(),  0, 1);
		area2.setValueAt(s.getDependantVariableName(),  0, 2);
		
	}
	
	return new SmartDataInputDialog(area2, null);
	

}

/**Creates a data input dialog for editing of multiple series. Sets up the input array as the input data*/
public static SmartDataInputDialog createGroupedDataDialogFrom(ArrayList<GroupedDataSeries> starting) {
	boolean useSetOrder=true;
	DataTable area2=createDataTableToHold(starting);
	
	int row=1;
	for(GroupedDataSeries s: starting) {
		String namenew = s.getName();
		
		
		if (useSetOrder)  {
					HashMap<Double, String> map2 = s.getCategoryToLocationMap();
					for(double d=s.smallestPosition(); d<=s.largestPosition(); d+=1) {
						String part = map2.get(d);
						if (part==null) continue;
						DataSeries datapart = s.getValuesForPosition(part);
						if (datapart==null) continue;
						for(int i=0; i<datapart.length(); i++) {
						area2.setValueAt(namenew, row, 0);
						area2.setValueAt(datapart.getName(), row, 1);
						area2.setValueAt(datapart.getDataPoint(i).getValueString(), row, 2);
						row++;
					}
		}
		} 
		else for(int i=0; i<s.length(); i++) {
						area2.setValueAt(namenew, row, 0);
						area2.setValueAt(s.getCategoryOf(i), row, 1);
						area2.setValueAt(s.getDataPoint(i).getValueString(), row, 2);
						row++;
		}
		
		area2.setValueAt("Series Name", 0, 0);
		area2.setValueAt("Group Name" , 0, 1);
		area2.setValueAt("value"      , 0, 2);
		
	}
	
	return new SmartDataInputDialog(area2, null);
	

}

public static SmartDataInputDialog createKaplanDataDialogFrom(ArrayList<KaplanMeierDataSeries> cols) {
	DataTable area2=createDataTableToHold(cols);
	int row=1;
	for(KaplanMeierDataSeries s: cols) {
		String namenew = s.getName();
		for(int i=0; i<s.length(); i++) {
			area2.setValueAt(namenew, row, 0);
			KaplenMeierDataPoint dataP = s.getPointAt(i);
			area2.setValueAt(dataP.isCensored()? "Censored": "Dead", row, 1);
			area2.setValueAt(dataP.getSerialTime(), row, 2);
			row++;
		}
		
		area2.setValueAt("Series Name", 0, 0);
		area2.setValueAt("status",  0, 1);
		area2.setValueAt("Serial Time",  0, 2);
		
	}
	
	return new SmartDataInputDialog(area2, null);
}




public static SmartDataInputDialog showTableFromUserFile(boolean modal) {
	File f = FileChoiceUtil.getUserOpenFile();
	if (f.exists()) try {
		String s = FileChoiceUtil.readStringFrom(new FileInputStream(f));
		
		String[] ss = s.split(""+'\n');
		
		int nrow=ss.length;
		int ncol=12;
		for(String part: ss) {
			String[] tt = part.split(""+'\t');
			if (tt.length>ncol) ncol=tt.length;
		}
		
		DataTable areaNew = new DataTable(nrow+150, ncol+2);
		SmartDataInputDialog dialog = new SmartDataInputDialog(areaNew, PlotType.DEFAULT_PLOT_TYPE_COLS);
		dialog.getDataTable().putFileIntoTable(f);
		dialog.setModal(modal);
		dialog.showDialog();
		return dialog;
	} catch (Throwable t) {}
	
	return null;
}

public JMenu getPlotMakerMenu() {
	return plotMakerMenu;
}

public void setPlotMakerMenu(JMenu plotMakerMenu) {
	this.plotMakerMenu = plotMakerMenu;
}

public ArrayList<? extends PlotCreator<?>> getListOfPlotCreators() {
	return listOfPlotCreators;
}

/**returns the category of plot that this data table is for
 * @return
 */
public PlotType getPlotForm() {
	return form;
}



	
}
	
	
