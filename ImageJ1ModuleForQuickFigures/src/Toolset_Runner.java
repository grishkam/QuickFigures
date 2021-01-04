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
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;
import figureFormat.DirectoryHandler;
import ij.IJ;
import ij.plugin.PlugIn;
import imageDisplayApp.KeyDownTracker;
import includedToolbars.ActionToolset1;
import includedToolbars.ActionToolset2;
import includedToolbars.LayoutToolSet;
import includedToolbars.ObjectToolset1;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

/**This class is used by imageJ to make the toolbars appear*/
public class Toolset_Runner implements PlugIn {
	static boolean firstRun=true;

	@Override
	public void run(String arg0) {
		if (firstRun) {
			onFirstRun();
			firstRun=false;
		}
		
		
		
		if (arg0.equals("file list")) {
			///new FileListGui().run("gui");
		}
		if (arg0.equals("Position Action Tools")) {
			new ActionToolset1().run("");
			new ActionToolset2().run("");
		}
		
		
		if (arg0.equals("LayoutToolSet")) {
			LayoutToolSet tsr = new LayoutToolSet();
			
			tsr.run("");
			
		}
		
		if (arg0.equals("Specify...")) {
			IJ.doCommand("Specify...");
		}
		
		if (arg0.equals("FigureWizApp")||arg0.equals("Object Tools")) {
			
			ObjectToolset1 tsr = new ObjectToolset1();
			
			tsr.run("");
			new ActionToolset1().run("");
		}

	}
	
	
	
	private void onFirstRun() {
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
		
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		try {
			if (!DirectoryHandler.getDefaultHandler().defaultTemplateExits())
					checkForAutoRun() ;
			} catch (Throwable t) {
			IssueLog.logT(t);
		}
		Toolkit.getDefaultToolkit().addAWTEventListener(new KeyDownTracker(), AWTEvent.KEY_EVENT_MASK);
		
	}
	
	
	static String macrosStartupFilePath() {return IJ.getDirectory("macros")+"StartupMacros.txt";}
	static String macrosStartupFilePathIJM() {return IJ.getDirectory("macros")+"RunAtStartup.ijm";}
	
	public static boolean fileContainsString(File f, String contained, String nonContained) throws FileNotFoundException {
		if (!f.exists()) return false;
		
		Scanner scanner = new Scanner(f);
		 boolean hasString=false;
		    while (scanner.hasNextLine()) {
		        String line = scanner.nextLine();
		      
		        if(line.contains(contained) && (nonContained==null||!line.contains(nonContained))) { 
		            hasString=true;
		        }
		    	}
		    scanner.close();
		    
		    return hasString;
	}
	
	static boolean hasAutoRun() throws Exception {
		return hasAutoRunInTXTfile()||hasAutoRunInIJMfile();
	}
	
	static boolean hasAutoRunInTXTfile() throws Exception {
		String macrosPath = IJ.getDirectory("macros");
		
		if (macrosPath!=null) {
			
			File f=new File(macrosStartupFilePath());
			if (!f.exists()) return false;
			boolean hasAutoRun=fileContainsString(f, "AutoRun", "//");
			
			    return hasAutoRun;
	}
		else return false;
		
	}
	
	static boolean hasAutoRunInIJMfile() throws Exception {
		String macrosPath = IJ.getDirectory("macros");
		
		if (macrosPath!=null) {
			
			File f=new File(macrosStartupFilePathIJM());
			
			
			if (!f.exists()) return false;
			boolean hasAutoRun=fileContainsString(f, "Layout Tools", null);
			
			    return hasAutoRun;
	}else
		return false;
	}
		
		
	public static void checkForAutoRun() throws Exception {
		
			    
			    if (!hasAutoRun() ) {
			 
			    	boolean start =   FileChoiceUtil.yesOrNo("Open QuickFigures toolbars whenever ImageJ starts?"); 
			    	IssueLog.log("Will write to startup macros file");
			    	if (start) try {
			    		String st="";
			    		if (FileChoiceUtil.yesOrNo("Do you want to automatically color channels and add slice labels after opening ZVI files?" )) {
			    			st="Z";
			    		}
			    		
			    		File mFile = new File( macrosStartupFilePathIJM());
			    		if (!mFile.exists()) { 
			    			FileChoiceUtil.writeLocalStringToFile(macrosStartupFilePathIJM(), st+"AutoRunMacro2.txt", Toolset_Runner.class.getClassLoader(), false);
			    		}
			    			else
			    		if (mFile.exists()) {
			    			FileChoiceUtil.addLocalStringToFile(macrosStartupFilePathIJM(), st+"AutoRunMacro2.txt", Toolset_Runner.class.getClassLoader());
			    		}
			    		else FileChoiceUtil.addLocalStringToFile(macrosStartupFilePath(), st+"AutoRunMacro.txt", Toolset_Runner.class.getClassLoader());
			    	
			    	
			    	} catch (Exception e) {IssueLog.logT(e);}
			    }
		
	}
		
	
	

	
	
	
}
