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
 * Date Modified: Mar 26, 2022
 * Version: 2022.1
 */
package plateDisplay;

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
			table.setValueAt(""+(i+1),0, i+1);
		}
		for(int i=0; i<plate.getNRow(); i++) {
			table.setValueAt(""+BasicCellAddress.getCharForIndex(i), i+1, 0);
		}
		
		for(PlateCell cell: plate.getPlateCells()) try {
			BasicCellAddress index = cell.getAddress();
			table.setValueAt(cell.getShortLabel(), index.getRow()+1, index.getCol()+1);
			table.setWrapTextAt(index.getRow()+1, index.getCol()+1);
		} catch (Throwable t) {
			IssueLog.log("not a valid plate address ");
		}
	}
	
	
	public static void main(String[] args) {
		new ShowPlate().showVisiblePlate(new Plate());
	}
}
