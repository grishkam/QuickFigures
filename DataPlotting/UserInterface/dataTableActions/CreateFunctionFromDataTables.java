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
 * Date Created: Dec 17, 2022
 * Date Modified: Dec 19, 2022
 * Version: 2023.2
 */
package dataTableActions;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.JButton;

import dataTableDialogs.TableReader;
import figureFormat.DirectoryHandler;
import infoStorage.FileBasedMetaWrapper;
import layout.RetrievableOption;
import logging.IssueLog;
import messages.ShowMessage;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialogListener;
import standardDialog.strings.StringInputPanel;
import storedValueDialog.FileSlot;
import storedValueDialog.StoredValueDilaog;
import ultilInputOutput.FileChoiceUtil;

/**
 
 * 
 */
public class CreateFunctionFromDataTables extends BasicDataTableAction{

	private StoredValueDilaog currentDialog;
	String saveAsR ="make_plots";// "make_plots.R";
	@RetrievableOption(key = "Input sample setup", label="Sample setup file", note="Excel")
	public FileSlot theSampleSetup=new FileSlot(true);
	
	String supportFiles="plot_functions";
	
	//@RetrievableOption(key = "results file", label="results file", note="Excel")
	//public FileSlot results_file=new FileSlot();
	

	
	@RetrievableOption(key = "sample", label="parameters are")
	public ColumnSlotList col1=new ColumnSlotList(
			new ColumnSlot(theSampleSetup, "x_axis_column=", true),
			new ColumnSlot(theSampleSetup, "color_variable_column=", false)
			,
			new ColumnSlot(theSampleSetup, "division_column=", false)
				);
	
	@RetrievableOption(key = "excluded_based_on", label="exclude from column", category="more")
	public ColumnSlot slotexclude= new ColumnSlot(theSampleSetup, "exclude if values in", false);
	@RetrievableOption(key = "excluded", label="are equal to", category="more")
	public ColumnSelectionSlot slotExclude=new ColumnSelectionSlot(slotexclude);
	
	@RetrievableOption(key = "data_location", label="data_location", note="any")
	public FileSlot dataFile=new FileSlot(false);
	
	
	/**
	@RetrievableOption(key = "sample2", label="data parameters are")
	public ColumnSlotList col2=new ColumnSlotList(
			new ColumnSlot(results_file, "y_axis=")
				);*/


	
	
	public String form="Rscript ";
	String regex = " ";
	
	@RetrievableOption(key = "terminal_command", label="plot using", choices= {"plot_NGS.R", "plot_FACS.R", "plot_NGS.R plot_NGS("}, chooseExtra=true)
	public String functionName="plot_NGS.R";
	
	private StringInputPanel comp=new StringInputPanel("fuction", "");;
	
	
	boolean isRScriptOnly() {
		return functionName.toUpperCase().endsWith(".R");
	}
	
	/**returns true if the method used is a function from an R source file*/
	boolean isRFunctionFromSourceFile() {
		if(this.isRScriptOnly())
			return false;
		
		String[] div = functionName.split(regex);
		if(div.length!=2)
			return false;
		if(div[0].toUpperCase().endsWith(".R"))
			return true;
		
		return false;
	}
	
	String getRSource() {
		String[] div = functionName.split(regex);
		return div[0];
	}
	
	String getRFunction() {
		String[] div = functionName.split(regex);
		String string = div[1];
		if(!string.endsWith("("))
			string+="(";
		return string;
	}
	
	
	boolean isReady() {
		
		ArrayList<String> warnings=new ArrayList<String>();
		if(theSampleSetup.isEmpty())
			warnings.add("Sample setup file is required");
		//if(results_file.isEmpty())warnings.add("results file is required");
		if(warnings.size()>0)
			ShowMessage.showOptionalMessage("More items are required", false, warnings);
		return warnings.size()<1;
		
	}
	
	String buildInput() throws IOException {
		
		
		String in = "";
	
		if(this.isRScriptOnly())
			in+=form+functionName+" \"";
		if(this.isRFunctionFromSourceFile()) {
			in+= "source('"+this.getRSource()+"')"+"\n";
			in+=this.getRFunction();
		}
		boolean comma=false;
		
		if(!theSampleSetup.isEmpty()) {
			if(comma) in+=", ";
			in+= "sample_setup_file = '"+theSampleSetup.getPath()+"'";
			comma=true;
		}
		
		/**
		 * 	if(!theSampleSetup.isEmpty()) {
			in+="the_sample_setup_file='"+theSampleSetup.getPath()+"'"+"\n";
		}
		if(!results_file.isEmpty()) {
			if(comma) in+=", ";
			in="the_results_file='"+results_file.getPath()+"'"+"\n"+in;
			in+= "results_file = the_results_file";
			comma=true;
		}*/
		
		for(ColumnSlot c: col1.eachSlot) {
			if(c.isEmpty())
				continue;
			if(comma) in+=", ";
			String asString = c.getAsString();
			warnAboutColName(asString);
			in+= c.getLabel()+"'"+asString+"'";
		}
		
		if(!dataFile.isEmpty() &&dataFile.exists()) {
			in+=","+"data_location" + "='"+dataFile.getPath()+"'";
		}
		
		if(!slotexclude.isEmpty()&&!this.slotExclude.isEmpty()) {
			String condition_check = "!=";
			ArrayList<String> selected = slotExclude.getAllSelected();
			if(selected.size()>0 ) {
				in+=", filter_rules=expr(";
				String asString = slotexclude.getAsString();
				warnAboutColName(asString);
				if(selected.size()==1) {
					
					in+=asString+condition_check+"'"+selected.get(0)+"')";
				}
				
				if(selected.size()>1) {
					
					
					for(int i=0; i<selected.size(); i++) {
						in+=(i==0?"":"&")+""+asString+condition_check+"'"+selected.get(i)+"'";
					}
					
							in+=")";
				}
			
			}
		}
		if(this.isRScriptOnly())
			in+="\"";
		else if(this.isRFunctionFromSourceFile()) {
			in+=")";
		}
		return ""+in;
		
	}

	/**
	 * @param asString
	 */
	public void warnAboutColName(String asString) {
		if(asString.contains(" ")) {
			IssueLog.log("Warning: column names should not have spaces in an R data table, please rename: "+asString);
		}
	}

	public void processTableAction(TableReader item, DataTableActionContext context) {
		
		currentDialog = new StoredValueDilaog("Create a plot from sample setup and results",  this, "general");
		currentDialog.setHideOK(true);
		comp = new StringInputPanel("function", "", 8, 32);
	
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx=5;
		gc.gridy=1;
		JButton spreadsheet=new JButton("Create Script"); {spreadsheet.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				runActionPlate(comp.getTextFromField(), getScriptSaveName());
				
			}});}
		currentDialog.addButton(spreadsheet);
		
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


	/**called when user presses a button
	 * @param textFromField
	 * @param string
	 */
	protected void runActionPlate(String textFromField, String string) {
		try {
			
			if(!this.isReady())
				return;
			String parent = theSampleSetup.getFile().getParent();
			File f = FileChoiceUtil.getSaveFile(parent, string);
			String parent2 = f.getParent();
			
			String supportFolder = DirectoryHandler.getDefaultHandler().getFigureFolderPath()+"/"+this.supportFiles;
			if(new File(supportFolder).exists()) {
				copyRfilesToWorkingDirectory(supportFolder, parent2);
			} else {
				IssueLog.log("please paste any R scripts for function into folder "+supportFolder);
				new File(supportFolder).mkdirs();
			}
			FileBasedMetaWrapper.stringToFile(textFromField, f);
			
		
			
			if (IssueLog.isWindows()) {
				IssueLog.log("on windows, you will decide how to run the script yourself");
				Desktop.getDesktop().open(f);
				//String[] args = new String[] {"Rscript", f.getAbsolutePath()};//, "\""+parent+"\""};
				//Process proc = new ProcessBuilder(args).start();
			} else {
				IssueLog.log("on mac ");
				openTerminalAt(parent, f.getName());
				if(this.isRFunctionFromSourceFile())
					ShowMessage.showOptionalMessage("my option", true, "in the terminal type '"+form +""+f.getName()+"'");
				else {
					
				}
			}
			
			//Desktop.getDesktop().open(f);
		} catch (Exception e) {
			IssueLog.logT(e);
		}
	}

	/**
	 * @param rFolder
	 * @param parent
	 */
	private void copyRfilesToWorkingDirectory(String rFolder, String parent) {
		File[] allFiles = new File(rFolder).listFiles();
		for(File f: allFiles) {
		
			
			String name2 = parent+"/"+f.getName();
		
			
			File f2 = new File(name2);
			if(f2.exists()) {
				IssueLog.log("working directory already has file: " +f.getName());
				
			} else try {
				
				IssueLog.log("making copy of file");
				IssueLog.log(f);
				IssueLog.log(name2);
				copyFile(f, f2);
			} catch (Throwable t) {
				IssueLog.logT(t);
			}
			
		}
	}

	/**
	 * @param f
	 * @param f2
	 * @throws IOException 
	 */
	private void copyFile(File f, File f2) throws IOException {
		 InputStream in = new FileInputStream(f);
         OutputStream out = new FileOutputStream(f2);

         // Copy the bits from instream to outstream
         byte[] buf = new byte[1024];
         int len;
         while ((len = in.read(buf)) > 0) {
             out.write(buf, 0, len);
         }
         in.close();
         out.close();
		
	}

	/**
	 * @param f
	 * @param string 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void openTerminalAt(String f, String string) throws IOException, InterruptedException {
		
		if(f.contains(" "))
		{
			ShowMessage.showOptionalMessage("folder path has spaces. ");
			
			IssueLog.log(f);
			return ;
			}
		IssueLog.log("will open terminal window");
		IssueLog.log("you may run the script you just created by typing 'sh "+string+"'");
		Process p = Runtime.getRuntime().exec("/usr/bin/open -a Terminal "+f);
		 p.waitFor();
	}

	/**
	 * 
	 */
	private void updatePlateDisplayAfterDialogChange() {
		try {
			comp.getTextComponent().setText(buildInput());
			currentDialog.pack();
			this.currentDialog.repaint();
		} catch (Exception e) {
			IssueLog.logT(e);
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		IssueLog.sytemprint=true;
		new CreateFunctionFromDataTables().processTableAction(null, null);
		
		//Runtime rt = Runtime.getRuntime();
		//Process p = rt.exec("cmd /c ls");
		
	}
	
	@Override
	public String getNameText() {
		
		return "Create plot script";
	}

	/**
	 * @return
	 */
	public String getScriptSaveName() {
		if(this.isRFunctionFromSourceFile())
			return saveAsR+".R";
		return saveAsR;
	}
	
}
