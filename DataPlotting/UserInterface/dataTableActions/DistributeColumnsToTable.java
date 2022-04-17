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
 * Date Modified: April 5, 2022
 * Version: 2022.0
 */
package dataTableActions;

import java.io.File;
import java.io.IOException;

import dataTableDialogs.ExcelTableReader;
import dataTableDialogs.TableReader;
import figureFormat.DirectoryHandler;
import layout.RetrievableOption;
import logging.IssueLog;
import messages.ShowMessage;
import plateDisplay.ShowPlate;
import plates.Plate;
import plates.PlateCell;
import plates.PlateOrientation;
import storedValueDialog.StoredValueDilaog;

/**
 A table action that transforms a list of conditions into a plate setup file
 */
public class DistributeColumnsToTable extends BasicDataTableAction implements DataTableAction {

	
	
	@RetrievableOption(key = "sample", label="Sample names are in column #")
	public double sampleNameIndex=0;
	
	//@RetrievableOption(key = "A1", label="Will paste plate locations into column #")
	public double colAddressColumnIndex=3;
	
	@RetrievableOption(key = "col", label="plate has this many columns")
	public double nCol=12;
	
	@RetrievableOption(key = "row", label="plate has this many rows")
	public double nRow=8;
	
	@RetrievableOption(key = "skip", label="Skip rows/cols for replicates? (set to >0)")
	public double skip=0;
	
	@RetrievableOption(key = "block", label="Block samples ")
	public double blockSize=4;
	
	@RetrievableOption(key = "Input File With Sample names (.xlsx)", label="Input File With Sample names (.xlsx)")
	public File templateFile=new File("C:\\Users\\Greg Mazo\\Desktop\\example4.xlsx");
	
	@RetrievableOption(key = "Combine File with another? (optional)", label="Combine File with another? (optional)")
	public File templateFile2=null;
	
	@RetrievableOption(key = "roate", label="Distribute samples vertically")
	public boolean rotatePlate=false;
	
	@RetrievableOption(key = "h", label="First Line is header (always true)")
	public boolean headerPlate=true;
	
	
	
	
	
	@Override
	public String getNameText() {
		
		return "Distribute Rows To Plate Setup";
	}

	@Override
	public void processTableAction(TableReader item, DataTableActionContext context) {
		if(item!=null)
			templateFile=new File( item.getOriginalSaveAddress());
		StoredValueDilaog dd = new StoredValueDilaog("Distribute rows to a plate setup",  this);
		dd.setModal(true);
		dd.showDialog();
		if(dd.wasCanceled())
			return;
		
		item=new ExcelTableReader(templateFile);
		
		PlateOrientation po=PlateOrientation.STANDARD;
		if(rotatePlate)
			po=PlateOrientation.FLIP;
		Plate plate = new Plate((int)nRow,(int) nCol, po, (int)skip, (int)blockSize);
		
		
		item= new ExcelTableReader(templateFile);
		IssueLog.log("working on selected file "+item.getOriginalSaveAddress());
		
		if(templateFile2!=null) {
			ExcelTableReader secondTemplateTable = new ExcelTableReader(templateFile2);
			item=combinePlates(item, secondTemplateTable);
			sampleNameIndex=0;
			if(item.getColumnCount()>colAddressColumnIndex)
				colAddressColumnIndex=item.getColumnCount()+1;
		}
		
		distributeExcelRowsToPlate(plate, item);
		
	}

	/**
	 * @param item
	 * @param secondTemplateTable
	 * @return
	 */
	private TableReader combinePlates(TableReader item, ExcelTableReader secondTemplateTable) {
		int count=1;
		try {
			ExcelTableReader output = new ExcelTableReader();
			int row3=0;
			int transitionColumnIndex=item.getColumnCount();
			int shiftForward=2;
			for(int col1=0; col1<transitionColumnIndex; col1++) {
				output.setValueAt(item.getValueAt(0, col1), row3, shiftForward+col1);
			}
			for(int col2=0; col2<secondTemplateTable.getColumnCount(); col2++) {
				output.setValueAt(secondTemplateTable.getValueAt(0, col2), row3, shiftForward+col2+transitionColumnIndex);
			}
			row3=1;
			
			
			
			for(int row1=1; row1<=item.getRowCount(); row1++) {
				for(int row2=1; row2<=secondTemplateTable.getRowCount(); row2++) {
					
					
					for(int col1=0; col1<transitionColumnIndex; col1++) {
						output.setValueAt(item.getValueAt(row1, col1), row3, shiftForward+col1);
					}
					for(int col2=0; col2<secondTemplateTable.getColumnCount(); col2++) {
						output.setValueAt(secondTemplateTable.getValueAt(row2, col2), row3,shiftForward+ col2+transitionColumnIndex);
					}
					
					String combined = ""+item.getValueAt(row1, (int) sampleNameIndex)+" "+'\n'+secondTemplateTable.getValueAt(row2, (int) sampleNameIndex);
					output.setValueAt(combined,row3, 0);
					output.setValueAt(count,row3, 1);
					count++;
					output.setValueAt("Combined Names ",0,1);
					row3++;
					
				}
				
				
			}
			return output;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	/**
	 * @param plate
	 * @param item
	 */
	private void distributeExcelRowsToPlate(Plate plate, TableReader item) {
		
		int total=item.getRowCount();
		
		item.setValueAt("plate_location", 0, (int) colAddressColumnIndex);
		for(int i=1; i<=total; i++) {
			String index = plate.getIndexAddress(i-1);
			if(i-1>=plate.getPlateCells().size()) {
				ShowMessage.showOptionalMessage("Too many samples", true, "You have to many samples for this plate size");
				break;
			}
			PlateCell plateCell = plate.getPlateCells().get(i-1);
			plateCell.setSpreadSheetRow(i);
			plateCell.setShortName(item.getValueAt(i, (int) sampleNameIndex));
		
			item.setValueAt(index, i, (int) colAddressColumnIndex);
		}
		TableReader sheet2=item.createNewSheet("Sample setup");
		new  ShowPlate().showPlate(sheet2, plate);
		
		String oSave=item.getOriginalSaveAddress();
		if(oSave!=null&&oSave.contains(".xlsx"))
			{
				oSave=oSave.replace(".xlsx", "_with_plate_locations.xlsx");
				oSave=findUniqueOutputFileNam(oSave, ".xlsx");
			}
		
		if(oSave==null)
			oSave=findOutputFileNam();
		
		item.saveTable(true, oSave);
		
	}

	
	/**returns a non existent output file
	 * @return
	 */
	private String findOutputFileNam() {
		int count=1;
		String name="output";
		String extension=".xlsx";
		name=DirectoryHandler.getDefaultHandler().getFigureFolderPath()+File.separator+name;
		File f=new File(name+extension);
		while(f.exists()) {
			 f=new File(name+"_"+count+extension);
			 count++;
		}
		return f.getAbsolutePath();
	}
	
	/**returns a non existent output file
	 * @return
	 */
	private String findUniqueOutputFileNam(String output, String extension) {
		int count=1;
		
		if(output.endsWith(extension))
			output=output.replace(extension,"");
		String name=output;
		
		File f=new File(name+extension);
		while(f.exists()) {
			 f=new File(name+"_"+count+extension);
			 count++;
		}
		return f.getAbsolutePath();
	}

	public static void main(String[] args) {
		IssueLog.sytemprint=true;
		new DistributeColumnsToTable().performActionDisplayedImageWrapper(null);
	}

}
