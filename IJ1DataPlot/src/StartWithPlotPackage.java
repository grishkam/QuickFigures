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



import appContextforIJ1.ImageDisplayTester;
import applicationAdapters.StartApplication;
import basicMenusForApp.MenuBarForApp;
import basicMenusForApp.MenuBarItemInstaller;
import dialogs.DataShapeSyncer;
import fileread.PlotExampleShower;
import fileread.ExcelFileToBarPlot;
import fileread.ExcelFileToComplexCategoryPlot;
import fileread.ExcelFileToKaplanPlot;
import fileread.ExcelFileToXYPlot;
import fileread.ExcelRowToJTable;
import fileread.PlotType;
import fileread.ShowTable;
import imageDisplayApp.ImageWindowAndDisplaySet;
import includedToolbars.ObjectToolset1;
import includedToolbars.QuickFiguresToolBar;
import includedToolbars.ToolInstallers;
import logging.IssueLog;
import plotCreation.ColumnPlotCreator;
import plotCreation.ColumnPlotCreator.ColumnPlotStyle;
import plotCreation.XYPlotCreator;
import plotCreation.XYPlotCreator.xyPlotForm;
import plotTools.ColumnSwapTool;
import plotTools.TTestTool;
import selectedItemMenus.SelectionOperationsMenu;

public class StartWithPlotPackage extends StartApplication implements MenuBarItemInstaller, ToolInstallers{

		static boolean alreadyInstalled=false;
	
	public static void main(String[] args) {
		IssueLog.sytemprint=false;
		IssueLog.windowPrint=true;
		install();
		startToolbars(true);
		ImageDisplayTester.setupImageJ();
		 ImageWindowAndDisplaySet.createAndShowNew("Figure", 400,300);
		
	}
	
	public static void install() {
		if (alreadyInstalled) return;
		StartWithPlotPackage freeRun = new  StartWithPlotPackage();
		MenuBarForApp.addMenuBarItemInstaller(freeRun);
		ObjectToolset1.includeBonusTool(new StartWithPlotPackage());
		
		for(int i=0; i<=5; i++)SelectionOperationsMenu.addNewOperator(new DataShapeSyncer(i));
	
		alreadyInstalled=true;
		
	}

	@Override
	public void addToMenuBar(MenuBarForApp installer) {
		try {
			PlotType[] types=new PlotType[] {PlotType.DEFAULT_PLOT_TYPE_COLS, PlotType.XY_PLOT_TYPE, PlotType.GROUP_PLOT_TYPE, PlotType.KAPLAN_MEIER_PLOT_TYPE};
			for(PlotType t: types)
			installer.installItem(new PlotExampleShower(t, true));
			
			
			for(PlotType t: types)
				installer.installItem(new PlotExampleShower(t, false));
			
		
		for(ColumnPlotStyle plotform: ColumnPlotCreator.ColumnPlotStyle.values())
			installer.installItem(new ExcelFileToBarPlot(plotform));
		
		for(xyPlotForm form:XYPlotCreator.xyPlotForm.values()) installer.installItem(new ExcelFileToXYPlot(form));
		
		installer.installItem(new ExcelFileToComplexCategoryPlot(0));
		installer.installItem(new ExcelFileToComplexCategoryPlot(1));
		installer.installItem(new ExcelFileToComplexCategoryPlot(2));
		installer.installItem(new ExcelFileToComplexCategoryPlot(3));
		installer.installItem(new ExcelFileToKaplanPlot());
		installer.installItem(new ExcelRowToJTable());
		installer.installItem(new ShowTable(1));
		installer.installItem(new ShowTable(9));
		
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public void installTools(QuickFiguresToolBar toolset) {
		try {toolset.addToolBit(new TTestTool());} catch (Throwable t) {
			t.printStackTrace();
		}
		toolset.addToolBit(new ColumnSwapTool());
	}
	


}
