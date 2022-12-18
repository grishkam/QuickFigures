/**
 * Author: Greg Mazo
 * Date Modified: Dec 17, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package dataTableActions;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JLabel;

import channelMerging.ChannelEntry;
import dataTableDialogs.TableReader;
import layout.RetrievableOption;
import logging.IssueLog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialogListener;
import standardDialog.graphics.GraphicComponent;
import standardDialog.strings.StringInputPanel;
import storedValueDialog.FileSlot;
import storedValueDialog.StoredValueDilaog;

/**
 
 * 
 */
public class CreateFunctionFromDataTables {

	private StoredValueDilaog currentDialog;
	
	@RetrievableOption(key = "Input sample setup", label="Sample setup file", note="Excel")
	public FileSlot theSampleSetup=new FileSlot();
	
	@RetrievableOption(key = "results file", label="results file", note="Excel")
	public FileSlot results_file=new FileSlot();
	
	String functionName="createPlotsFrom";
	
	@RetrievableOption(key = "sample", label="parameters are")
	public ColumnSlotList col1=new ColumnSlotList(
			new ColumnSlot(theSampleSetup, "color=")
			,new ColumnSlot(theSampleSetup, "xAxis=")
				);

	private StringInputPanel comp=new StringInputPanel("fuction", "");;
	
	String buildInput() {
		
		
		String in = "";
		if(!theSampleSetup.isEmpty()) {
			in+="the_sample_setup_file='"+theSampleSetup.getFile()+"'"+"\n";
		}
		
		in+=functionName+"(";
		boolean comma=false;
		
		if(!theSampleSetup.isEmpty()) {
			if(comma) in+=", ";
			in+= "sample_setup_file = the_sample_setup_file";
			comma=true;
		}
		
		if(!results_file.isEmpty()) {
			if(comma) in+=", ";
			in="the_results_file='"+results_file.getFile()+"'"+"\n"+in;
			in+= "results_file = the_results_file";
			comma=true;
		}
		
		for(ColumnSlot c: col1.eachSlot) {
			if(c.isEmpty())
				continue;
			if(comma) in+=", ";
			in+= c.getLabel()+"'"+c.getAsString()+"'";
		}
		
		
		in+=")";
		return in;
		
	}

	public void processTableAction(TableReader item, DataTableActionContext context) {
		
		currentDialog = new StoredValueDilaog("Distribute rows to a plate setup",  this, "general");
		  comp = new StringInputPanel("function", "", 4, 32);
	
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx=5;
		gc.gridy=1;
		
		
		
		currentDialog.add("output", comp);
		updatePlateDisplayAfterDialogChange();
		currentDialog.addDialogListener(new StandardDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				
				updatePlateDisplayAfterDialogChange();
				
			}

			});
		
		currentDialog.showDialog();
		if(currentDialog.wasCanceled())
			return;
		
		
		
	}


	/**
	 * 
	 */
	private void updatePlateDisplayAfterDialogChange() {
		comp.getTextComponent().setText(buildInput());
		currentDialog.pack();
		this.currentDialog.repaint();
		
	}
	
	public static void main(String[] args) throws IOException {
		IssueLog.sytemprint=true;
		new CreateFunctionFromDataTables().processTableAction(null, null);
		
		//Runtime rt = Runtime.getRuntime();
		//Process p = rt.exec("cmd /c ls");
		
	}
	
}
