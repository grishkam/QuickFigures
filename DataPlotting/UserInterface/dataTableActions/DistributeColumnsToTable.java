/**
 * Author: Greg Mazo
 * Date Modified: Mar 26, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package dataTableActions;

import dataTableDialogs.TableReader;
import graphicalObjects.ZoomableGraphic;
import logging.IssueLog;
import plateDisplay.ShowPlate;
import plates.Plate;
import plates.PlateCell;

/**
 
 * 
 */
public class DistributeColumnsToTable extends BasicDataTableAction implements DataTableAction {

	int sampleNameIndex=0;
	int colAddressColumnIndex=3;
	
	@Override
	public String getNameText() {
		
		return "Distribute Rows To Plate Setup";
	}

	@Override
	public void processTableAction(TableReader item, DataTableActionContext context) {
		
	
		Plate plate = new Plate();
		
		distributeExcelRowsToPlate(plate, item);
		
	}

	/**
	 * @param plate
	 * @param item
	 */
	private void distributeExcelRowsToPlate(Plate plate, TableReader item) {
		IssueLog.sytemprint=true;
		int total=item.getRowCount();
		
		item.setValueAt("plate_location", 0, colAddressColumnIndex);
		for(int i=1; i<=total; i++) {
			String index = plate.getIndexAddress(i-1);
			PlateCell plateCell = plate.getPlateCells().get(i-1);
			plateCell.setSpreadSheetRow(i);
			plateCell.setShortName(item.getValueAt(i, sampleNameIndex));
		
			item.setValueAt(index, i, colAddressColumnIndex);
		}
		TableReader sheet2=item.createNewSheet("Sample setup");
		new  ShowPlate().showPlate(sheet2, plate);
		
		item.saveTable(true);
		
	}

	
	public static void main(String[] args) {
		new DistributeColumnsToTable().performActionDisplayedImageWrapper(null);
	}

}
