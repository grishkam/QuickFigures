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
 * Date Modified: April 28, 2021
 * Version: 2023.2
 */


import java.util.ArrayList;

import appContextforIJ1.ImageDisplayTester;
import applicationAdapters.StartApplication;
import basicMenusForApp.MenuBarForApp;
import basicMenusForApp.MenuBarItemInstaller;
import dataTableActions.DataTableAction;
import dataTableActions.DataTableMenu;
import dialogs.DataShapeSyncer;
import fileread.PlotExampleShower;
import fileread.ExcelFileToBarPlot;
import fileread.ExcelFileToComplexCategoryPlot;
import fileread.ExcelFileToKaplanPlot;
import fileread.ExcelFileToXYPlot;
import fileread.ExcelRowToJTable;
import fileread.PlotType;
import fileread.ShowTable;
import genericTools.NormalToolDragHandler;
import groupedDataPlots.Grouped_Plot;
import imageDisplayApp.ImageWindowAndDisplaySet;
import includedToolbars.ObjectToolset1;
import includedToolbars.QuickFiguresToolBar;
import includedToolbars.ToolInstallers;
import lineprofile.ProfileLineTool;
import logging.IssueLog;
import plotCreation.ColumnPlotCreator;
import plotCreation.ColumnPlotCreator.ColumnPlotStyle;
import plotCreation.XYPlotCreator;
import plotCreation.XYPlotCreator.xyPlotForm;
import plotTools.ColumnSwapTool;
import plotTools.TTestTool;
import selectedItemMenus.SelectionOperationsMenu;

/**this class contains methods to install the plot package onto QuickFigure*/
public class StartWithPlotPackage extends StartApplication implements MenuBarItemInstaller, ToolInstallers{

	static boolean alreadyInstalled=false;
	
	public static void main(String[] args) {
		IssueLog.sytemprint=false;
		IssueLog.windowPrint=true;
		installPlotPackageOntoQuickFigures();
		startToolbars(true);
		ImageDisplayTester.setupImageJ();
		 ImageWindowAndDisplaySet.createAndShowNew("Figure", 400,300);
		
	}
	
	/***Installs the plot package*/
	public static void installPlotPackageOntoQuickFigures() {
		if (alreadyInstalled) return;
		StartWithPlotPackage freeRun = new  StartWithPlotPackage();
		
		MenuBarForApp.addMenuBarItemInstaller(freeRun);
		ObjectToolset1.includeBonusTool(new StartWithPlotPackage());
		
		for(int i=0; i<=5; i++)
			SelectionOperationsMenu.addNewOperator(new DataShapeSyncer(i));
		NormalToolDragHandler.fileDropExtras.add(new ExcelRowToJTable());
		;
		
		alreadyInstalled=true;
		
	}

	/**Adds the menu items for the plot package onto the menu bar*/
	@Override
	public void addToMenuBar(MenuBarForApp installer) {
		try {
			PlotType[] types=new PlotType[] {PlotType.DEFAULT_PLOT_TYPE_COLS, PlotType.XY_PLOT_TYPE, PlotType.GROUP_PLOT_TYPE, PlotType.KAPLAN_MEIER_PLOT_TYPE};
			for(PlotType t: types)
			installer.installItem(new PlotExampleShower(t, true));
			
			
			for(PlotType t: types)
				installer.installItem(new PlotExampleShower(t, false));
			
		
		for(ColumnPlotStyle plotform: ColumnPlotCreator.ColumnPlotStyle.values())
			{
			installer.installItem(new ExcelFileToBarPlot(plotform, true));
			installer.installItem(new ExcelFileToBarPlot(plotform, false));
			}
		
		for(xyPlotForm form:XYPlotCreator.xyPlotForm.values()) installer.installItem(new ExcelFileToXYPlot(form));
		
		installer.installItem(new ExcelFileToComplexCategoryPlot(Grouped_Plot.STAGGERED_BARS));
		installer.installItem(new ExcelFileToComplexCategoryPlot(Grouped_Plot.STACKED_BARS));
		installer.installItem(new ExcelFileToComplexCategoryPlot(Grouped_Plot.SEQUENTIAL_BARS));
		installer.installItem(new ExcelFileToComplexCategoryPlot(Grouped_Plot.JITTER_POINTS));
		installer.installItem(new ExcelFileToKaplanPlot());
		installer.installItem(new ExcelRowToJTable());
		installer.installItem(new ShowTable(ShowTable.OPEN_FILE));
		installer.installItem(new ShowTable(ShowTable.NEW_TABLE));
		ArrayList<DataTableAction> tableActions = DataTableMenu.createActions();
		
		for(DataTableAction a: tableActions) {
			installer.installItem(a);
		}
		
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/***Adds the plot package tools to the given toolbar*/
	@Override
	public void installTools(QuickFiguresToolBar toolset) {
		try {toolset.addToolBit(new TTestTool());} catch (Throwable t) {
			t.printStackTrace();
		}
		toolset.addToolBit(new ColumnSwapTool());
		toolset.addToolBit(new ProfileLineTool());
	}
	


}
