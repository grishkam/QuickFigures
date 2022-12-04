/**
 * Author: Greg Mazo
 * Date Modified: Dec 4, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package dataTableActions;

import java.io.File;

import channelMerging.ChannelEntry;
import dataTableDialogs.TableReader;
import layout.RetrievableOption;
import logging.IssueLog;
import storedValueDialog.FileSlot;
import storedValueDialog.StoredValueDilaog;
import ultilInputOutput.FileChoiceUtil;

/**
 
 * 
 */
public class MakePlotFromTable extends BasicDataTableAction {

	@RetrievableOption(key = "Data File (.xlsx)", label="Input File (.xlsx)", note="Excel")
	public FileSlot templateFile=new FileSlot();
	
	@RetrievableOption(key = "sample", label="names are in col #")
	public ColumnSlotList columnsChosen=new ColumnSlotList(templateFile, new String[] {"name","x", "y"});
	
	


	/**
	 * @param f
	 */
	public MakePlotFromTable(File f, String[] cc) {
		templateFile.setFile(f);
		columnsChosen=new ColumnSlotList(templateFile, cc);
	}

	@Override
	public String getNameText() {
		// TODO Auto-generated method stub
		return "input columns";
	}
	
	public static void main(String[] args) {
		IssueLog.sytemprint=true;
		File absolutePath = FileChoiceUtil.getOpenFile();
		IssueLog.log(absolutePath.getAbsolutePath());
		new MakePlotFromTable(absolutePath, new String[] {"name","x", "y"}).showSelectionDialog();
	}
	@Override
	public void processTableAction(TableReader item, DataTableActionContext context) {
		showSelectionDialog();
	
	}

	/**
	 * 
	 */
	public void showSelectionDialog() {
		StoredValueDilaog currentDialog = new StoredValueDilaog("Select Column to use",  this, "general");
		currentDialog.setModal(true);
		currentDialog.showDialog();
	}

}
