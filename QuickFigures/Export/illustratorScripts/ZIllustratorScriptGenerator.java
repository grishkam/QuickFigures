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
 * Date Modified: Nov 28, 2021
 * Version: 2021.2
 */
package illustratorScripts;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import figureFormat.DirectoryHandler;
import logging.IssueLog;
import messages.ShowMessage;
import photoshopScripts.AdobeScriptGenerator;

/**this class generates text that can be run in an adobe illustrator java script*/
public class ZIllustratorScriptGenerator {
	

		
	public static ZIllustratorScriptGenerator instance=new ZIllustratorScriptGenerator();
	double x0=0;
	double y0=0;
	double scale=1;
	
	public double getScale() {
		return scale;
	}
	public void setScale(double d) {scale=d;}
	
	public String accumulatedscrip="";
	private String pathOfImages="temp";
	boolean deleteonExit=true;
	
	public void setZero(int x, int y) {
		x0=x*scale; y0=y*scale;
	//	IJ.log("Zero is "+x+", "+y);
	}
	
	/**Generates a random integer*/
	static int createRandom() {
		return ((int)(10000*Math.random()));
	}
	
	/**Adds lines of code the the accumulated javascript*/
	void addScript(String... arg) {
		for (String st: arg) {
			accumulatedscrip+='\n'+st;
		}
	}

	
	/**saves the string containing javascript code and opens it.
	 * resets the accumulated script*/
	public void execute() {
		savejsxAndRun(accumulatedscrip, DirectoryHandler.getDefaultHandler().getFigureFolderPath()+"/"+AdobeScriptGenerator.outputFileSubPath+AdobeScriptGenerator.outputFile);
		accumulatedscrip="";
	}
	public  static void savejsxAndRun(String javascript, String directoryJSX){
		IssueLog.log("File inside "+directoryJSX);
		File file = new File(directoryJSX);
		file.getParentFile().mkdirs();//creates the parent directories
		saveString("#target illustrator"+'\n'+javascript, directoryJSX, false);
		try {
		Desktop.getDesktop().open(file);} catch (IOException e) {
			ShowMessage.showOptionalMessage("Failed to find Adobe Illustrator", false, "Did not run script in Adobe Illustrator", "Make sure you have Illustrator recently installed and set as the default software for .jsx files");
			IssueLog.logT(e);
		}
	}

	/**returns the path for saving images files temporarily*/
	public String getPathOfImages() {
		File f=new File(pathOfImages);
		if (!f.exists()) {
			f.mkdirs();
		}
		
		return pathOfImages;
	}
	public void setPathOfImages(String pathOfImages) {
		this.pathOfImages = pathOfImages;
	}

	boolean invertvertical=true;
	
	
	/**saves a string*/
	 public static String saveString(String string, String path, boolean append) {
	        
	        try {
	            BufferedWriter out = new BufferedWriter(new FileWriter(path, append));
	            out.write(string);
	            out.close();
	        } catch (Exception e) {
	            IssueLog.logT(e);
	        }
	        return null;
	    }
		
	}
	



