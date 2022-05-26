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
 * Date Modified: May 26, 2022
 * Version: 2022.1
 */
package dataTableActions;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;

import dataTableDialogs.DataTable;
import dataTableDialogs.ExcelTableReader;
import dataTableDialogs.TableReader;
import figureFormat.DirectoryHandler;
import layout.RetrievableOption;
import logging.IssueLog;
import messages.ShowMessage;
import objectDialogs.CroppingDialog.AllOKListener;
import plateDisplay.PlateDisplayGui;
import plateDisplay.ShowPlate;
import plates.BasicCellAddress;
import plates.Plate;
import plates.PlateCell;
import plates.PlateOrientation;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialogListener;
import standardDialog.graphics.GraphicComponent;
import storedValueDialog.StoredValueDilaog;

/**
 A table action that transforms a list of conditions into a plate setup file
 */
public class DistributeColumnsToTable extends BasicDataTableAction implements DataTableAction {

	
	
	
	
	//@RetrievableOption(key = "A1", label="Will paste plate locations into column #")
	public double colAddressColumnIndex=3;
	
	@RetrievableOption(key = "col", label="# Columns")
	public double nCol=12;
	
	@RetrievableOption(key = "row", label="# Rows")
	public double nRow=8;
	
	@RetrievableOption(key = "skip", label="How many replicates?")
	public double skip=3;
	
	@RetrievableOption(key = "block", label="Group samples ")
	public double blockSize=6;
	
	@RetrievableOption(key = "flip group", label="Flip group orientation")
	public boolean flipGroup=false;
	
	@RetrievableOption(key = "Input File With Sample names (.xlsx)", label="Input File 1 (.xlsx)")
	public File templateFile=null;
	
	@RetrievableOption(key = "Combine File with another? (optional)", label="Combine with File2? (optional)")
	public File templateFile2=null;
	
	@RetrievableOption(key = "rotate plate", label="Distribute samples vertically")
	public boolean rotatePlate=false;
	
	@RetrievableOption(key = "show names", label="Preview sample names")
	public boolean showSampleNames=false;
	@RetrievableOption(key = "sample", label="names are in col #")
	public double sampleNameIndex=0;
	
	
	PlateDisplayGui diplay=new PlateDisplayGui("untitled plate", new Plate());

	private StoredValueDilaog currentDialog;
	
	
	@Override
	public String getNameText() {
		
		return "Distribute Rows To Plate Setup";
	}

	@Override
	public void processTableAction(TableReader item, DataTableActionContext context) {
		if(item!=null)
			templateFile=new File( item.getOriginalSaveAddress());
		currentDialog = new StoredValueDilaog("Distribute rows to a plate setup",  this);
		GraphicComponent comp = new GraphicComponent();
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx=5;
		gc.gridy=1;
		comp.setPrefferedSize(600, 420);
		comp.setMagnification(0.8);
		comp.getGraphicLayers().add(diplay);
		currentDialog.add(comp, gc);
		updatePlateDisplayAfterDialogChange();
		currentDialog.addDialogListener(new StandardDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				
				updatePlateDisplayAfterDialogChange();
				
			}

			});
		JButton spreadsheet=new JButton("Create Spreadsheet"); {spreadsheet.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				buildPlate(true);
				
			}});}
		currentDialog.addButton(spreadsheet);
		//currentDialog.setModal(true);
		currentDialog.showDialog();
		if(currentDialog.wasCanceled())
			return;
		
		
		
	}

	/**
	 * 
	 */
	public void updatePlateDisplayAfterDialogChange() {
		try {
			diplay.setShowSampleNames(this.showSampleNames);
			diplay.setPlate(buildPlate(false));
		} catch (Exception e) {
			IssueLog.log("failed to build plate. Make sure a valid excel file is selected");
			IssueLog.logT(e);
		}
		diplay.updateDisplay();
		currentDialog.repaint();
	}
	
	
	/**
	 * @return 
	 * 
	 */
	public Plate buildPlate(boolean createFile) {
		Plate plate = createPlate();
		
		
		TableReader item=openExcelFile(templateFile);
		if(item==null)
				try
			{item=createExampleSheetForPlate(plate);}
				catch (Throwable t) {
			IssueLog.log("failed to create table");
		}
		
		
		ExcelTableReader secondTemplateTable = openExcelFile(templateFile2);
		if(secondTemplateTable!=null) {
			
			item=combinePlates(item, secondTemplateTable);
			sampleNameIndex=0;
			if(item.getColumnCount()>colAddressColumnIndex)
				colAddressColumnIndex=item.getColumnCount()+1;
		}
		
		distributeExcelRowsToPlate(plate, item, createFile);
		return plate;
	}

	/**
	 * @return
	 */
	public ExcelTableReader openExcelFile(File templateFile) {
		if(templateFile==null)
			return null;
		if(!templateFile.exists())
			return null;
		if(templateFile.getAbsolutePath().endsWith(".xlsx"))
			return new ExcelTableReader(templateFile);
		IssueLog.log("The file is not an excel file", templateFile.getAbsolutePath(), "");
		return null;
	}

	/**
	 * @param plate
	 * @return
	 * @throws IOException 
	 */
	private TableReader createExampleSheetForPlate(Plate plate) throws IOException {
		ExcelTableReader table = new ExcelTableReader();
		table.setValueAt("Numbers", 0, (int)sampleNameIndex);
		int nToFill = (int) (plate.getNRow()*plate.getNCol()/this.getNReplicates());
		for(int i=1; i<nToFill+1; i++) {
			table.setValueAt(i+"", i, (int)sampleNameIndex);
		}
		return table;
	}

	/**
	 * @return
	 */
	public Plate createPlate() {
		PlateOrientation po=PlateOrientation.STANDARD;
		if(rotatePlate)
			po=PlateOrientation.FLIP;
		Plate plate = new Plate((int)nRow,(int) nCol, po, (int)getNReplicates(), (int)blockSize, this.flipGroup);
		return plate;
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

	/**iterates through the rows of the excel sheet and creates a column for the plate well assignments 
	 * @param plate
	 * @param tableAssignment
	 * @param createFile 
	 */
	private void distributeExcelRowsToPlate(Plate plate, TableReader tableAssignment, boolean createFile) {
		
		int total=tableAssignment.getRowCount();
		
		
		tableAssignment.setValueAt("plate_location", 0, (int) colAddressColumnIndex);
	
		int rowCount = tableAssignment.getRowCount();
		int numberCellsNeeded = (int) ((rowCount-1)*this.getNReplicates());
		int nWells = plate.getNCol()*plate.getNRow();
		int nPlatesNeeded = numberCellsNeeded/nWells;//number plates that can be filled completely
	
		if(nPlatesNeeded%nWells>0)
			nPlatesNeeded++;//one more plate may be needed. this plate will be not filled completely
		ArrayList<Plate> plates = new ArrayList<Plate>();
		plates.add(plate);
		for(int i=1; i<nPlatesNeeded; i++) {
			plates.add(plate.createSimilar());
		}
		if(plates.size()>1) {
			for(int i=1; i<=plates.size(); i++) {
				plates.get(i-1).setPlateName("Plate"+i+"");
			}
		}
		int plateNumber = 0;
		Plate currentPlate = plate;
		int cellIndex=1;
		for(int i=1; i<=total; i++)try {
			for(int j=0; j<getNReplicates(); j++) {
			
			Object val = tableAssignment.getValueAt(i, (int) colAddressColumnIndex);
			if(val==null)
				val="";
			
			
			//BasicCellAddress indexAddress = plate.getIndexAddress(i-1);
			//String plateAddressAt = indexAddress.getAddress();
			if(cellIndex-1>=plate.getPlateCells().size()) {
				cellIndex=1;
				plateNumber++;
				if(plates.size()<=plateNumber)
					break;
				currentPlate=plates.get(plateNumber);
				//ShowMessage.showOptionalMessage("Too many samples", true, "You have to many samples for this plate size");
				//break;
			}
			PlateCell plateCell =currentPlate.getPlateCells().get(cellIndex-1);
			plateCell.setSpreadSheetRow(i);
			plateCell.setShortName(tableAssignment.getValueAt(i, (int) sampleNameIndex));
			plateCell.setSpreadSheetRow(i);
			plateCell.setSourceSheetName(tableAssignment.getSheetName(0)+"");
			String plateAddressAt = plateCell.getAddress().getAddress();
			
			String newText=val+"";
			if(newText.length()>0)
				newText+=", ";
			
			newText+=plateAddressAt;
			tableAssignment.setValueAt(newText, i, (int) colAddressColumnIndex);
			if(plate.getPlateName().length()>0)
				{
				String newText2 = currentPlate.getPlateName();
				tableAssignment.setValueAt(newText2, i, (int) colAddressColumnIndex+1);
				tableAssignment.setValueAt("plate_name", 0,(int) colAddressColumnIndex+1);
				if(newText.contains(","))
					newText=newText.replace(", ", ", "+newText2+"-");
				newText=newText2+"-"+newText;
				tableAssignment.setValueAt(newText, i, (int) colAddressColumnIndex+2);
				tableAssignment.setValueAt("full_location", 0,(int) colAddressColumnIndex+2);
				}
			
			cellIndex++;
			}
		} catch (Throwable t) {
			IssueLog.log("plate location distributor failed at "+i);
			IssueLog.logT(t);
		}
		
		/**Creates a sample setup sheet*/
		for(int i=0; i<plates.size(); i++) {
			Plate p=plates.get(i);
			String tabname = "Sample setup";
			if(plates.size()>1)
				tabname+=p.getPlateName();
			TableReader sheet2=tableAssignment.createNewSheet(tabname);
			new  ShowPlate().showPlate(sheet2, p);
			}
		
		if(createFile) {
			String oSave=tableAssignment.getOriginalSaveAddress();
			
			if(oSave!=null&&oSave.contains(".xlsx"))
				{
				File file = new File(oSave);
				String outputFolder = file.getParentFile().getAbsolutePath()+"/plate_setup/";
				if(!new File(outputFolder).exists())
					new File(outputFolder).mkdirs();
					oSave=oSave.replace(".xlsx", "_with_plate_locations.xlsx");
					oSave= outputFolder+new File(oSave).getName();
					oSave=findUniqueOutputFileNam(oSave, ".xlsx");
				}
			
			if(oSave==null)
				oSave=findOutputFileNam();
			
			tableAssignment.saveTable(true, oSave);
		}
	}

	/**
	 * @return
	 */
	public double getNReplicates() {
		if(skip<1)
			return 1;
		return skip;
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
