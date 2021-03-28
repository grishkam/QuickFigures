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
 * Date Modified: Mar 28, 2021
 * Version: 2021.1
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import ij.IJ;
import ij.plugin.PlugIn;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

/**This class is used by imageJ to make the toolbars appear*/
public class Plot_ToolInstaller implements PlugIn {
	static boolean firstRun=true;

	@Override
	public void run(String arg0) {
		IssueLog.log("QuickFigures: Plot package is instaling");
		if (firstRun) {
			onFirstRun();
			firstRun=false;
			new Toolset_Runner().run("Object Tools");
		}
		

	}
	
	
	
	private void onFirstRun() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		StartWithPlotPackage.install();
		try {
			if (true)
					checkForAutoRun() ;
			} catch (Throwable t) {
			IssueLog.logT(t);
		}
		
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
		return false;
	}
	
	static boolean hasAutoRunInIJMfile() throws Exception {
		String macrosPath = IJ.getDirectory("macros");
		
		if (macrosPath!=null) {
			
			File f=new File(macrosStartupFilePathIJM());
			
			
			if (!f.exists()) return false;
			boolean hasAutoRun=fileContainsString(f, "Install Plot Package", null);
			
			    return hasAutoRun;
	}
		return false;
	}
		
		
	public static void checkForAutoRun() throws Exception {
		
		//IssueLog.log("path is");
		
			    
			    if (!hasAutoRun() ) {
			    //	IssueLog.log("autorun not found");
			    	boolean start =   FileChoiceUtil.yesOrNo("Start Plot Package at ImageJ startup"); 
			    	
			    	if (start) try {
			    		
			    		File mFile = new File( macrosStartupFilePathIJM());
			    		if (!mFile.exists()) { 
			    			FileChoiceUtil.writeLocalStringToFile(macrosStartupFilePathIJM(), "AutoRunPlot.txt", Plot_ToolInstaller.class.getClassLoader(), false);
			    		}
			    			else
			    		if (mFile.exists()) {
			    			FileChoiceUtil.addLocalStringToFile(macrosStartupFilePathIJM(), "AutoRunPlot.txt", Plot_ToolInstaller.class.getClassLoader());
			    		}
			    		
			    	
			    	} catch (Exception e) {IssueLog.logT(e);}
			    }
		
	}
		
	
}

	
	
	

