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
 * Date Modified: Feb 6, 2023
 * Version: 2022.2
 */
package dataTableActions;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;

import channelMerging.ChannelEntry;
import dataTableDialogs.ExcelTableReader;
import dataTableDialogs.TableReader;
import figureFormat.DirectoryHandler;
import graphicalObjects.BasicGraphicalObject;
import layout.RetrievableOption;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import menuUtil.BasicSmartMenuItem;
import menuUtil.SmartPopupJMenu;
import messages.ShowMessage;
import plateDisplay.PlateDisplayGui;
import plateDisplay.ShowPlate;
import plates.AddressModification;
import plates.BasicCellAddress;
import plates.Plate;
import plates.PlateCell;
import plates.PlateOrientation;
import plates.SheetAssignment;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialog;
import standardDialog.StandardDialogListener;
import standardDialog.graphics.GraphicComponent;
import standardDialog.graphics.GraphicComponent.CanvasMouseListener;
import storedValueDialog.FileSlot;
import storedValueDialog.StoredValueDilaog;
import ultilInputOutput.FileChoiceUtil;
import undo.AbstractUndoableEdit2;

/**
 A table action that transforms a list of conditions into a plate setup file
 */
public class DistributeColumnsToTable extends BasicDataTableAction implements DataTableAction, CanvasMouseListener {

	
	
	
	
	//@RetrievableOption(key = "A1", label="Will paste plate locations into column #")
	public double colAddressColumnIndex=2;
	
	@RetrievableOption(key = "col", label="# Columns")
	public double nCol=12;
	
	@RetrievableOption(key = "row", label="# Rows")
	public double nRow=8;
	
	@RetrievableOption(key = "skip", label="How many replicates?")
	public double skip=3;
	
	@RetrievableOption(key = "block", label="Group samples ")
	public double blockSize=0;
	
	@RetrievableOption(key = "flip group", label="Flip group orientation")
	public boolean flipGroup=false;
	
	@RetrievableOption(key = "Input File With Sample names (.xlsx)", label="Input File 1 (.xlsx)", note="Excel")
	public FileSlot templateFile=new FileSlot(true);
	
	@RetrievableOption(key = "Combine File with another? (optional)", label="Combine with File2? (optional)", note="Excel")
	public FileSlot templateFile2=new FileSlot(templateFile);

	
	
	@RetrievableOption(key = "rotate plate", label="Distribute samples vertically")
	public boolean rotatePlate=false;
	
	/**the names of the samples that will be seen in the spreadsheet */
	@RetrievableOption(key = "show names", label="Preview sample names")
	public boolean showSampleNames=false;
	
	
	//@RetrievableOption(key = "sample", label="names are in col #")
//public double getSampleNameIndex()=0;
	
	
	@RetrievableOption(key = "sample", label="names are in col #")
	public ColumnSlotList col1=new ColumnSlotList(
			new ColumnSlot(templateFile, new ChannelEntry("first column", 0))
			//,new ColumnSlot(templateFile, new ChannelEntry("next column", 1))
				);
	
	@RetrievableOption(key = "row shift", label="shift rows", category="special")
	public double rowShift=0;
	
	@RetrievableOption(key = "col shift", label="shift cols", category="special")
	public double colShift=0;
	
	@RetrievableOption(key = "display plate", label="show plate preview", category="special")
	public double displayPlate=0;
	
	int sheetIndex=0;
	
	
	PlateDisplayGui diplay=new PlateDisplayGui("untitled plate", new Plate());//displays the setup for this plate

	private StoredValueDilaog currentDialog;

	private PlateCell cellPress;

	private PlateCell cellRelease;

	private GraphicComponent comp;

	private PlateCell cellDrag;

	private ArrayList<PlateCell> selectedCells=new ArrayList<PlateCell> ();
	
	private ArrayList<PlateCell> bannedCells=new ArrayList<PlateCell> ();
	
	private HashMap<SheetAssignment, PlateCell> manualCells=new HashMap<SheetAssignment, PlateCell> ();
	
	@Override
	public String getNameText() {
		
		return "Create sample_setup file";
	}

	@Override
	public void processTableAction(TableReader item, DataTableActionContext context) {
		if(item!=null)
			setSampleListFile(new File( item.getOriginalSaveAddress()));
		currentDialog = new StoredValueDilaog("Distribute rows to a plate setup",  this, "general");
		currentDialog.setHideOK(true);
		 comp = new GraphicComponent();
		comp.addComponentMouseListener(this);
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx=5;
		gc.gridy=1;
		comp.setPrefferedSize(540, 378);
		comp.setMagnification(0.70);
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
		spreadsheet.setBackground(Color.green);
		spreadsheet.setFont(new Font("Arial",  Font.BOLD, 24));
		currentDialog.addButton(spreadsheet);
		//currentDialog.setModal(true);
		currentDialog.showDialog();
		if(currentDialog.wasCanceled())
			return;
		
		
		
	}

	/**
	 * @param file
	 */
	private void setSampleListFile(File file) {
		templateFile.setFile(file);
		
	}

	/**
	 * 
	 */
	public void updatePlateDisplayAfterDialogChange() {
		try {
			diplay.updatePlateDisplay();
			diplay.setShowSampleNames(this.showSampleNames);
			ArrayList<Plate> buildPlate = buildPlate(false);
			
			diplay.setPlate(buildPlate, (int) displayPlate);
			diplay.updatePlateDisplay();
		} catch (Exception e) {
			IssueLog.log("failed to build plate. Make sure a valid excel file is selected");
			IssueLog.logT(e);
		}
		diplay.updateDisplay();
		currentDialog.repaint();
	}
	
	
	/**creates a plate based on the currently settings
	 * @return 
	 * 
	 */
	public ArrayList<Plate> buildPlate(boolean createFile) {
		Plate plate = createPlate();
		
		
		TableReader item=ExcelTableReader.openExcelFile(getSampleListFile());
		
		
		
		if(item==null)
				try
			{item=createExampleSheetForPlate(plate);}
				catch (Throwable t) {
			IssueLog.log("failed to create table");
		}
		
		int columnCount = item.getColumnCount();
		
			
		
		
		ExcelTableReader secondTemplateTable = ExcelTableReader.openExcelFile(getSecondFile());
		if(secondTemplateTable!=null) {
			
			item=combinePlates(item, secondTemplateTable);
			setSampleNameIndex(0);
			if(columnCount>colAddressColumnIndex)
				colAddressColumnIndex=columnCount;
		}
		
		return distributeExcelRowsToPlate(plate, item, createFile);
		//return plate;
	}



	/**
	 * @param i
	 */
	private void setSampleNameIndex(int i) {
		 col1.setIndex(0,i);
		
	}

	/**
	 * @return
	 */
	public File getSampleListFile() {
		return templateFile.getFile();
	}

	/**returns the file that will be combined with the first
	 * @return
	 */
	public File getSecondFile() {
		return templateFile2.getFile();
	}

	

	/**
	 * @param plate
	 * @return
	 * @throws IOException 
	 */
	private TableReader createExampleSheetForPlate(Plate plate) throws IOException {
		ExcelTableReader table = new ExcelTableReader();
		table.setValueAt("Numbers", 0, (int)getSampleNameIndex());
		int nToFill = (int) (plate.getNRow()*plate.getNCol()/this.getNReplicates());
		for(int i=1; i<nToFill+1; i++) {
			table.setValueAt(i+"", i, (int)getSampleNameIndex());
		}
		return table;
	}

	/**
	 * @return
	 */
	private int getSampleNameIndex() {
		return (int)col1.getIndex(0);
	}

	/**
	 * @return
	 */
	public Plate createPlate() {
		PlateOrientation po=PlateOrientation.STANDARD;
		if(rotatePlate)
			po=PlateOrientation.FLIP;
		Plate plate = new Plate((int)nRow,(int) nCol, po, (int)getNReplicates(), (int)getWorkingBlockSize(), this.flipGroup, this.getAddressMod(), bannedCells);
		return plate;
	}

	/**
	 * @return
	 */
	public double getWorkingBlockSize() {
		if(blockSize!=0)
			return blockSize;
		if(rotatePlate)
			return nRow;
		return nCol;
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
					
					String combined = ""+item.getValueAt(row1, (int) getSampleNameIndex())+" "+'\n'+secondTemplateTable.getValueAt(row2, (int) getSampleNameIndex());
					output.setValueAt(combined,row3, 0);
					output.setValueAt(count,row3, 1);
					count++;
					output.setValueAt("Combined_Names",0,0);
					output.setValueAt("Combined_Name_Index ",0,1);
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
	 * @return 
	 */
	private ArrayList<Plate> distributeExcelRowsToPlate(Plate plate, TableReader tableAssignment, boolean createFile) {
		
		
		
		int total=tableAssignment.getRowCount();
		
		if (colAddressColumnIndex<tableAssignment.getColumnCount()) {
			colAddressColumnIndex=tableAssignment.getColumnCount()+1;
		}
		tableAssignment.setValueAt("plate_location", 0, (int) colAddressColumnIndex);
	
		int rowCount = tableAssignment.getRowCount();
		int numberCellsNeeded = (int) ((rowCount-1)*this.getNReplicates());
		int nWells = plate.getNCol()*plate.getNRow()-this.bannedCells.size();
		if(nWells==0) {
			nWells=1;
			IssueLog.log("well list is empty");
		}
		int nPlatesNeeded = numberCellsNeeded/nWells;//number plates that can be filled completely
	
		if(nPlatesNeeded%nWells>0)
			nPlatesNeeded++;//one more plate may be needed. this plate will be not filled completely
		ArrayList<Plate> plates = new ArrayList<Plate>();
		plates.add(plate);
		for(int i=1; i<nPlatesNeeded; i++) {
			plates.add(plate.createSimilar());
		}
		if(plates.size()>=1) {
			for(int i=1; i<=plates.size(); i++) {
				plates.get(i-1).setPlateName("Plate"+i+"");
			}
		} else {
			
		}
		int plateNumber = 0;
		Plate currentPlate = plate;
		int cellIndex=1;
		for(int currentRow=1; currentRow<=total; currentRow++)try {
			for(int currentReplicate=0; currentReplicate<getNReplicates(); currentReplicate++) {
			
			
			
			
			//BasicCellAddress indexAddress = plate.getIndexAddress(i-1);
			//String plateAddressAt = indexAddress.getAddress();
			if(cellIndex-1>=plate.getPlateCells().size()||!currentPlate.hasNext()) {
				cellIndex=1;
				plateNumber++;
				if(plates.size()<=plateNumber)
					break;
				currentPlate=plates.get(plateNumber);
				//ShowMessage.showOptionalMessage("Too many samples", true, "You have to many samples for this plate size");
				//break;
			}
			if(cellIndex<1||currentPlate.getPlateCells().size()<1) {
				return plates;
			}
			
			int sampleNameIndex = (int) getSampleNameIndex();
			
			
			
			
			
			SheetAssignment sa = new SheetAssignment(currentRow, currentReplicate);
			String sheetName = tableAssignment.getSheetName(sheetIndex)+"";
			sa.setSourceSheetName(sheetName);
			
			SheetAssignment match = sa.findMatching(this.manualCells.keySet());
			PlateCell plateCell;
			if(match!=null) {
				PlateCell plateCell2 = manualCells.get(match);
				if(plateCell2!=null)
					plateCell = currentPlate.assignNextWell(plateCell2.getAddress());
				else plateCell = currentPlate.assignNextWell();
			} else 
					plateCell = currentPlate.assignNextWell();//.getPlateCells().get(cellIndex-1);
				
			plateCell.setSheetAssignment(sa);
			
			Object cellNameText = tableAssignment.getValueAt(currentRow, sampleNameIndex);
			plateCell.setShortName(cellNameText);
	
			
			
			
			String plateAddressAt = plateCell.getAddress().getAddress(this.getAddressMod());
			
			Object val = tableAssignment.getValueAt(currentRow, (int) colAddressColumnIndex);
			if(val==null)
				val="";
			String wellAddressListText=val+"";
			if(wellAddressListText.length()>0)
				wellAddressListText+=", ";
			
			wellAddressListText+=plateAddressAt;
			tableAssignment.setValueAt(wellAddressListText, currentRow, (int) colAddressColumnIndex);
			tableAssignment.setWrapTextAt(currentRow, (int) colAddressColumnIndex);
			
			int wellColumnIndex = (int) colAddressColumnIndex+3;
			int fullLocationColumnIndex = (int) colAddressColumnIndex+2;
			int plateNameColIndex = (int) colAddressColumnIndex+1;
			
			tableAssignment.setValueAt(wellAddressListText, currentRow, wellColumnIndex);
			tableAssignment.setValueAt("well", 0,wellColumnIndex);
		
				String plateNameText = currentPlate.getPlateName();
				tableAssignment.setValueAt(plateNameText, currentRow, plateNameColIndex);
				tableAssignment.setValueAt("plate_name", 0,plateNameColIndex);
				
				String fullAddressText = wellAddressListText;
				if(fullAddressText.contains(","))
					fullAddressText=fullAddressText.replace(", ", ", "+plateNameText+"-");
				fullAddressText=plateNameText+"-"+fullAddressText;
				
				tableAssignment.setValueAt("full_location", 0,fullLocationColumnIndex);
				tableAssignment.setValueAt(fullAddressText, currentRow, fullLocationColumnIndex);
				
			
				
			
			cellIndex++;
			}
		} catch (Throwable t) {
			IssueLog.log("plate location distributor failed at "+currentRow);
			IssueLog.logT(t);
		}
		
		createSampleSetupSheets(tableAssignment, plates);
		
		if(createFile) {
			saveTableWithPlateLocations(tableAssignment);
		}
		
		return plates;
	}

	/**
	 * @param tableAssignment
	 * @param plates
	 */
	public void createSampleSetupSheets(TableReader tableAssignment, ArrayList<Plate> plates) {
		/**Creates a sample setup sheet for each plate*/
		for(int i=0; i<plates.size(); i++) {
			Plate p=plates.get(i);
			String tabname = "Sample setup";
			if(plates.size()>1)
				tabname+=p.getPlateName();
			TableReader sheet2=tableAssignment.createNewSheet(tabname);
			new  ShowPlate().showPlate(sheet2, p);
			}
	}

	/** saves the table with the plate locations
	 * @param tableAssignment
	 */
	public void saveTableWithPlateLocations(TableReader tableAssignment) {
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
	
	/**returns the address modificaton*/
	public AddressModification getAddressMod() {
		return new AddressModification(rowShift, colShift);
	}

	
	
	/**responds to mouse drags on the plate with changed in the selection or a popup menu*/
	@Override
	public void itemAction(LocatedObject2D item, MouseEvent e) {
		if(item instanceof BasicGraphicalObject) {
			BasicGraphicalObject bgo=(BasicGraphicalObject) item;
			Object cell = bgo.getTag("Cell");
			
			//	
			if(e.getID()==MouseEvent.MOUSE_PRESSED&& !e.isShiftDown()) {
				
			
				
				if((cell instanceof PlateCell))
					{cellPress=(PlateCell) cell;
					selectedCells=diplay.selectCell(cellPress, cellPress);
					//diplay.selectCell(cellPress);
					comp.repaint();
					}
				else cellPress=null;
				currentDialog.repaint();
			}
			
if(e.getID()==MouseEvent.MOUSE_PRESSED&& e.isShiftDown()&&!e.isPopupTrigger()) {
				
			
				
				if((cell instanceof PlateCell))
					{PlateCell cellPress2 = (PlateCell) cell;
					diplay.selectCell(cellPress2, diplay.getSelectedCells().contains(cellPress2));
					selectedCells=diplay.getSelectedCells();
					//diplay.selectCell(cellPress);
					comp.repaint();
					}
				
				currentDialog.repaint();
			}
			
			if(e.getID()==MouseEvent.MOUSE_DRAGGED && !e.isShiftDown() ) {
				if((cell instanceof PlateCell))
					{cellDrag=(PlateCell) cell;
					if(cellPress==null)
						cellPress=cellDrag;
					selectedCells=diplay.selectCell(cellPress, cellDrag);
					comp.repaint();
					}
				else cellDrag=null;
				currentDialog.repaint();
			}
			
			if(e.getID()==MouseEvent.MOUSE_RELEASED && e.isShiftDown()) {
				//boolean answer = ShowMessage.yesOrNo("do you want to manually assign this wells location?");
				//assignManual(e);
			}
			
			if(e.getID()==MouseEvent.MOUSE_RELEASED && !e.isShiftDown()) {
				if((cell instanceof PlateCell))
					cellRelease=(PlateCell) cell;
				else cellRelease=null;
				
				
				SmartPopupJMenu ss = new SmartPopupJMenu();
		
				ss.add(new TableSetupMenuItem("96-well", 12, 8));
				ss.add(new TableSetupMenuItem("48-well", 8, 6));
				ss.add(new TableSetupMenuItem("24-well", 6, 4));
				ss.add(new TableSetupMenuItem("12-well", 4, 3));
				ss.add(new TableSetupMenuItem("6-well", 3, 2));
				ss.add(new TableSetupMenuItem("384-well", 24, 16));
				
				if(cellPress!=null &&cellRelease!=null) {
					String selectedCellRange = cellPress+" to "+cellRelease;
					////boolean answer = FileChoiceUtil.yesOrNo("do you want to select only this area of plate "+s);
					//if(!answer)
					//	return;
					
					int widthInCol =  1 + Math.abs(cellPress.getAddress().getCol()-cellRelease.getAddress().getCol());
					int heightInRow = 1 + Math.abs(cellPress.getAddress().getRow()-cellRelease.getAddress().getRow());
					
					
					int startRow = Math.min(cellPress.getAddress().getRow(), cellRelease.getAddress().getRow());
					int startCol = Math.min(cellPress.getAddress().getCol(), cellRelease.getAddress().getCol());
					
					
					
					
					TableSetupMenuItem mi = createSelectSpecifcItem(selectedCellRange, widthInCol, heightInRow, (int)this.rowShift+ startRow, (int)this.colShift+startCol, this.selectedCells);
					ss.add(mi, 0);
					
					
					mi = createSelectSpecifcItem(selectedCellRange, widthInCol, heightInRow, startRow, startCol, this.selectedCells);
					mi.actionType=TableSetupMenuItem.banCells;
					mi.setText("Exclude cells "+selectedCellRange);
					ss.add(mi);
					
					
					mi = createSelectSpecifcItem(selectedCellRange, widthInCol, heightInRow, startRow, startCol, this.selectedCells);
					mi.actionType=TableSetupMenuItem.setLocations;
					mi.setText("Assign locations "+selectedCellRange);
					ss.add(mi);
					
					mi = createSelectSpecifcItem(selectedCellRange, widthInCol, heightInRow, startRow, startCol, this.selectedCells);
					mi.actionType=TableSetupMenuItem.reset;
					mi.setText("reset plate ");
					ss.add(mi);
					
				}
				
				//if(e.isPopupTrigger())
				if(!e.isShiftDown()|| (e.isShiftDown()&&e.isPopupTrigger()))
					{
						ss.show(comp, e.getX(), e.getY());
						cellPress=null;
					}
			}
		}
		
	}

	/**reprecated
	 * @param e
	 */
	public void assignManual(MouseEvent e) {
		if (e.isShiftDown())try {
			PlateCell cellPress2 = cellPress;
			PlateCell cellRelease2 = cellRelease;
			if(e.isShiftDown()&& !cellRelease2.getAddress().matches(cellPress2.getAddress())) {
				assignCellTo(cellPress2, cellRelease2);
			}
		} catch (Exception e2) {
			IssueLog.logT(e2);
		}
	}

	/**manually assigned the location of one cells sheet address to another well
	 * @param cellClicked
	 * @param targetDestination
	 */
	public void assignCellTo(PlateCell cellClicked, PlateCell targetDestination) {
		SheetAssignment sa = cellClicked.getSheetAddress();
		if(sa!=null) {
			SheetAssignment match = sa.findMatching(manualCells.keySet());
			if(match!=null)
				manualCells.remove(match);
			this.manualCells.put(sa, targetDestination);
			
		}
	}
	
	/**manually assigned the location of one cells sheet address to another well
	 * @param cellClicked
	 * @param targetDestination
	 */
	public void assignCellTo(PlateCell cellClicked, Plate p, int drow, int rcol) {
		SheetAssignment sa = cellClicked.getSheetAddress();
		if(sa!=null) {
			SheetAssignment match = sa.findMatching(manualCells.keySet());
			if(match!=null)
				manualCells.remove(match);
			//IssueLog.log("working on cell "+cellClicked.getAddress());
			PlateCell targetDestination = p.getCellWithAddress(new BasicCellAddress(cellClicked.getAddress().getRow()+drow, cellClicked.getAddress().getCol()+rcol, null));
			//IssueLog.log("will place well into destination  "+targetDestination);
			this.manualCells.put(sa, targetDestination);
			
		}
	}

	/**
	 * @param s
	 * @param widthInCol
	 * @param heightInRow
	 * @param startRow
	 * @param startCol
	 * @param selectedCells2 
	 * @return
	 */
	public TableSetupMenuItem createSelectSpecifcItem(String s, int widthInCol, int heightInRow, int startRow,
			int startCol, ArrayList<PlateCell> selectedCells2) {
		TableSetupMenuItem mi = new TableSetupMenuItem(widthInCol, heightInRow, startRow, startCol);
		mi.setText("use Cells "+s);
		mi.cellSelection=selectedCells2;
		return mi;
	}

	
	/**Changes the settings*/
	class TableSetupMenuItem extends BasicSmartMenuItem {

		
		public ArrayList<PlateCell> cellSelection;
		public static final int banCells=1, setLocations = 2, reset = 3;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int widthInCol=12, heightInRow=8, startRow, startCol;
		private int actionType;
		
	
		
		public TableSetupMenuItem(int widthInCol, int heightInRow, int startRow, int startCol) {
			this( "custom ",widthInCol, heightInRow);
			this.startRow=startRow;
			this.startCol=startCol;
		}
		
		public TableSetupMenuItem(String st, int widthInCol, int heightInRow) {
			this.setText(st);
			this.widthInCol=widthInCol;
			this.heightInRow=heightInRow;
		}
		
	public AbstractUndoableEdit2  performAction() {
		if(actionType==banCells) {
			bannedCells.addAll(selectedCells);
			updatePlateDisplayAfterDialogChange();
			return null;
		}
		if(actionType==reset) {
			this.resetTable();
			updatePlateDisplayAfterDialogChange();
			return null;
		}
		
		if(actionType==setLocations) {
			String st = StandardDialog.getStringFromUser("Where to put these cells?", "");
			Plate plate1 = diplay.getPlate();
			
			PlateCell firstCell = selectedCells.get(0);
			for(PlateCell c: selectedCells) {
				if(c.getAddress().getRow()<=firstCell.getAddress().getRow()&&c.getAddress().getCol()<=firstCell.getAddress().getCol())
					firstCell=c;
				
			}
			
			PlateCell cell1 = plate1.getCellWithAddress(st);
			if(cell1==null) {
				IssueLog.log("The plate does not have such a location "+st);
				return null;
			}
			int dRow = cell1.getAddress().getRow()-firstCell.getAddress().getRow();
			int dCol = cell1.getAddress().getCol()-firstCell.getAddress().getCol();
			
			if(selectedCells==null||selectedCells.size()<1)
				return null;
			for(PlateCell c: selectedCells) {
				assignCellTo(c,plate1, dRow, dCol);
			}
			
			
			updatePlateDisplayAfterDialogChange();
			ShowMessage.showOptionalMessage("Manual locaitons", true, "manual locations for these samples have been saved", "you may use the reset option to remove them");
			return null;
		}
		
		 applySetup();
		 diplay.updatePlateDisplay();
		 return null;
	}
		
		/**
		 * @param widthInCol
		 * @param heightInRow
		 * @param startRow
		 * @param startCol
		 */
		public void applySetup() {
			resetTable();
			currentDialog.setNumberAndNotify("row", heightInRow);
			currentDialog.setNumberAndNotify("col", widthInCol);
			double rs = 0;//rowShift;
			double cs = 0;//colShift;
			currentDialog.setNumberAndNotify("row shift", startRow+rs);
			currentDialog.setNumberAndNotify("col shift", startCol+cs);
			
		}

		/**
		 * 
		 */
		public void resetTable() {
			bannedCells=new ArrayList<PlateCell>();
			manualCells.clear();
		}
	}
	
	

}
