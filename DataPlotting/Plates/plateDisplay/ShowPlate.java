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
 * Date Created: Mar 26, 2022
 * Date Modified: May 19, 2022
 * Version: 2022.2
 */
package plateDisplay;

import java.awt.Color;
import java.util.ArrayList;

import dataTableDialogs.DataTable;
import dataTableDialogs.SmartDataInputDialog;
import dataTableDialogs.TableReader;
import fileread.PlotType;
import logging.IssueLog;
import plates.BasicCellAddress;
import plates.Plate;
import plates.PlateCell;

/**
 
 * 
 */
public class ShowPlate {

	/**formulas that reference another sheet are not automatically understood to be formulas by libre office nad excel
	 * this feature is not yet implemented*/
	boolean useFormula=false;
	
	public void showVisiblePlate(Plate p) {
		SmartDataInputDialog dialog =  new SmartDataInputDialog(new DataTable(300, 15), PlotType.COLUMN_PLOT_TYPE);
		showPlate(dialog.getDataTable(), p);
		dialog.showDialog();;
	}
	
	public void showPlate(TableReader table, Plate plate) {
		
		/**for(int i=0; i<plate.getNRows(); i++) {
			table.setValueAt(Plate.getCharForIndex(i), i+1, 0);
		}*/
		
		for(int i=0; i<plate.getNCol(); i++) {
			int i3 = i+1;
			if(plate.addressMod!=null)
				i3+=plate.addressMod.getColShift();
			table.setValueAt(""+i3,0, i+1);
		}
		for(int i=0; i<plate.getNRow(); i++) {
			int i2 = i;
			if(plate.addressMod!=null)
				i2+=plate.addressMod.getRowShift();
			table.setValueAt(""+BasicCellAddress.getCharForIndex(i2), i+1, 0);
		}
		
		if(plate.hues!=null) {
			ArrayList<Color> c=new ArrayList<Color>();
			c.addAll(plate.hues.values());
			table.setupColorMap( c);
		} else {
			IssueLog.log("group hues not setup yet");
		}
		
		for(PlateCell cell: plate.getPlateCells()) try {
			BasicCellAddress index = cell.getAddress();
			int rowR = index.getRow()+1;
			int ColC = index.getCol()+1;
			String shortLabel = cell.getShortLabel();
			if(this.useFormula&&cell.getSpreadSheetRow()!=null) {
				shortLabel="="+cell.getSourceSheetName()+"!"+".A"+cell.getSpreadSheetRow();
				
			}
				table.setValueAt(shortLabel, rowR, ColC);
			table.setWrapTextAt(rowR, ColC);
			
			table.setCellColor(cell.getColor(), rowR, ColC);
		} catch (Throwable t) {
			IssueLog.log("something went wrong  "+cell.getAddress());
			t.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		new ShowPlate().showVisiblePlate(new Plate());
	}
}
